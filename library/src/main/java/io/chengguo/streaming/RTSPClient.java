package io.chengguo.streaming;

import android.util.Log;

import java.net.URI;

import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IInterceptor;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ISessionStateObserver;
import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.transport.TransportMethod;

/**
 * RTSPClient
 */
public class RTSPClient extends Observable<RTSPClient.IRTPPacketObserver> {

    private static final String TAG = RTSPClient.class.getSimpleName();

    private RTSPSession session;

    public RTSPClient(Builder builder) {
        registerObserver(builder.rtpPacketReceiver);
        session = new RTSPSession(builder.host, builder.port, builder.timeout, builder.transportMethod);
        session.addInterceptor(builder.rtspInterceptor);
        session.setStateObserver(mTransportListener);
        session.setRTPResolverObserver(mRTPResolverCallback);
    }

    public void connect() {
        session.connect();
    }

    public void disconnect() {
        if (isConnected()) {
            session.disconnect();
        }
    }

    public void play(URI uri) {
        if (!isConnected()) {
            Log.e(TAG, "session is not connection");
            return;
        }
        Request request = new Request.Builder()
                .method(Method.OPTIONS)
                .uri(uri)
                .build();
        session.send(request);
    }

    public void pause() {
        if (!isConnected()) {
            Log.e(TAG, "session is not connection");
            return;
        }
        Request stop = new Request.Builder()
                .method(Method.PAUSE)
                .build();
        session.send(stop);
    }

    public void teardown() {
        if (!isConnected()) {
            Log.e(TAG, "session is not connection");
            return;
        }
        Request stop = new Request.Builder()
                .method(Method.TEARDOWN)
                .build();
        session.send(stop);
    }

    public void send(Request request) {
        if (!isConnected()) {
            Log.e(TAG, "session is not connection");
            return;
        }
        session.send(request);
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    private final ISessionStateObserver mTransportListener = new ISessionStateObserver() {
        @Override
        public void onConnected() {
            for (IRTPPacketObserver observer : mObservers) {
                observer.onConnected();
            }
        }

        @Override
        public void onConnectFailure(Throwable throwable) {
            for (IRTPPacketObserver observer : mObservers) {
                observer.onConnectFailure(throwable);
            }
        }

        @Override
        public void onDisconnected() {
            for (IRTPPacketObserver observer : mObservers) {
                observer.onDisconnected();
            }
        }
    };

    private final IResolver.IResolverCallback<RtpPacket> mRTPResolverCallback = new IResolver.IResolverCallback<RtpPacket>() {
        @Override
        public void onResolve(RtpPacket rtpPacket) {
            for (IRTPPacketObserver observer : mObservers) {
                observer.onReceive(rtpPacket);
            }
        }
    };

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private String host;
        private int port = 554;
        private int timeout = 3000;
        private TransportMethod transportMethod = TransportMethod.TCP;
        private IRTPPacketObserver rtpPacketReceiver;
        private IInterceptor rtspInterceptor;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder transport(TransportMethod transportMethod) {
            this.transportMethod = transportMethod;
            return this;
        }

        public Builder rtspInterceptor(IInterceptor rtspInterceptor) {
            this.rtspInterceptor = rtspInterceptor;
            return this;
        }

        public Builder setRTPPacketObserver(IRTPPacketObserver rtpPacketObserver) {
            this.rtpPacketReceiver = rtpPacketObserver;
            return this;
        }

        public RTSPClient build() {
            return new RTSPClient(this);
        }
    }

    public interface IRTPPacketObserver {
        void onConnected();

        void onConnectFailure(Throwable throwable);

        void onDisconnected();

        void onReceive(RtpPacket rtpPacket);
    }
}