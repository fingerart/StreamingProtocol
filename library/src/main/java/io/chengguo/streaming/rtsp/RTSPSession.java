package io.chengguo.streaming.rtsp;

import java.util.concurrent.atomic.AtomicInteger;

import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.UserAgentHeader;
import io.chengguo.streaming.transport.ITransport;
import io.chengguo.streaming.transport.TransportMethod;

/**
 * Created by fingerart on 2018-09-08.
 */
public class RTSPSession {

    private String target;
    private int port;
    private TransportMethod method;
    private String session;
    private AtomicInteger sequence = new AtomicInteger();
    private ITransport transport;

    public RTSPSession(String target, int port, TransportMethod method) {
        this.target = target;
        this.port = port;
        this.method = method;
        transport = method.createTransport(target, port, 3000);
        transport.setResolver(new RTSPResolver());
    }

    public void connect() {
        transport.connect();
    }

    public void setTransportListener(ITransportListener transportListener) {
        transport.setTransportListener(transportListener);
    }

    // TODO: 2018/9/13 RTSP & RTP
    public void setResolverCallback(IResolver.IResolverCallback<Response> resolverCallback) {
        transport.getResolver().setResolverCallback(resolverCallback);
    }

    public void send(Request request) {
        request.addHeader(new CSeqHeader(sequence.incrementAndGet()));
        request.addHeader(new UserAgentHeader("ChengGuo"));
        StringBuilder sb = new StringBuilder();
        sb.append(">----------------------- ").append(request.getLine().getMethod()).append("\r\n")
                .append(request.toString())
                .append(">----------------------- ").append(request.getLine().getMethod());
        System.out.println(sb);
        transport.send(request.toRaw());
    }

    public boolean isConnected() {
        return transport.isConnected();
    }
}