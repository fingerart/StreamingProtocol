package io.chengguo.streaming.transport;

import io.chengguo.streaming.rtcp.IPacket;
import io.chengguo.streaming.rtcp.RTCPResolver;
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

    void connect() throws Exception;

    void connectAsync();

    boolean isConnected();

    void disconnect();

    void send(IMessage message);

    void setRtspResolver(IResolver<Integer, IResolver.IResolverCallback<Response>> rtspResolver);

    void setRtpResolver(IResolver<Integer, IResolver.IResolverCallback<RtpPacket>> rtpResolver);

    void setRtcpResolver(IResolver<Integer, RTCPResolver.RTCPResolverListener> rtcpResolver);

    IResolver<Integer, IResolver.IResolverCallback<Response>> getRtspResolver();

    IResolver<Integer, IResolver.IResolverCallback<RtpPacket>> getRtpResolver();

    IResolver<Integer, RTCPResolver.RTCPResolverListener> getRtcpResolver();
}
