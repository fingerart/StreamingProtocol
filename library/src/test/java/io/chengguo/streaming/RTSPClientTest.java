package io.chengguo.streaming;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.transport.TransportMethod;

/**
 * Created by fingerart on 2018-09-11.
 */
public class RTSPClientTest {
    private RTSPSession session;

    @Before
    public void setUp() throws Exception {
        session = new RTSPSession("127.0.0.1", 554, TransportMethod.TCP);
        session.connect();
    }

    @Test
    public void testConnect() throws Exception {
        Thread.sleep(3000);
        Assert.assertTrue("ok", session.isConnected());
    }

    @Test
    public void testOptions() throws Exception {
        Thread.sleep(3000);
        Request request = new Request.Builder()
                .uri(URI.create("rtsp://127.0.0.1/NeverPlay.mp3"))
                .method(Method.OPTIONS)
                .build();
        session.send(request);

        Thread.sleep(3000);
    }

    @Test
    public void test() throws Exception {

    }
}