package io.chengguo.streaming.rtsp.header;

import org.junit.Before;
import org.junit.Test;

import static io.chengguo.streaming.rtsp.Method.DESCRIBE;
import static io.chengguo.streaming.rtsp.Method.OPTIONS;
import static io.chengguo.streaming.rtsp.Method.PAUSE;
import static io.chengguo.streaming.rtsp.Method.PLAY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by fingerart on 2018-07-18.
 */
public class SupportMethodHeaderTest {
    SupportMethodHeader headerByStr;
    SupportMethodHeader headerByEnum;

    @Before
    public void setUp() {
        headerByStr = new SupportMethodHeader("Public: OPTIONS,DESCRIBE,PAUSE,PLAY");
        headerByEnum = new SupportMethodHeader(OPTIONS, DESCRIBE, PAUSE, PLAY);
    }

    @Test
    public void testEquals() {
        assertThat(headerByStr, is(headerByEnum));
    }

    @Test
    public void isSupportMethod() {
        assertThat(headerByStr.isSupportMethod(PLAY), is(true));
    }

}