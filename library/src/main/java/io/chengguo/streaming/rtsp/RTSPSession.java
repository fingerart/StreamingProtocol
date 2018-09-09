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
    private AtomicInteger sequence = new AtomicInteger();
    private ITransport transport;

    public RTSPSession(String target, int port, TransportMethod method) {
        this.target = target;
        this.port = port;
        this.method = method;
    }

    public void connect() {
        transport = method.createTransport(target, port, 3000);
        transport.setResolver(new RTSPResolver());
        transport.connect();
    }

    public void setTransportListener(ITransportListener transportListener) {
        transport.setTransportListener(transportListener);
    }

    public void setResolverCallback(IResolver.IResolverCallback<Response> resolverCallback) {
        transport.getResolver().setResolverCallback(resolverCallback);
    }

    public void send(Request request) {
        request.addHeader(new CSeqHeader(sequence.incrementAndGet()));
        request.addHeader(new UserAgentHeader("ChengGuo"));
        transport.send(request.toRaw());
    }
}