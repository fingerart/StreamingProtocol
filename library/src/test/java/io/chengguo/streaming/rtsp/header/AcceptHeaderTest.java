package io.chengguo.streaming.rtsp.header;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fingerart on 2018-07-18.
 */
public class AcceptHeaderTest {

    @Test
    public void testConstructor() throws Exception {
        AcceptHeader header = new AcceptHeader(ContentTypeHeader.Type.SDP);
        Assert.assertEquals(header, "Accept: application/sdp");
    }
}