package io.chengguo.streaming.rtsp;

import android.view.Surface;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

import io.chengguo.streaming.MediaStream;
import io.chengguo.streaming.rtcp.RTCPResolver;
import io.chengguo.streaming.rtcp.ReceiverReport;
import io.chengguo.streaming.rtcp.SenderReport;
import io.chengguo.streaming.rtcp.SourceDescription;
import io.chengguo.streaming.rtp.RTPResolver;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtp.h264.H264MediaStream;
import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.Header;
import io.chengguo.streaming.rtsp.header.RangeHeader;
import io.chengguo.streaming.rtsp.header.SessionHeader;
import io.chengguo.streaming.rtsp.header.TransportHeader;
import io.chengguo.streaming.rtsp.header.UserAgentHeader;
import io.chengguo.streaming.rtsp.sdp.SDP;
import io.chengguo.streaming.transport.IMessage;
import io.chengguo.streaming.transport.SendCallback;
import io.chengguo.streaming.transport.TransportImpl;
import io.chengguo.streaming.transport.TransportMethod;
import io.chengguo.streaming.utils.L;
import io.chengguo.streaming.utils.Utils;

import static io.chengguo.streaming.rtsp.header.TransportHeader.Specifier.TCP;

/**
 * RTSP session
 * Created by fingerart on 2018-09-08.
 */
public class RTSPSession {

    private static final String TAG = RTSPSession.class.getSimpleName();

    @RtspState
    private int mState = RtspState.UNSTART;
    public String mHost;
    public int mPort;
    private TransportMethod method;
    private TransportImpl mTransport;
    private String mSession;
    private String mBaseUri;
    private AtomicInteger mSequence = new AtomicInteger();
    private HashMap<Integer, Request> mRequestList = new HashMap<>();
    private ArrayList<IInterceptor> mInterceptors = new ArrayList<>();
    private ISessionStateObserver mSessionStateObserver;
    private MediaStream mMediaStream;
    private Surface mSurface;

    public RTSPSession(String host, int port, int timeout, TransportMethod method) {
        this(host, port, timeout, method, null);
    }

    public RTSPSession(String host, int port, int timeout, TransportMethod method, Surface surface) {
        this.mHost = host;
        this.mPort = port;
        this.method = method;
        mSurface = surface;
        mTransport = this.method.createTransport(this.mHost, this.mPort, timeout);
        mTransport.setTransportListener(new RTSPTransportListenerWrapper(
                createRTSPResolver(),
                createRTPResolver(),
                createRTCPResolver()
        ));
    }

