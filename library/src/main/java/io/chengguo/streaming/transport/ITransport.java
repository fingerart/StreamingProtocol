package io.chengguo.streaming.transport;

import java.io.IOException;

import io.chengguo.streaming.rtcp.IReport;
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

    void connect() throws IOException;

    void connectAsync();

    boolean isConnected();

    void disconnect();

    void send(IMessage message);

    void setRtspResolver(IResolver<Integer, Response> rtspResolver);

    void setRtpResolver(IResolver<Integer, RtpPacket> rtpResolver);

    void setRtcpResolver(IResolver<Integer, IReport> rtcpResolver);

    IResolver<Integer, Response> getRtspResolver();

    IResolver<Integer, RtpPacket> getRtpResolver();

    IResolver<Integer, IReport> getRtcpResolver();
}
