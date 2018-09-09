package io.chengguo.streaming;

import java.net.URI;

import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;
import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.rtsp.Response;
import io.chengguo.streaming.rtsp.header.TransportHeader;
import io.chengguo.streaming.transport.TransportMethod;

/**
 * Created by fingerart on 2018-07-15.
 */
public class RTSPClient implements ITransportListener, IResolver.IResolverCallback<Response> {

    private RTSPSession session;

    public RTSPClient() {
        session = new RTSPSession("", 554, TransportMethod.TCP);
        session.setTransportListener(this);
        session.setResolverCallback(this);
        session.connect();
    }

    public void options(URI uri) {
        Request request = new Request.Builder()
                .method(Method.OPTIONS)
                .uri(uri)
                .build();
        session.send(request);
    }

    public void describe(URI uri) {
        Request request = new Request.Builder()
                .method(Method.DESCRIBE)
                .uri(uri)
                .build();
        session.send(request);
    }

    public void setup(URI uri) {
        Request request = new Request.Builder()
                .method(Method.SETUP)
                .uri(uri)
                .addHeader(new TransportHeader(""))
                .build();
        session.send(request);
    }

    @Override
    public void onConnected() {
        System.out.println("RTSPClient.onConnected");
    }

    @Override
    public void onConnectFail(Exception exception) {
        System.out.println("exception = [" + exception + "]");
    }

    @Override
    public void onResolve(Response response) {
        System.out.println("response = [" + response + "]");
    }

    @Override
    public void onDisconnected() {
        System.out.println("RTSPClient.onDisconnected");
    }
}