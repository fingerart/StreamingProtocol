package io.chengguo.streaming;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.rtsp.header.TransportHeader;
import io.chengguo.streaming.transport.TransportMethod;

/**
 * Created by fingerart on 2018-09-11.
 */
public class RTSPClientTest {
    private RTSPSession session;

    @Before
    public void setUp() throws Exception {
        session = new RTSPSession("127.0.0.1", 554, TransportMethod.TCP);
    }

    @Test
    public void testConnect() throws Exception {
        connect();
        options();
        describe();
        setup();
    }

    private void connect() throws InterruptedException {
        session.connect();
        Thread.sleep(1000);
        Assert.assertTrue("Not connect", session.isConnected());
    }

    private void options() throws Exception {
        Thread.sleep(1000);
        Request request = new Request.Builder()
                .uri(URI.create("rtsp://127.0.0.1/NeverPlay.mp3"))
                .method(Method.OPTIONS)
                .build();
        session.send(request);
    }

    private void describe() throws Exception {
        Thread.sleep(1000);
        Request request = new Request.Builder()
                .method(Method.DESCRIBE)
                .uri(URI.create("rtsp://127.0.0.1/NeverPlay.mp3"))
                .build();
        session.send(request);
    }

    private void setup() throws Exception {
        Thread.sleep(1000);
        Request request = new Request.Builder()
                .method(Method.SETUP)
                .uri(URI.create("rtsp://172.17.0.2/NeverPlay.mp3/track1"))
                .addHeader(new TransportHeader.Builder()
                        .specifier(TransportHeader.Specifier.TCP)
                        .broadcastType(TransportHeader.BroadcastType.unicast)
                        .clientPort(50846, 50847)
                        .build())
                .build();
        session.send(request);
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(1000);
    }
}