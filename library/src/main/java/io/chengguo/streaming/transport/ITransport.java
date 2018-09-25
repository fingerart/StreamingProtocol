package io.chengguo.streaming.transport;

import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;
import io.chengguo.streaming.rtsp.Response;

/**
 * Created by fingerart on 2018-09-08.
 */
public interface ITransport {

    void setTransportListener(ITransportListener listener);

    void connect();

    boolean isConnected();

    void disconnect();

    void send(IMessage message);

    void setRtspResolver(IResolver<Integer, Response> resolver);

    void setRtpResolver(IResolver<Integer, RtpPacket> resolver);

    IResolver<Integer, Response> getRtspResolver();

    IResolver<Integer, RtpPacket> getRtpResolver();
}
