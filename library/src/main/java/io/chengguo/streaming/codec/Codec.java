package io.chengguo.streaming.codec;

import java.io.IOException;

public interface Codec {
    void prepare() throws IOException;

    void stop();

    void release();
}
