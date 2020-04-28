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
    private final ISessionStateObserver mSessionStateObserver;

    public RTSPTransportListenerWrapper(ISessionStateObserver sessionStateObserver, IResolver.IResolverCallback<Response> rtspResolver, IResolver.IResolverCallback<RtpPacket> rtpPacketIResolverCallback, RTCPResolver.RTCPResolverListener rtcpResolver) {
        mSessionStateObserver = sessionStateObserver;
        mRTSPResolver = new RTSPResolver(rtspResolver);
        mRTPResolver = new RTPResolver(rtpPacketIResolverCallback);
        mRTCPResolver = new RTCPResolver(rtcpResolver);
    }

    @Override
    public void onConnected(DataInputStream in) throws IOException {
        if (mSessionStateObserver != null) {
            mSessionStateObserver.onConnected();
        }
        int firstByte;
        while ((firstByte = in.readUnsignedByte()) > 0) {
            L.d("First Byte: " + firstByte);
            //'$' beginning is the RTP and RTCP
            if (firstByte == 36) {//0x24
                int secondByte = in.readUnsignedByte();
                //channel is rtp
                if (secondByte == 0) {
                    int rtpLength = in.readUnsignedShort();
                    mRTPResolver.resolve(in, rtpLength);
                } else if (secondByte == 1) { //channel is rtcp
                    int rtcpLength = in.readUnsignedShort();
                    mRTCPResolver.resolve(in, rtcpLength);
                }
            } else {//RTSP
                mRTSPResolver.resolve(in, firstByte);
            }
        }
    }

    @Override
    public void onConnectChanged(int state, Throwable throwable) {
        switch (state) {
            case STATE_CONNECT_FAILURE:
                if (mSessionStateObserver != null) {
                    mSessionStateObserver.onConnectFailure(throwable);
                }
            case STATE_DISCONNECTED:
                if (mSessionStateObserver != null) {
                    mSessionStateObserver.onDisconnected();
                }
        }
    }
}
