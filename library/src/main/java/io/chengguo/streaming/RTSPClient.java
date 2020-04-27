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

    private RTSPInterceptor mInterceptor;
    private RTSPSession session;
    private String baseUri;

    public RTSPClient(Builder builder) {
        mInterceptor = builder.rtspInterceptor;
        registerObserver(builder.rtpPacketReceiver);
        session = new RTSPSession(builder.host, builder.port, builder.transportMethod);
        session.setTransportListener(mTransportListener);
        session.setRTSPResolverCallback(mRTSPResolverCallback);
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

    private final IResolver.IResolverCallback<Response> mRTSPResolverCallback = new IResolver.IResolverCallback<Response>() {
        @Override
        public void onResolve(Response response) {
            System.out.println("response = [" + response + "]");
            if (response.getLine().isSuccessful()) {
                Request nextRequest = makeNextRequest(response);
                try {
                    if (mInterceptor != null) {
                        nextRequest = mInterceptor.onIntercept(nextRequest, response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nextRequest != null && nextRequest.getLine().getMethod() != null) {
                    session.send(nextRequest);
                }
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
        private RTSPInterceptor rtspInterceptor;
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

        public Builder setInterceptor(RTSPInterceptor rtspInterceptor) {
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

    public interface RTSPInterceptor {
        Request onIntercept(@Nullable Request nextRequest, @NonNull Response preResponse);
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