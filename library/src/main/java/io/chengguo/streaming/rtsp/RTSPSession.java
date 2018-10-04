package io.chengguo.streaming.rtsp;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.chengguo.streaming.rtcp.IReport;
import io.chengguo.streaming.rtcp.RTCPResolver;
import io.chengguo.streaming.rtp.RTPResolver;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.SessionHeader;
import io.chengguo.streaming.rtsp.header.UserAgentHeader;
import io.chengguo.streaming.transport.ITransport;
import io.chengguo.streaming.transport.TransportMethod;
import io.chengguo.streaming.utils.Utils;

/**
 * Created by fingerart on 2018-09-08.
 */
public class RTSPSession {

    private String target;
    private int port;
    private TransportMethod method;
    private ITransport transport;
    private String session;
    private AtomicInteger sequence = new AtomicInteger();
    private HashMap<Integer, Request> requestList = new HashMap<>();
    private IResolver.IResolverCallback<Response> mRtspResolverCallback;

    public RTSPSession(String target, int port, TransportMethod method) {
        this.target = target;
        this.port = port;
        this.method = method;
        transport = method.createTransport(target, port, 3000);
        setRTSPAndCallback();
    }

    private void setRTSPAndCallback() {
        transport.setRtspResolver(new RTSPResolver());
        transport.setRtpResolver(new RTPResolver());
        transport.setRtcpResolver(new RTCPResolver());
        transport.getRtspResolver().setResolverCallback(new IResolver.IResolverCallback<Response>() {
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
                //回调
                if (mRtspResolverCallback != null) {
                    mRtspResolverCallback.onResolve(response);
                }
            }
        });
    }

    public void connect() {
        transport.connectAsync();
    }

    public void setTransportListener(ITransportListener transportListener) {
        transport.setTransportListener(transportListener);
    }

    public void setRTSPResolverCallback(IResolver.IResolverCallback<Response> resolverCallback) {
        mRtspResolverCallback = resolverCallback;
    }

    public void setRTPCallback(IResolver.IResolverCallback<RtpPacket> rtpResolverCallback) {
        IResolver<Integer, RtpPacket> rtpResolver = transport.getRtpResolver();
        if (rtpResolver != null) {
            rtpResolver.release();
            rtpResolver.setResolverCallback(rtpResolverCallback);
        }
    }

    public void setRTCPCallback(IResolver.IResolverCallback<IReport> rtcpResolverCallback) {
        IResolver<Integer, IReport> rtcpResolver = transport.getRtcpResolver();
        if (rtcpResolver != null) {
            rtcpResolver.release();
            rtcpResolver.setResolverCallback(rtcpResolverCallback);
        }
    }

    public void send(Request request) {
        int cseq = sequence.incrementAndGet();
        request.addHeader(new CSeqHeader(cseq));
        if (!Utils.isEmpty(session)) {
            request.addHeader(new SessionHeader(session, 0));
        }
        request.addHeader(new UserAgentHeader("ChengGuo"));
        StringBuilder sb = new StringBuilder();
        sb.append(">----------------------- ").append(request.getLine().getMethod()).append("\r\n")
                .append(request.toString())
                .append(">----------------------- ").append(request.getLine().getMethod());
        System.out.println(sb);
        //暂存Request
        requestList.put(cseq, request);
        //发送请求
        transport.send(request);
    }

    public boolean isConnected() {
        return transport.isConnected();
    }
}