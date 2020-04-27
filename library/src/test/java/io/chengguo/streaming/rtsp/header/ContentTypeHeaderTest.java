package io.chengguo.streaming.rtsp.header;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by fingerart on 2018-07-18.
 */
public class ContentTypeHeaderTest {
    ContentTypeHeader header;

    @Before
    public void setUp() {
        header = new ContentTypeHeader(ContentTypeHeader.Type.SDP);
    }

    @Test
    public void testConstructor() {
        ContentTypeHeader headerByStr = new ContentTypeHeader("Content-Type: application/sdp");
        Assert.assertEquals(headerByStr, header);
    }

    @Test
    public void testIsSupportType() {
        Assert.assertTrue(header.isSupportType(ContentTypeHeader.Type.SDP));
    }

}