    @NonNull
    private IResolver.IResolverCallback<Response> createRTSPResolver() {
        return new IResolver.IResolverCallback<Response>() {
            @Override
            public void onResolve(Response response) {
                //获取暂存中的Request
                CSeqHeader cSeqHeader = response.getHeader(CSeqHeader.DEFAULT_NAME);
                if (cSeqHeader != null) {
                    Request request = mRequestList.get(cSeqHeader.getRawValue());
                    response.setRequest(request);
                    mRequestList.remove(cSeqHeader.getRawValue());
                }
                SessionHeader sessionHeader = response.getHeader(SessionHeader.DEFAULT_NAME);
                if (sessionHeader != null) {
                    mSession = sessionHeader.getSession();
                }
                for (IInterceptor interceptor : mInterceptors) {
                    response = interceptor.onResponse(response);
                }
                if (response != null && response.getLine().isSuccessful()) {
                    try {
                        Request nextRequest = makeNextRequest(response);
                        if (nextRequest != null && nextRequest.getLine().getMethod() != null) {
                            send(nextRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (mSessionStateObserver != null) {
                            mSessionStateObserver.onError(e);
                        }
                    }
                }
            }
        };
    }

    @NonNull
    private IResolver.IResolverCallback<RtpPacket> createRTPResolver() {
        return new IResolver.IResolverCallback<RtpPacket>() {
            @Override
            public void onResolve(RtpPacket rtpPacket) {
                mMediaStream.feedPacket(rtpPacket);
            }
        };
    }

    @NonNull
    private RTCPResolver.RTCPResolverListener createRTCPResolver() {
        return new RTCPResolver.RTCPResolverListener() {
            @Override
            public void onSenderReport(SenderReport senderReport) {
                System.out.println("RTSPSession.onSenderReport: " + "senderReport = [" + senderReport + "]");
                RtspPacket rtspPacket = new RtspPacket(0x01, new IMessage[]{new ReceiverReport(8888)});
                mTransport.send(rtspPacket);
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

    private Request makeNextRequest(Response response) throws Exception {
        Request.Builder builder = new Request.Builder();
        Request prevRequest = response.getRequest();
        switch (prevRequest.getLine().getMethod()) {
            case OPTIONS://1. next is describe
                mState = RtspState.OPTIONS;
                builder.method(Method.DESCRIBE).uri(prevRequest.getLine().getUri());
                break;
            case DESCRIBE://2. next is setup
                mState = RtspState.DESCRIBE;
                Header<String> base = response.getHeader("Content-Base");
                mBaseUri = base.getRawValue();
                SDP sdp = SDP.parse(response.getBody().toString());
                mMediaStream = new H264MediaStream(sdp, mSurface);
                mMediaStream.prepare();
                TransportHeader header = new TransportHeader.Builder()
                        .specifier(TCP)
                        .broadcastType(TransportHeader.BroadcastType.unicast)
                        .build();
                List<SDP.MediaDescription> md = sdp.getMediaDescriptions();
                builder.method(Method.SETUP).uri(URI.create(mBaseUri + md.get(0).mediaControl)).addHeader(header);
                break;
            case SETUP://3. next is play
                mState = RtspState.SETUP;
                builder.method(Method.PLAY).addHeader(new RangeHeader(0)).uri(mBaseUri);
                break;
            case PLAY://4. not replay
                mState = RtspState.PLAY;
                return null;
            case TEARDOWN:
                mState = RtspState.TEARDOWN;
                return null;
            case PAUSE:
                mState = RtspState.PAUSE;
                return null;
            case RECORD:
            default:
                return null;
        }
        return builder.build();
    }

    public void connect() {
        mTransport.connect();
    }

    public void connect(TransportImpl.ConnectCallback callback) {
        mTransport.connect(callback);
    }

    public boolean isNormal() {
        return mState == RtspState.PAUSE || mState == RtspState.PLAY;
    }

    public boolean isPause() {
        return mState == RtspState.PAUSE;
    }

    public boolean isPlay() {
        return mState == RtspState.PLAY;
    }

    /**
     * 发送RTSP请求
     *
     * @param request
     */
    public void send(Request request) {
        send(request, null);
    }

    public void send(Request request, SendCallback sendCallback) {
        int cseq = mSequence.incrementAndGet();
        request.addHeader(new CSeqHeader(cseq));
        if (!Utils.isEmpty(mSession)) {
            request.addHeader(new SessionHeader(mSession, 0));
        }
        request.addHeader(new UserAgentHeader("ChengGuo Live"));
        if (request.getLine().getUri() == null && !Utils.isEmpty(mBaseUri)) {
            request.getLine().setUri(URI.create(mBaseUri));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>").append(request.getLine().getMethod()).append("\r\n")
                .append(request.toString())
                .append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ").append(request.getLine().getMethod());
        L.d(sb.toString());

        for (IInterceptor Interceptor : mInterceptors) {
            request = Interceptor.onSend(request, null);
        }

        //暂存Request
        mRequestList.put(cseq, request);

        //Teardown 清理资源
        if (request.getLine().getMethod() == Method.TEARDOWN) {
            mSession = null;
            mBaseUri = null;
        }

        //发送请求
        mTransport.send(request, sendCallback);
    }

    /**
     * 是否链接
     *
     * @return
     */
    public boolean isConnected() {
        return mTransport.isConnected();
    }

    /**
     * 断开链接
     */
    public void disconnect() {
        if (isConnected()) {
            mTransport.disconnect();
        }
    }

    public void addInterceptor(IInterceptor interceptor) {
        if (interceptor != null && !mInterceptors.contains(interceptor)) {
            mInterceptors.add(interceptor);
        }
    }

    public void removeInterceptor(IInterceptor Interceptor) {
        if (Interceptor != null) {
            mInterceptors.remove(Interceptor);
        }
    }

    public void setStateObserver(ISessionStateObserver sessionStateObserver) {
        mSessionStateObserver = sessionStateObserver;
    }

    /**
     * TransportListener Wrapper
     */
    public class RTSPTransportListenerWrapper implements io.chengguo.streaming.transport.ITransportListener {

        private final RTSPResolver mRTSPResolver;
        private final RTPResolver mRTPResolver;
        private final RTCPResolver mRTCPResolver;

        public RTSPTransportListenerWrapper(IResolver.IResolverCallback<Response> rtspResolver, IResolver.IResolverCallback<RtpPacket> rtpPacketIResolverCallback, RTCPResolver.RTCPResolverListener rtcpResolver) {
            mRTSPResolver = new RTSPResolver(rtspResolver);
            mRTPResolver = new RTPResolver(rtpPacketIResolverCallback);
            mRTCPResolver = new RTCPResolver(rtcpResolver);
        }

        @Override
        public void onConnected(DataInputStream in) throws IOException {
            if (mSessionStateObserver != null) {
                mSessionStateObserver.onConnectChanged(ISessionStateObserver.SESSION_STATE_CONNECTED);
            }
            int firstByte;
            while ((firstByte = in.readUnsignedByte()) > 0) {
                L.d("First Byte: " + firstByte);
                //'$' beginning is the RTP and RTCP
                if (firstByte == 36) {//0x24
                    int secondByte = in.readUnsignedByte();
                    //channel is rtp
                    if (secondByte == 0) {
                        int rtpLength = in.readUnsignedShort();
                        mRTPResolver.resolve(in, rtpLength);
                    } else if (secondByte == 1) { //channel is rtcp
                        int rtcpLength = in.readUnsignedShort();
                        mRTCPResolver.resolve(in, rtcpLength);
                    }
                } else {//RTSP
                    mRTSPResolver.resolve(in, firstByte);
                }
            }
        }

        @Override
        public void onConnectChanged(int state, Throwable throwable) {
            switch (state) {
                case STATE_CONNECT_FAILURE:
                case STATE_DISCONNECTED:
                    mSessionStateObserver.onConnectChanged(ISessionStateObserver.SESSION_STATE_DISCONNECTED);
            }
        }
    }
}