package io.chengguo.streaming.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class AndroidDecoder extends Decoder {

    private final String mCodecName;
    private MediaCodec mMediaCodec;

    public AndroidDecoder(String codecName) {
        mCodecName = codecName;
    }

    @Override
    public void prepare() throws IOException {
        if (mMediaCodec != null) {
            throw new IllegalStateException("Decoder is prepared");
        }
        MediaFormat format = createMediaFormat();
        String mime = format.getString(MediaFormat.KEY_MIME);
        MediaCodec mediaCodec = createDecoder(mime);
        mediaCodec.configure(format, createPreviewSurface(), null, 0);
        onMediaCodecConfigured(mediaCodec);
        mediaCodec.start();
        mMediaCodec = mediaCodec;
    }

    @Override
    public void feed(byte[] frame) {
        int index = dequeueInputBuffer(0);
        if (index > 0) {
            ByteBuffer buffer = getInputBuffer(index);
            buffer.clear();
            buffer.put(frame);
            queueInputBuffer(index, 0, frame.length, System.nanoTime() / 1000, 0);
        }
    }

    @Override
    public void stop() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
        }
    }

    @Override
    public void release() {
        if (mMediaCodec != null) {
            mMediaCodec.release();
            mMediaCodec = null;
        }
    }

    public ByteBuffer getOutputBuffer(int index) {
        return getCodec().getOutputBuffer(index);
    }

    public ByteBuffer getInputBuffer(int index) {
        return getCodec().getInputBuffer(index);
    }

    public void queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
        getCodec().queueInputBuffer(index, offset, size, presentationTimeUs, flags);
    }

    public int dequeueInputBuffer(long timeoutUs) {
        return getCodec().dequeueInputBuffer(timeoutUs);
    }

    public int dequeueOutputBuffer(MediaCodec.BufferInfo info, long timeoutUs) {
        return getCodec().dequeueOutputBuffer(info, timeoutUs);
    }

    public void releaseOutputBuffer(int index, boolean render) {
        getCodec().releaseOutputBuffer(index, render);
    }

    private MediaCodec getCodec() {
        return Objects.requireNonNull(mMediaCodec, "MediaCodec doesn't prepare");
    }

    private MediaCodec createDecoder(String type) throws IOException {
        if (mCodecName != null) {
            try {
                return MediaCodec.createByCodecName(mCodecName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return MediaCodec.createDecoderByType(type);
    }

    protected Surface createPreviewSurface() {
        return null;
    }


    protected abstract MediaFormat createMediaFormat();

    protected abstract void onMediaCodecConfigured(MediaCodec mediaCodec);
}
