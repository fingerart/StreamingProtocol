package io.chengguo.streaming.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BitsTest {

    @Test
    public void testByteArrayToInt() {
        byte[] bytes = {
                Byte.parseByte("101", 2),
                Byte.parseByte("111", 2)
        };
        assertThat(Bits.byteArrayToInt(bytes), is(1287));
    }

    @Test
    public void testByteArrayToInt_outOfBounds() {
        byte[] bytes = {
                (byte) 255,
                Byte.parseByte("101", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("111", 2)
        };
        assertThat(Bits.byteArrayToInt(bytes), is(-16448249));
    }

    @Test
    public void testLongToByteArray() {
        assertThat(Bits.longToByteArray(10, 1), is(new byte[]{10}));
        assertThat(Bits.longToByteArray(329382938, 2), is(new byte[]{(byte) 252, 26}));
        assertThat(Bits.longToByteArray(725, 3), is(new byte[]{0, 2, -43}));
    }

    @Test
    public void dumpBytes() {
        byte[] bytes = {
                (byte) 255,
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2)
        };
        System.out.println(Bits.dumpBytes(bytes));
    }

    @Test
    public void dumpBytesToBinary() {
        byte[] bytes = {
                (byte) 255,
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2),
                Byte.parseByte("11010", 2),
                Byte.parseByte("1010", 2),
                Byte.parseByte("101", 2)
        };
        System.out.println(Bits.dumpBytesToBinary(bytes));
    }

    @Test
    public void testDumpBytesToHex() {
        byte[] bytes = {32, 12, 2, 3, 1, 8, 32, 12, 2, 3, 1, 8, 32, 12, 2, 3, 1, 8,};
        System.out.println(Bits.dumpBytesToHex(bytes));
    }
}