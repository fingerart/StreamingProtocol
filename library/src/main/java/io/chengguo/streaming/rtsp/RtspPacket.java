package io.chengguo.streaming.rtsp;

import io.chengguo.streaming.rtcp.IPacket;

/**
 *
 */
public class RtspPacket implements IPacket {
    private int magic;
    private int channel;
    private long length;
    private IPacket[] bs;



    @Override
    public byte[] toRaw() {
        return new byte[0];
    }
}
