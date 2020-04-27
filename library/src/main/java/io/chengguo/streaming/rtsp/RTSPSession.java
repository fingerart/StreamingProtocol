package io.chengguo.streaming.rtsp;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import io.chengguo.streaming.rtcp.RTCPResolver;
import io.chengguo.streaming.rtcp.ReceiverReport;
import io.chengguo.streaming.rtcp.SenderReport;
import io.chengguo.streaming.rtcp.SourceDescription;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.Header;
import io.chengguo.streaming.rtsp.header.RangeHeader;
import io.chengguo.streaming.rtsp.header.SessionHeader;
import io.chengguo.streaming.rtsp.header.TransportHeader;
import io.chengguo.streaming.rtsp.header.UserAgentHeader;
import io.chengguo.streaming.rtsp.sdp.H264SDP;
import io.chengguo.streaming.transport.TransportImpl;
import io.chengguo.streaming.transport.TransportMethod;
import io.chengguo.streaming.utils.Utils;

import static io.chengguo.streaming.rtsp.header.TransportHeader.Specifier.TCP;

/**
 * RTSP session
 * Created by fingerart on 2018-09-08.
 */
public class RTSPSession {

    private static final String TAG = RTSPSession.class.getSimpleName();

    private String host;
    private int port;
    private TransportMethod method;
    private TransportImpl transport;
    private String session;
    private String baseUri;
    private AtomicInteger sequence = new AtomicInteger();
    private HashMap<Integer, Request> requestList = new HashMap<>();
    private IResolver.IResolverCallback<RtpPacket> mRtpResolverCallback;
    private ArrayList<IInterceptor> mInterceptors = new ArrayList();

    public RTSPSession(String host, int port, TransportMethod method) {
        this.host = host;
        this.port = port;
        this.method = method;
        transport = this.method.createTransport(this.host, this.port, 3000);
        transport.setTransportListener(new RTSPTransportListenerWrapper(createWrapRtspResolver(), createWrapRtpBridge(), createWrapRtcpResolver()));
    }

    @NonNull
    private IResolver.IResolverCallback<Response> createWrapRtspResolver() {
        return new IResolver.IResolverCallback<Response>() {
            @Override
            public void onResolve(Response response) {
                //获取暂存中的Request
                CSeqHeader cSeqHeader = response.getHeader(CSeqHeader.DEFAULT_NAME);
                if (cSeqHeader != null) {
                    Request request = requestList.get(cSeqHeader.getRawValue());
                    response.setRequest(request);
                    requestList.remove(cSeqHeader.getRawValue());
                }
                SessionHeader sessionHeader = response.getHeader(SessionHeader.DEFAULT_NAME);
                if (sessionHeader != null) {
                    session = sessionHeader.getSession();
                }
                for (IInterceptor Interceptor : mInterceptors) {
                    Interceptor.onResponse(response);
                }
                if (response.getLine().isSuccessful()) {
                    Request nextRequest = makeNextRequest(response);
                    try {
                        for (IInterceptor Interceptor : mInterceptors) {
                            nextRequest = Interceptor.onSend(nextRequest, response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nextRequest != null && nextRequest.getLine().getMethod() != null) {
                        transport.send(nextRequest);
                    }
                }
            }
        };
    }

    @NonNull
    private IResolver.IResolverCallback<RtpPacket> createWrapRtpBridge() {
        return new IResolver.IResolverCallback<RtpPacket>() {
            @Override
            public void onResolve(RtpPacket rtpPacket) {
                mRtpResolverCallback.onResolve(rtpPacket);
            }
        };
    }

    @NonNull
    private RTCPResolver.RTCPResolverListener createWrapRtcpResolver() {
        return new RTCPResolver.RTCPResolverListener() {
            @Override
            public void onSenderReport(SenderReport senderReport) {
                System.out.println(senderReport);
                RtspPacket rtspPacket = new RtspPacket(0x01, new IMessage[]{new ReceiverReport(8888)});
                transport.send(rtspPacket);
            }

            @Override
            public void onReceiverReport(ReceiverReport receiverReport) {
                System.out.println("RTSPSession#onReceiverReport: " + "receiverReport = [" + receiverReport + "]");
            }

            @Override
            public void onSourceDescription(SourceDescription sourceDescription) {
                System.out.println("RTSPSession#onSourceDescription: " + "sourceDescription = [" + sourceDescription + "]");
            }
        };
    }

    private Request makeNextRequest(Response response) {
        Request.Builder builder = new Request.Builder();
        Request prevRequest = response.getRequest();
        switch (prevRequest.getLine().getMethod()) {
            case OPTIONS:
                builder.method(Method.DESCRIBE).uri(prevRequest.getLine().getUri());
                break;
            case DESCRIBE:
                Header<String> base = response.getHeader("Content-Base");
                baseUri = base.getRawValue();
                H264SDP sdp = new H264SDP();
                sdp.from(response.getBody().toString());
                TransportHeader header = new TransportHeader.Builder()
                        .specifier(TCP)
                        .broadcastType(TransportHeader.BroadcastType.unicast)
                        .build();
                builder.method(Method.SETUP).uri(URI.create(baseUri + sdp.getMediaControl())).addHeader(header);
                break;
            case SETUP:
                builder.method(Method.PLAY).addHeader(new RangeHeader(0)).uri(baseUri);
                break;
            case PLAY:
            default:
                return null;//Not replay
        }
        return builder.build();
    }

    public void connect() {
        transport.connect();
    }

    /**
     * 设置RTP解析回调
     *
     * @param rtpResolverCallback
     */
    public void setRTPResolverCallback(IResolver.IResolverCallback<RtpPacket> rtpResolverCallback) {
        mRtpResolverCallback = rtpResolverCallback;
    }

    /**
     * 发送RTSP请求
     *
     * @param request
     */
    public void send(Request request) {
        int cseq = sequence.incrementAndGet();
        request.addHeader(new CSeqHeader(cseq));
        if (!Utils.isEmpty(session)) {
            request.addHeader(new SessionHeader(session, 0));
        }
        request.addHeader(new UserAgentHeader("ChengGuo Live"));
        StringBuilder sb = new StringBuilder();
        sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>").append(request.getLine().getMethod()).append("\r\n")
                .append(request.toString())
                .append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ").append(request.getLine().getMethod());
        System.out.println(sb);
        //暂存Request
        requestList.put(cseq, request);

        for (IInterceptor Interceptor : mInterceptors) {
            request = Interceptor.onSend(request, null);
        }

        //发送请求
        transport.send(request);
    }

    /**
     * 是否链接
     *
     * @return
     */
    public boolean isConnected() {
        return transport.isConnected();
    }

    /**
     * 断开链接
     */
    public void disconnect() {
        if (isConnected()) {
            transport.disconnect();
        }
    }

    public void addInterceptor(IInterceptor Interceptor) {
        if (Interceptor != null && !mInterceptors.contains(Interceptor)) {
            mInterceptors.add(Interceptor);
        }
    }

    public void removeInterceptor(IInterceptor Interceptor) {
        if (Interceptor != null) {
            mInterceptors.remove(Interceptor);
        }
    }
}