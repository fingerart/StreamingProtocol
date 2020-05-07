package io.chengguo.streaming.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.RequiresApi;

/**
 * 视频解码器
 */
public class VideoDecoder extends AndroidDecoder {

    private final VideoConfig mConfig;
    private final Surface mSurface;

    public VideoDecoder(VideoConfig config, Surface surface) {
        super(null);
        mConfig = config;
        mSurface = surface;
    }

    @Override
    protected Surface createPreviewSurface() {
        return mSurface;
    }

    @Override
    protected MediaFormat createMediaFormat() {
        return mConfig.toMediaFormat();
    }

    @Override
    protected void onMediaCodecConfigured(MediaCodec mediaCodec) {
    }
}
