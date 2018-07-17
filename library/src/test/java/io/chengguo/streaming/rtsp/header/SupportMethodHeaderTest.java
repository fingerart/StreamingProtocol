package io.chengguo.streaming.rtsp.header;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static io.chengguo.streaming.rtsp.Method.DESCRIBE;
import static io.chengguo.streaming.rtsp.Method.OPTIONS;
import static io.chengguo.streaming.rtsp.Method.PAUSE;
import static io.chengguo.streaming.rtsp.Method.PLAY;

/**
 * Created by fingerart on 2018-07-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SupportMethodHeaderTest {
    SupportMethodHeader headerByStr;
    SupportMethodHeader headerByEnum;

    @Before
    public void setUp() throws Exception {
        headerByStr = new SupportMethodHeader("Public: OPTIONS,DESCRIBE,PAUSE,PLAY");
        headerByEnum = new SupportMethodHeader(OPTIONS, DESCRIBE, PAUSE, PLAY);
    }

    @Test
    public void testConstructor() throws Exception {
        Assert.assertEquals(headerByStr, headerByEnum);
    }

    @Test
    public void setRawValue() throws Exception {
        SupportMethodHeader header = new SupportMethodHeader("Public");
        header.setRawValue("Public: OPTIONS,DESCRIBE,PAUSE,PLAY");
        Assert.assertEquals(header, headerByStr);
    }

    @Test
    public void isSupportMethod() throws Exception {
        Assert.assertTrue(headerByStr.isSupportMethod(PLAY));
    }

}