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
import io.chengguo.streaming.transport.TransportMethod;

/**
 * RTSPClient
 */
public class RTSPClient implements ITransportListener {

    private static final String TAG = RTSPClient.class.getSimpleName();

    private RTSPInterceptor mInterceptor;
    private final RTPPacketReceiver mRtpPacketReceiver;
    private RTSPSession session;
    private String baseUri;

    public RTSPClient(Builder builder) {
        mInterceptor = builder.rtspInterceptor;
        mRtpPacketReceiver = builder.rtpPacketReceiver;
        session = new RTSPSession(builder.host, builder.port, builder.transportMethod);
        session.setTransportListener(this);
        session.setRTSPResolverCallback(new IResolver.IResolverCallback<Response>() {
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
        });
        session.setRTPResolverCallback(new IResolver.IResolverCallback<RtpPacket>() {
            @Override
            public void onResolve(RtpPacket rtpPacket) {
                if (mRtpPacketReceiver != null) {
                    mRtpPacketReceiver.onReceive(rtpPacket);
                }
            }
        });
    }

    public void connect() {
        session.connect();
    }

    public void disconnect() {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    public void play(URI uri) {
        if (!session.isConnected()) {
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
        if (!session.isConnected()) {
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
        if (!session.isConnected()) {
            Log.e(TAG, "session is not connection");
            return;
        }
        Request stop = new Request.Builder()
                .method(Method.TEARDOWN)
                .uri(baseUri)
                .build();
        session.send(stop);
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
    public void onDisconnected() {
        System.out.println("RTSPClient.onDisconnected");
    }

    private Request makeNextRequest(Response response) {
        Request.Builder builder = new Request.Builder();
        Request preRequest = response.getRequest();
        switch (preRequest.getLine().getMethod()) {
            case OPTIONS:
                builder.method(Method.DESCRIBE)
                        .uri(preRequest.getLine().getUri());
                break;
            case DESCRIBE:
                Header<String> base = response.getHeader("Content-Base");
                baseUri = base.getRawValue();
                builder.method(Method.SETUP)
                        .uri(URI.create(baseUri + "track1"))
                        .addHeader(new TransportHeader.Builder()
                                .specifier(TransportHeader.Specifier.TCP)
                                .broadcastType(TransportHeader.BroadcastType.unicast)
                                .clientPort(50846, 50847)
                                .build()
                        );
                break;
            case SETUP:
                builder.method(Method.PLAY)
                        .addHeader(new RangeHeader(0))
                        .uri(baseUri);
                break;
            case PLAY:
            default:
                return null;//Not replay
        }
        return builder.build();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private String host;
        private int port = 554;
        private TransportMethod transportMethod = TransportMethod.TCP;
        private RTSPInterceptor rtspInterceptor;
        private RTPPacketReceiver rtpPacketReceiver;

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

        public Builder setRTPPacketReciver(RTPPacketReceiver rtpPacketReceiver) {
            this.rtpPacketReceiver = rtpPacketReceiver;
            return this;
        }

        public RTSPClient build() {
            return new RTSPClient(this);
        }
    }

    public interface RTSPInterceptor {
        Request onIntercept(@Nullable Request nextRequest, @NonNull Response preResponse);
    }

    public interface RTPPacketReceiver {
        void onReceive(RtpPacket rtpPacket);
    }
}