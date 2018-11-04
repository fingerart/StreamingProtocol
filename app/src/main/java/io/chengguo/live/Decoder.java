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

    private MediaCodec mediaCodec;
    private Callback mCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Decoder() throws IOException {
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_MPEG);
        MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 44100, 2);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 44100*16*2/1024);
        audioFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 10 * 1024);
        mediaCodec.configure(audioFormat, null, null, 0);
    }

    public void start() {
        mediaCodec.start();
    }

    public void stop() {
        mediaCodec.stop();
    }

    int index;

    public void input(final byte[] data, final int offset, final int length, final long presentationTimeUs) {
        try {
            if ((index = mediaCodec.dequeueInputBuffer(-1)) >= 0) {
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
                inputBuffer.clear();
                inputBuffer.put(data, offset, length);
                mediaCodec.queueInputBuffer(index, offset, length, System.nanoTime(), 0);
                System.out.println("Decoder.input");
            }
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            while ((index = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000)) >= 0) {
                System.out.println("Decoder.run " + index);
                ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(index);
                if (bufferInfo.size > 0 && outputBuffer != null) {
                    byte[] buff = new byte[bufferInfo.size];
                    outputBuffer.position(bufferInfo.offset);
                    outputBuffer.limit(bufferInfo.offset+bufferInfo.size);
                    outputBuffer.get(buff);
                    if (mCallback != null) {
                        mCallback.onOutput(buff, 0, buff.length);
                    }
                    mediaCodec.releaseOutputBuffer(index, false);
                }
            }
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                System.out.println("MediaCodec.INFO_OUTPUT_FORMAT_CHANGED");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
