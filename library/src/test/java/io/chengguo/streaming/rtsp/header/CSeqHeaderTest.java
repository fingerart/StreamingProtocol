package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by FingerArt on 2018/7/18.
 */
public class CSeqHeaderTest {

    @Test
    public void testConstructor_nameOrRaw() {
        CSeqHeader header = new CSeqHeader("CSeq: 6");
        assertThat(header.getRawValue(), is(6));
    }

    @Test
    public void testConstructor_value() {
        CSeqHeader header = new CSeqHeader(1);
        assertThat(header.getRawValue(), is(1));
    }
}