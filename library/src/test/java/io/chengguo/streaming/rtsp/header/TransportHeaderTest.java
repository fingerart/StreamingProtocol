package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

/**
 * Created by fingerart on 2018-09-09.
 */
public class TransportHeaderTest {

    @Test
    public void build() throws Exception {
        TransportHeader transportHeader = new TransportHeader.Builder()
                .specifier(TransportHeader.Specifier.TCP)
                .broadcastType(TransportHeader.BroadcastType.unicast)
                .destination("192.168.0.2")
                .source("110.110.110")
                .clientPort(3240, 3241)
                .serverPort(6970, 6971)
                .build();
        System.out.println(transportHeader);
    }

    @Test
    public void resolve() throws Exception {
        String s = "Transport: RTP/AVP/TCP;unicast;destination=192.168.0.2;source=110.110.110;client_port=3240-3241;server_port=6970-6971;mode=PLAY;";

    }
}