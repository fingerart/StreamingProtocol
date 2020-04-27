package io.chengguo.streaming.rtsp.header;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by fingerart on 2018-07-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcceptHeaderTest {

    @Test
    public void testConstructor() {
        AcceptHeader header = new AcceptHeader(ContentTypeHeader.Type.SDP);
        Assert.assertEquals(header.toString(), "Accept: application/sdp");
    }

    @Test
    public void testConstructor_string() {
        AcceptHeader header = new AcceptHeader("application/sdp");
        Assert.assertEquals(header.toString(), "Accept: application/sdp");
    }
}