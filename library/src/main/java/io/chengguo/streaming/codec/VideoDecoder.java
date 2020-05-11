package io.chengguo.streaming.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;

/**
 * 视频解码器
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoDecoder extends AndroidDecoder implements Thread.UncaughtExceptionHandler {

    private final VideoConfig mConfig;
    private final Surface mSurface;
    private Thread mOutputWorker;

    public VideoDecoder(VideoConfig config, Surface surface) {
        super(null);
        mConfig = config;
        mSurface = surface;
    }

    @Override
    public void prepare() throws IOException {
        super.prepare();
        mOutputWorker = new Thread(createOutputRunnable());
        mOutputWorker.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mOutputWorker.setName("VideoDecoder");
        mOutputWorker.setUncaughtExceptionHandler(this);
        mOutputWorker.start();
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

    private Runnable createOutputRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                while (true) {

                }
            }
        };
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

    }
}
