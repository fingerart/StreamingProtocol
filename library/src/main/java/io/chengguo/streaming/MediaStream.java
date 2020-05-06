package io.chengguo.streaming;

import io.chengguo.streaming.codec.Decoder;
import io.chengguo.streaming.rtp.RtpPacket;

public abstract class MediaStream {


    public abstract void feed(RtpPacket packet);
    public abstract Decoder getDecoder();

    public void prepare() {

    }
}
