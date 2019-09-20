package io.chengguo.streaming.utils;

import org.junit.Test;

public class BitsTest {

    @Test
    public void byteArrayToInt() {
        byte[] bytes = {Byte.parseByte("10", 2), Byte.parseByte("11", 2)};
        System.out.println(Bits.byteArrayToInt(bytes));
    }

    @Test
    public void longToByteArray() {
        System.out.println(Bits.dumpBytes(Bits.longToByteArray(329382938, 2)));
    }

    @Test
    public void dumpBytesToHex() {
        System.out.println(Bits.dumpBytesToHex(new byte[]{32, 12, 2,3,1,8,32, 12, 2,3,1,8,32, 12, 2,3,1,8,}));
    }
}