package io.chengguo.streaming.utils;

import java.nio.ByteBuffer;

public class Bits {

    private Bits() {
    }

    /**
     * 获取4byte，转成long类型
     *
     * @param bb
     * @return
     */
    public static long getLongByInt(ByteBuffer bb) {
        byte[] buffer = new byte[Integer.SIZE / Byte.SIZE];
        bb.get(buffer);
        long result = 0;
        for (int i = 0; i < buffer.length; i++) {
            result |= ((long) buffer[i] & 0xff) << ((buffer.length - 1 - i) * Byte.SIZE);
        }
        return result;
    }
}
