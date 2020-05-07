package io.chengguo.streaming.codec;

import android.media.MediaFormat;

import java.nio.ByteBuffer;
import java.util.Arrays;

import io.chengguo.streaming.utils.Bits;
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
            videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(mBuilder.csd0));
        }
        if (mBuilder.csd1 != null) {
            videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(mBuilder.csd1));
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
        byte[] csd0;
        byte[] csd1;

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

        /**
         * sps
         * @param csd0
         * @return
         */
        public Builder setCsd0(byte[] csd0) {
            this.csd0 = csd0;
            return this;
        }

        /**
         * pps
          * @param csd1
         * @return
         */
        public Builder setCsd1(byte[] csd1) {
            this.csd1 = csd1;
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "mime='" + mime + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", bitRate=" + bitRate +
                    ", frameRate=" + frameRate +
                    ", iFrameInterval=" + iFrameInterval +
                    ", csd0=" + Bits.dumpBytesToHex(csd0) +
                    ", csd1=" + Bits.dumpBytesToHex(csd1) +
                    '}';
        }

        public VideoConfig build() {
            if (Utils.isEmpty(mime)) {
                throw new IllegalArgumentException("Mime must not be empty or null");
            }
            System.out.println(this.toString());
            return new VideoConfig(this);
        }
    }
}
