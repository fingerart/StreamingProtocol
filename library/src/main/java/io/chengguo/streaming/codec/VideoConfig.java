package io.chengguo.streaming.codec;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

import io.chengguo.streaming.utils.Utils;

public class VideoConfig {
    private final Builder mBuilder;

    private VideoConfig(Builder builder) {
        mBuilder = builder;
    }

    public MediaFormat toMediaFormat() {
        MediaFormat videoFormat = MediaFormat.createVideoFormat(mBuilder.mime, mBuilder.width, mBuilder.height);
        if (mBuilder.frameRate > 0) {
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mBuilder.frameRate);
        }
        if (mBuilder.bitRate > 0) {
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBuilder.bitRate);
        }
        if (mBuilder.iFrameInterval > 0) {
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        }
        if (mBuilder.csd0 != null) {
            videoFormat.setByteBuffer("csd-0", mBuilder.csd0);
        }
        if (mBuilder.csd1 != null) {
            videoFormat.setByteBuffer("csd-1", mBuilder.csd1);
        }
        return videoFormat;
    }

    public static class Builder {
        String mime;
        int width;
        int height;
        int bitRate;
        int frameRate;
        int iFrameInterval;
        ByteBuffer csd0;
        ByteBuffer csd1;

        public Builder setMime(String mime) {
            this.mime = mime;
            return this;
        }

        public Builder setBitRate(int bitRate) {
            this.bitRate = bitRate;
            return this;
        }

        public Builder setFrameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setiFrameInterval(int iFrameInterval) {
            this.iFrameInterval = iFrameInterval;
            return this;
        }

        public Builder setCsd0(ByteBuffer csd0) {
            this.csd0 = csd0;
            return this;
        }

        public Builder setCsd1(ByteBuffer csd1) {
            this.csd1 = csd1;
            return this;
        }

        public VideoConfig build() {
            if (Utils.isEmpty(mime)) {
                throw new IllegalArgumentException("Mime must not be empty or null");
            }
            return new VideoConfig(this);
        }
    }
}
