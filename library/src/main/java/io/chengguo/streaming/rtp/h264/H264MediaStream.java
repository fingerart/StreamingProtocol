package io.chengguo.streaming.rtp.h264;

import io.chengguo.streaming.MediaStream;
import io.chengguo.streaming.codec.h264.SPS;

/**
 * H264媒体流
 * 从RTP中组帧H264流媒体数据
 */
public class H264MediaStream extends MediaStream {

    public String mimeType;
    public int sampleRate;
    public int packetizationMode;
    public SPS sps;

    protected void parseAttr(String shortField, String content) {
        if ("rtpmap".equals(shortField)) {
            String[] value = content.split(" ");
            if (value.length == 2) {
                String[] split = value[1].split("/");
                mimeType = split[0];
                sampleRate = Integer.parseInt(split[1]);
            }
        } else if ("fmtp".equals(shortField)) {
            String[] value = content.split(" ");
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
    }
}