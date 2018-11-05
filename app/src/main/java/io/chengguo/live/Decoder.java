package io.chengguo.live;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Decoder {

    private static final int TIMEOUT = -1;
    private static final String TAG = "Decoder";

    private MediaCodec mediaCodec;
    private Callback mCallback;
    private ByteBuffer[] codecInputBuffers;
    private ByteBuffer[] codecOutputBuffers;
    private MediaCodec.BufferInfo bufferInfo;
    private boolean decoding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Decoder() throws IOException {
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_MPEG);
        MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 44100, 2);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 337);
        audioFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 10 * 1024);
        mediaCodec.configure(audioFormat, null, null, 0);
    }

    public void start() {
        mediaCodec.start();
        bufferInfo = new MediaCodec.BufferInfo();
        codecInputBuffers = mediaCodec.getInputBuffers();
        codecOutputBuffers = mediaCodec.getOutputBuffers();
        decoding = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (decoding) {
                    Log.d("Decoder.run", decoding + "");
                    if ((index = mediaCodec.dequeueOutputBuffer(bufferInfo, -1)) >= 0) {
                        Log.d("Decoder.output", index + "");
//                ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(index);
                        ByteBuffer outputBuffer = codecOutputBuffers[index];
                        if (bufferInfo.size > 0) {
                            byte[] buff = new byte[bufferInfo.size];
                            outputBuffer.position(bufferInfo.offset);
                            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                            outputBuffer.get(buff);
                            if (mCallback != null) {
                                mCallback.onOutput(buff, 0, buff.length);
                            }
                            mediaCodec.releaseOutputBuffer(index, true);
                        }
                    } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        codecOutputBuffers = mediaCodec.getOutputBuffers();
                        Log.d(TAG, "output buffers have changed.");
                    } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat oformat = mediaCodec.getOutputFormat();
                        Log.d(TAG, "output format has changed to " + oformat);
                    } else {
                        Log.d(TAG, "dequeueOutputBuffer returned " + index);
                    }
                }
            }
        }).start();
    }

    public void stop() {
        decoding = false;
        mediaCodec.stop();
        mediaCodec.release();
    }

    int index;

    public void input(final byte[] data, final int offset, final int length, final long presentationTimeUs) throws Exception {
        if (((index = mediaCodec.dequeueInputBuffer(5000)) >= 0)) {
            System.out.println("Decoder.input" + index);
//            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
            ByteBuffer inputBuffer = codecInputBuffers[index];
            inputBuffer.clear();
            inputBuffer.put(data, 4, length - 4);
            mediaCodec.queueInputBuffer(index, 0, length - 4, System.nanoTime(), 0);
        } else {
            System.out.println("Decoder.input#exit " + index);
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onOutput(byte[] bytes, int offset, int size);
    }
}
