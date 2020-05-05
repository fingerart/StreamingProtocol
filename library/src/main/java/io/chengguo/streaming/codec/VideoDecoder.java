package io.chengguo.streaming.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * 视频解码器
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoDecoder extends AndroidDecoder {

    private final VideoConfig mConfig;

    public VideoDecoder(VideoConfig config) {
        super(null);
        mConfig = config;
    }

    @Override
    protected MediaFormat createMediaFormat() {
        return mConfig.toMediaFormat();
    }

    @Override
    protected void onMediaCodecConfigured(MediaCodec mediaCodec) {
    }
}
