package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by FingerArt on 2018/7/18.
 */
public class RangHeaderTest {

    @Test
    public void testConstructor() throws Exception {
        RangeHeader header = new RangeHeader("Range: npt=0.000-107.234");
        assertEquals("比较Begin相等", 0, header.getBegin(), 0);
        assertEquals("比较End相等", 107.234, header.getEnd(), 0);
        assertTrue(RangeHeader.TimeUnit.NPT == header.getTimeUnit());
    }

    @Test
    public void setEnd() throws Exception {
        RangeHeader header = new RangeHeader(2, 257.23);
        assertEquals("Range: npt=2.000-257.230", header.toString());
    }

}