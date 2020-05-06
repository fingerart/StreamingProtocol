package io.chengguo.streaming.rtp.h264;

import io.chengguo.streaming.MediaStream;
import io.chengguo.streaming.codec.AndroidDecoder;
import io.chengguo.streaming.codec.Decoder;
import io.chengguo.streaming.codec.VideoConfig;
import io.chengguo.streaming.codec.VideoDecoder;
import io.chengguo.streaming.codec.h264.SPS;
import io.chengguo.streaming.rtsp.sdp.SDP;

/**
 * H264媒体流
 * 从RTP中组帧H264流媒体数据，送入解码器
 */
public class H264MediaStream extends MediaStream {

    private final SDP mSdp;
    public int sampleRate;
    public int packetizationMode;
    public SPS sps;

    public H264MediaStream(SDP sdp) {
        mSdp = sdp;
        init();
    }

    private void init() {
        VideoConfig.Builder vBuilder = new VideoConfig.Builder();
        for (SDP.MediaDescription mediaDescription : mSdp.getMediaDescriptions()) {
            if (mediaDescription.isVideo()) {
                // rtpmap:96 H264/90000
                String rtpmap = mediaDescription.attributes.get("rtpmap");
                if (rtpmap != null) {
                    String[] value = rtpmap.split(" ");
                    if (value.length == 2) {
                        String[] split = value[1].split("/");
                        vBuilder.setBitRate(Integer.parseInt(split[1]));
                    }
                }
                String fmtp = mediaDescription.attributes.get("fmtp");
                if (fmtp != null) {
                    String[] value = fmtp.split(" ");
                    if (value.length == 2) {
                        String[] split = value[1].split(";");
                        for (int i = 0; i < split.length; i++) {
                            String[] items = split[i].split("=", 2);
                            if (items.length == 2) {
                                if ("packetization-mode".equals(items[0])) {
                                    packetizationMode = Integer.parseInt(items[1]);
                                } else if ("sprop-parameter-sets".equals(items[0])) {
                                    String[] sets = items[1].split(",");
                                    if (sets.length == 2) {
                                        sps = SPS.valueOf(sets[0]);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void feed(byte[] data) {

    }

    @Override
    public Decoder getDecoder() {
        return null;
    }
}