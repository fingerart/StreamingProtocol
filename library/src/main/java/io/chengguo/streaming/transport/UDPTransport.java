package io.chengguo.streaming.transport;

import io.chengguo.streaming.rtcp.RTCPResolver;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;
import io.chengguo.streaming.rtsp.Response;

/**
 * Created by fingerart on 2018-09-08.
 */
public class UDPTransport implements ITransport {
    @Override
    public void setTransportListener(ITransportListener listener) {

    }

    @Override
    public void connect() {

    }

    @Override
    public void connectAsync() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(IMessage data) {

    }

    @Override
    public void setRtspResolver(IResolver<Integer, IResolver.IResolverCallback<Response>> rtspResolver) {

    }

    @Override
    public void setRtpResolver(IResolver<Integer, IResolver.IResolverCallback<RtpPacket>> rtpResolver) {

    }

    @Override
    public void setRtcpResolver(IResolver<Integer, RTCPResolver.RTCPResolverListener> rtcpResolver) {

    }

    @Override
    public IResolver<Integer, IResolver.IResolverCallback<Response>> getRtspResolver() {
        return null;
    }

    @Override
    public IResolver<Integer, IResolver.IResolverCallback<RtpPacket>> getRtpResolver() {
        return null;
    }

    @Override
    public IResolver<Integer, RTCPResolver.RTCPResolverListener> getRtcpResolver() {
        return null;
    }
}
