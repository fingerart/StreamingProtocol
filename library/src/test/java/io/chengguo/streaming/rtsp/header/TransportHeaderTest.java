package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by fingerart on 2018-09-09.
 */
public class TransportHeaderTest {

    @Test
    public void testBuild() {
        TransportHeader transportHeader = new TransportHeader.Builder()
                .specifier(TransportHeader.Specifier.TCP)
                .broadcastType(TransportHeader.BroadcastType.unicast)
                .destination("192.168.0.2")
                .source("110.110.110")
                .clientPort(3240, 3241)
                .serverPort(6970, 6971)
                .build();
        assertResult(transportHeader);
    }

    @Test
    public void testConstructor_raw() {
        String raw = "Transport: RTP/AVP/TCP;unicast;destination=192.168.0.2;source=110.110.110;client_port=3240-3241;server_port=6970-6971;mode=PLAY;";
        TransportHeader transportHeader = new TransportHeader(raw);
        assertResult(transportHeader);
    }

    private void assertResult(TransportHeader transportHeader) {
        assertThat(transportHeader.getSpecifier(), is(TransportHeader.Specifier.TCP));
        assertThat(transportHeader.getBroadcastType(), is(TransportHeader.BroadcastType.unicast));
        assertThat(transportHeader.getDestination(), is("192.168.0.2"));
        assertThat(transportHeader.getSource(), is("110.110.110"));
        assertThat(transportHeader.getClientPort(), is(new TransportHeader.PairPort(3240, 3241)));
        assertThat(transportHeader.getServerPort(), is(new TransportHeader.PairPort(6970, 6971)));
    }
}