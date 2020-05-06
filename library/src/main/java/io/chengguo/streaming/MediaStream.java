package io.chengguo.streaming;

import io.chengguo.streaming.codec.Decoder;

public abstract class MediaStream {
    public abstract void feed(byte[] data);
    public abstract Decoder getDecoder();
}
