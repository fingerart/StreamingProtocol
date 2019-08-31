package io.chengguo.streaming.codec.h264;

import java.nio.ByteBuffer;

public class SPS extends Frame {
    public static final int TYPE = 7;

    public int profileIdc;
    public int width;
    private int height;

    public static boolean itsMine(byte first) {
        int type = first & 0x1F;
        return type == TYPE;
    }

    public static SPS create(ByteBuffer buffer) {
        SPS sps = new SPS();
        sps.parseType(buffer.get());
        sps.parseProfile(buffer.get());
        byte b = buffer.get();
        sps.parseSetFlag(((byte) (b >> 2)));
        byte pre = buffer.get(5);
        byte next = buffer.get(6);
        int widthMarco = (pre & 0x3) << 5 | (next >> 3 & 0x1F);
        sps.width = (widthMarco + 1) * 16;
        byte last = buffer.get(7);
        int heightMarco = next & 0x7 | (last >> 4 & 0xF);
        sps.height = (heightMarco + 1) * 16;
        return sps;
    }

    private void parseSetFlag(byte b) {

    }

    private void parseProfile(byte b) {
        profileIdc = b;
    }

    private void parseType(byte b) {
        type = new Type(b);
    }

    @Override
    public String toString() {
        return "SPS{" +
                "profileIdc=" + profileIdc +
                ", width=" + width +
                ", height=" + height +
                ", type=" + type +
                '}';
    }
}
