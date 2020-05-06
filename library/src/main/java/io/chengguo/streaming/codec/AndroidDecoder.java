package io.chengguo.streaming.codec;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import androidx.annotation.RequiresApi;

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
        //TODO Surface
        mediaCodec.configure(format, createPreviewSurface(), null, 0);
        onMediaCodecConfigured(mediaCodec);
        mediaCodec.start();
        mMediaCodec = mediaCodec;
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
