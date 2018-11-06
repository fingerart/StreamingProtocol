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

    /**
     * int 转 byte[]
     *
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * long 转 byte[]
     *
     * @param l
     * @return
     */
    public static byte[] longToByteArray(long l) {
        byte[] result = new byte[Long.SIZE / Byte.SIZE];
        //由高位到低位
        result[0] = (byte) ((l >> 56) & 0xFF);
        result[1] = (byte) ((l >> 48) & 0xFF);
        result[2] = (byte) ((l >> 40) & 0xFF);
        result[3] = (byte) ((l >> 32) & 0xFF);
        result[4] = (byte) ((l >> 24) & 0xFF);
        result[5] = (byte) ((l >> 16) & 0xFF);
        result[6] = (byte) ((l >> 8) & 0xFF);
        result[7] = (byte) (l & 0xFF);
        return result;
    }
}
