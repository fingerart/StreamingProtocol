package io.chengguo.streaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import java.net.URI;

import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;
import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.rtsp.Response;
import io.chengguo.streaming.rtsp.header.Header;
import io.chengguo.streaming.rtsp.header.RangeHeader;
import io.chengguo.streaming.rtsp.header.TransportHeader;
import io.chengguo.streaming.rtsp.sdp.H264SDP;
import io.chengguo.streaming.transport.TransportMethod;

import static io.chengguo.streaming.rtsp.header.TransportHeader.Specifier.TCP;

/**
 * RTSPClient
 */
public class RTSPClient extends Observable<RTSPClient.IRTPPacketObserver> {

    private static final String TAG = RTSPClient.class.getSimpleName();

    private RTSPSession session;

    public RTSPClient(Builder builder) {
        mInterceptor = builder.rtspInterceptor;
        registerObserver(builder.rtpPacketReceiver);
        session = new RTSPSession(builder.host, builder.port, builder.transportMethod);
        session.setObserver(mTransportListener);
        session.setRTPResolverCallback(mRTPResolverCallback);
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
                .uri(baseUri)
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
                .uri(baseUri)
                .build();
        session.send(stop);
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    private final ITransportListener mTransportListener = new ITransportListener() {
        @Override
        public void onConnected() {
            for (IRTPPacketObserver observer : mObservers) {
                observer.onConnected();
            }
        }

        @Override
        public void onConnectFail(Exception exception) {
            for (IRTPPacketObserver observer : mObservers) {
                observer.onConnectFail(exception);
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
        private TransportMethod transportMethod = TransportMethod.TCP;
        private IRTPPacketObserver rtpPacketReceiver;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder transport(TransportMethod transportMethod) {
            this.transportMethod = transportMethod;
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

        void onConnectFail(Exception exception);

        void onDisconnected();

        void onReceive(RtpPacket rtpPacket);
    }

    public class RTPPacketObserver implements IRTPPacketObserver {

        @Override
        public void onConnected() {
        }

        @Override
        public void onConnectFail(Exception exception) {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onReceive(RtpPacket rtpPacket) {
        }
    }
}