package io.chengguo.streaming.codec.h264;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Base64;

import io.chengguo.streaming.utils.Bits;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SPSTest {

    @Test
    public void testItsMine() {
        assertThat(SPS.itsMine(Byte.parseByte("111", 2)), is(true));
        assertThat(SPS.itsMine(Byte.parseByte("11111", 2)), is(false));
    }

    @Test
    public void testValueOf() {
        byte[] spsBytes = Base64.getDecoder().decode("Z00AHp2oKA9puAgICBA=");
        SPS sps = SPS.valueOf(ByteBuffer.wrap(spsBytes));
        assertThat(sps.width, is(640));
        assertThat(sps.height, is(480));
        assertThat(sps.profileIdc, is(77));
        assertThat(sps.type.forbidden, is(0));
        assertThat(sps.type.refIdc, is(3));
        assertThat(sps.type.type, is(7));
    }

    @Test
    public void testWidthHeight() {
        byte[] spsBytes = Base64.getDecoder().decode("J0LgC6kYYJ2ANQYBBrbCte98BA==");
        System.out.println(Bits.dumpBytesToBinary(spsBytes));
        System.out.println(Bits.dumpBytesToHex(spsBytes));
        SPS sps = SPS.valueOf(ByteBuffer.wrap(spsBytes));
        assertThat(sps.width, is(192));
        assertThat(sps.height, is(144));
    }
}