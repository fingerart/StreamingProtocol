package io.chengguo.live;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Decoder {

    private static final int TIMEOUT = -1;
    private static final String TAG = "Decoder";

    private MediaCodec.BufferInfo bufferInfo;
    private MediaCodec mediaCodec;
    private Callback mCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Decoder() throws IOException {
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_MPEG);
        MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 44100, 2);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 320);
        audioFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        mediaCodec.configure(audioFormat, null, null, 0);
        bufferInfo = new MediaCodec.BufferInfo();
    }

    public void start() {
        mediaCodec.start();
//        inputBuffers = mediaCodec.getInputBuffers();
//        outputBuffers = mediaCodec.getOutputBuffers();
    }

    public void stop() {
        mediaCodec.stop();
    }

    int index;

    public void input(final byte[] data, final int offset, final int length, final long presentationTimeUs) {
        if ((index = mediaCodec.dequeueInputBuffer(TIMEOUT)) >= 0) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
            inputBuffer.put(data, offset, length);
            mediaCodec.queueInputBuffer(index, offset, length, presentationTimeUs, 0);
            System.out.println("Decoder.input");
        }
        if ((index = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT)) >= 0) {
            System.out.println("Decoder.run " + index);
            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(index);
            if (bufferInfo.size > 0 && outputBuffer != null) {
                byte[] b = new byte[bufferInfo.size];
                outputBuffer.get(b);
                if (mCallback != null) {
                    mCallback.onOutput(b, 0, b.length);
                }
                mediaCodec.releaseOutputBuffer(index, false);
            }
        } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            System.out.println("MediaCodec.INFO_OUTPUT_FORMAT_CHANGED");
        }
        System.out.println("Decoder.input#exit " + index);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onOutput(byte[] bytes, int offset, int size);
    }
}
