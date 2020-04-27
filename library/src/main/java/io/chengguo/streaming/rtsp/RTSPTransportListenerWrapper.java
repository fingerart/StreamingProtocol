package io.chengguo.streaming.rtsp;

import java.io.DataInputStream;
import java.io.IOException;

import io.chengguo.streaming.rtcp.RTCPResolver;
import io.chengguo.streaming.rtp.RTPResolver;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.utils.L;

/**
 * TransportListener Wrapper
 */
public class RTSPTransportListenerWrapper implements io.chengguo.streaming.transport.ITransportListener {

    private final RTSPResolver mRTSPResolver;
    private final RTPResolver mRTPResolver;
    private final RTCPResolver mRTCPResolver;

    public RTSPTransportListenerWrapper(IResolver.IResolverCallback<Response> rtspResolver, IResolver.IResolverCallback<RtpPacket> rtpPacketIResolverCallback, RTCPResolver.RTCPResolverListener rtcpResolver) {
        mRTSPResolver = new RTSPResolver(rtspResolver);
        mRTPResolver = new RTPResolver(rtpPacketIResolverCallback);
        mRTCPResolver = new RTCPResolver(rtcpResolver);
    }

    @Override
    public void onConnected(DataInputStream in) throws IOException {
        int firstByte;
        while ((firstByte = in.readUnsignedByte()) > 0) {
            L.d("First Byte: " + firstByte);
            //'$' beginning is the RTP and RTCP
            if (firstByte == 36) {//0x24
                int secondByte = in.readUnsignedByte();
                //channel is rtp
                if (secondByte == 0) {
                    if (mRTPResolver != null) {
                        int rtpLength = in.readUnsignedShort();
                        mRTPResolver.resolve(in, rtpLength);
                    }
                } else if (secondByte == 1) { //channel is rtcp
                    if (mRTCPResolver != null) {
                        int rtcpLength = in.readUnsignedShort();
                        mRTCPResolver.resolve(in, rtcpLength);
                    }
                }
            } else {//RTSP
                if (mRTSPResolver != null) {
                    mRTSPResolver.resolve(in, firstByte);
                }
            }
        }
    }

    @Override
    public void onConnectChanged(int state, Throwable throwable) {

    }
}
