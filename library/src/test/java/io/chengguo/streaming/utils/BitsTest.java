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
        System.out.println(Bits.dumpByteArray(Bits.longToByteArray(532, 2)));
    }
}