package io.chengguo.streaming.codec.h264;

import android.util.Base64;

import java.nio.ByteBuffer;

public class SPS extends Frame {
    public static final int TYPE = 7;

    public int profileIdc;
    public int width;
    public int height;

    public static boolean itsMine(byte first) {
        int type = first & 0x1F;
        return type == TYPE;
    }

    public static SPS valueOf(String base64) {
        byte[] spsBytes = Base64.decode(base64, Base64.DEFAULT);
        ByteBuffer buffer = ByteBuffer.wrap(spsBytes);
        return valueOf(buffer);
    }

    public static SPS valueOf(ByteBuffer buffer) {
        SPS sps = new SPS();
        sps.parseType(buffer.get());
        sps.parseProfile(buffer.get());
        byte b = buffer.get();
        sps.parseSetFlag(((byte) (b >> 2)));
        byte pre = buffer.get(5);
        byte next = buffer.get(6);
        int widthMarco = ((pre & 0x7) << 8) | next;
        sps.width = (widthMarco + 0) * 16;
        pre = buffer.get(7);
        next = buffer.get(8);
        int heightMarco = (pre << 1) | (next >> 7 & 0x1);
        sps.height = (heightMarco + 0) * 16;
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
