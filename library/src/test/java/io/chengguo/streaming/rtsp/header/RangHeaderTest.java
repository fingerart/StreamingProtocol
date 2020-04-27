package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by FingerArt on 2018/7/18.
 */
public class RangHeaderTest {

    @Test
    public void testConstructor_string() {
        RangeHeader header = new RangeHeader("Range: npt=0.000-107.234");
        assertThat(header.getBegin(), is((double) 0));
        assertThat(header.getEnd(), is(107.234));
        assertThat(header.getTimeUnit(), is(RangeHeader.TimeUnit.NPT));
    }

    @Test
    public void testConstructor_value() {
        RangeHeader header = new RangeHeader(2, 257.23);
        assertThat(header.toString(), is("Range: npt=2.000-257.230"));
    }

}