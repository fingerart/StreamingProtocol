package io.chengguo.streaming.rtsp.sdp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SDPTest {

    @Test
    public void testFrom() {
        String input = "v=0\r\n" +
                "o=- 1567231587249627 1 IN IP4 172.17.0.2\r\n" +
                "s=H.264 Video, streamed by the LIVE555 Media Server\r\n" +
                "i=bipbop-gear1-all.264\r\n" +
                "t=0 0\r\n" +
                "a=tool:LIVE555 Streaming Media v2017.05.29\r\n" +
                "a=type:broadcast\r\n" +
                "a=control:*\r\n" +
                "a=range:npt=0-\r\n" +
                "a=x-qt-text-nam:H.264 Video, streamed by the LIVE555 Media Server\r\n" +
                "a=x-qt-text-inf:bipbop-gear1-all.264\r\n" +
                "m=video 0 RTP/AVP 96\r\n" +
                "c=IN IP4 0.0.0.0\r\n" +
                "b=AS:500\r\n" +
                "a=rtpmap:96 H264/90000\r\n" +
                "a=fmtp:96 packetization-mode=1;profile-level-id=42E00B;sprop-parameter-sets=J0LgC6kYYJ2ANQYBBrbCte98BA==,KN4JiA==\r\n" +
                "a=control:track1\r\n";
        H264SDP sdp = new H264SDP();
        sdp.from(input);
        System.out.println(sdp);
    }
}