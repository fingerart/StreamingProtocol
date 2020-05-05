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
        SDP sdp = new SDP();
        sdp.parse(input);
        System.out.println(sdp);
    }

    @Test
    public void testSDP() {
        String input = "v=0 \n" +
                "o=- 1556797244727851 1 IN IP4 172.17.0.2 \n" +
                "s=Matroska video+audio+(optional)subtitles, streamed by the LIVE555 Media Server \n" +
                "i=Tanya.webm \n" +
                "t=0 0 \n" +
                "a=tool:LIVE555 Streaming Media v2017.05.29 \n" +
                "a=type:broadcast \n" +
                "a=control:* \n" +
                "a=x-qt-text-nam:Matroska video+audio+(optional)subtitles, streamed by the LIVE555 Media Server \n" +
                "a=x-qt-text-inf:Tanya.webm \n" +
                "m=video 0 RTP/AVP 96 \n" +
                "c=IN IP4 0.0.0.0 \n" +
                "b=AS:500 \n" +
                "a=rtpmap:96 H264/90000 \n" +
                "a=fmtp:96 packetization-mode=1;profile-level-id=42C033;sprop-parameter-sets=Z0LAM6tAWgk0IAAAAwAgAAAGUeMGVA==,aM48gA== \n" +
                "a=control:track1 \n" +
                "m=audio 0 RTP/AVP 97 \n" +
                "c=IN IP4 0.0.0.0 \n" +
                "b=AS:128 \n" +
                "a=rtpmap:97 VORBIS/44100/2 \n" +
                "a=range:npt=0-1269.120 \n" +
                "a=fmtp:97 configuration=AAAAAfrK3hBsAh4iA\n" +
                "a=control:track2";
        SDP sdp = new SDP();
        sdp.parse(input);
        System.out.println(sdp);
    }
}