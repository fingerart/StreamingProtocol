package io.chengguo.streaming.rtsp.header;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by fingerart on 2018-07-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcceptHeaderTest {

    @Test
    public void testConstructor() throws Exception {
        AcceptHeader header = new AcceptHeader(ContentTypeHeader.Type.SDP);
        Assert.assertEquals(header.toString(), "Accept: application/sdp");
    }
}