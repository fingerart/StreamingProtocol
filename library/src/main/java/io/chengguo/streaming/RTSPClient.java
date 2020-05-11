package io.chengguo.streaming;

import android.util.Log;
import android.view.Surface;

import java.net.URI;

import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IInterceptor;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ISessionStateObserver;
import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.transport.SendCallback;
import io.chengguo.streaming.transport.TransportImpl;
import io.chengguo.streaming.transport.TransportMethod;
import io.chengguo.streaming.utils.L;

/**
 * RTSPClient
 */
public class RTSPClient extends Observable<RTSPClient.IRTPPacketObserver> {

    private static final String TAG = RTSPClient.class.getSimpleName();
    private final int timeout;
    private final TransportMethod transportMethod;
    private final IInterceptor rtspInterceptor;
    private final Surface mSurface;
    private RTSPSession session;

    public RTSPClient(Builder builder) {
        if (builder.rtpPacketReceiver != null) {
            registerObserver(builder.rtpPacketReceiver);
        }
        timeout = builder.timeout;
        transportMethod = builder.transportMethod;
        rtspInterceptor = builder.rtspInterceptor;
        mSurface = builder.surface;
        L.setEnabled(true);
    }

    public void disconnect() {
        if (isConnected()) {
            session.disconnect();
        }
    }

    public void play(final URI uri) {
        final Request request = new Request.Builder()
                .method(Method.OPTIONS)
                .uri(uri)
                .build();
        if (isConnected()) {
            if (!isSameTarget(uri)) {
                //链接到不同的服务器或端口号，断开链接
                session.disconnect();
                session = null;
            } else {
                //是否在播放中，需停止播放
                if (session.isNormal()) {
                    teardown(new SendCallback() {
                        @Override
                        public void onSuccess() {
                            session.send(request);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            System.out.println("throwable = " + throwable);
                        }
                    });
                } else {
                    session.send(request);
                }
                return;
            }
        }

        session = new RTSPSession(uri.getHost(), uri.getPort(), timeout, transportMethod, mSurface);
        session.addInterceptor(rtspInterceptor);
        session.setStateObserver(mTransportListener);
        session.connect(new TransportImpl.ConnectCallback() {
            @Override
            public void onSuccess() {
                session.send(request);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }
        });
    }

    private boolean isSameTarget(URI uri) {
        return uri.getHost().equals(session.mHost) && uri.getPort() == session.mPort;
    }

    public void resume() {
        if (!isConnected()) {
            L.e(TAG, "session is not connection");
            return;
        }
        if (!session.isPause()) {
            L.e(TAG, "session state is not pause");
            return;
        }
        Request request = new Request.Builder()
                .method(Method.PLAY)
                .build();
        session.send(request);
    }

    public void pause() {
        if (!isConnected()) {
            L.e(TAG, "session is not connection");
            return;
        }

        if (!session.isPlay()) {
            L.e(TAG, "session state is not play");
            return;
        }

        Request stop = new Request.Builder()
                .method(Method.PAUSE)
                .build();
        session.send(stop);
    }

    public void teardown() {
        teardown(null);
    }

    private void teardown(SendCallback sendCallback) {
        if (!isConnected()) {
            L.e(TAG, "session is not connection");
            return;
        }
        Request stop = new Request.Builder()
                .method(Method.TEARDOWN)
                .build();
        session.send(stop, sendCallback);
    }

    public void send(Request request) {
        if (!isConnected()) {
            L.e(TAG, "session is not connection");
            return;
        }
        session.send(request);
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    private final ISessionStateObserver mTransportListener = new ISessionStateObserver() {

        @Override
        public void onConnectChanged(int state) {
            for (IRTPPacketObserver observer : mObservers) {
                switch (state) {
                    case SESSION_STATE_CONNECTED:
                        observer.onConnected();
                        break;
                    case SESSION_STATE_DISCONNECTED:
                        observer.onDisconnected();
                        break;
                }
            }
        }

        @Override
        public void onError(Exception exception) {

        }
    };

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private int timeout = 3000;
        private TransportMethod transportMethod = TransportMethod.TCP;
        private IRTPPacketObserver rtpPacketReceiver;
        private IInterceptor rtspInterceptor;
        private Surface surface;

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

        public Builder setSurface(Surface surface) {
            this.surface = surface;
            return this;
        }

        public RTSPClient build() {
            return new RTSPClient(this);
        }
    }

    public interface IRTPPacketObserver {
        void onConnected();

        void onDisconnected();

        void onReceive(RtpPacket rtpPacket);
    }
}