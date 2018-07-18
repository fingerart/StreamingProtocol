package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by FingerArt on 2018/7/18.
 */
public class CSeqHeaderTest {

    @Test
    public void testConstructor() throws Exception {
        CSeqHeader header = new CSeqHeader("CSeq: 6");
        assertTrue(6 == header.getRawValue());
    }

    @Test
    public void test() throws Exception {
        CSeqHeader header = new CSeqHeader(1);
        assertTrue(1 == header.getRawValue());
    }
}