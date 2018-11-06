package io.chengguo.live;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Decoder {

    private static final int TIMEOUT = -1;
    private static final String TAG = "Decoder";
    private final ExecutorService executor;

    private LinkedBlockingQueue<byte[]> data = new LinkedBlockingQueue<>();
    private MediaCodec mediaCodec;
    private Callback mCallback;
    private ByteBuffer[] codecInputBuffers;
    private ByteBuffer[] codecOutputBuffers;
    private boolean decoding;
    private Future<?> outputWorker;
    private Future<?> inputWorker;

    public Decoder() throws IOException {
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_MPEG);
        MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 44100, 2);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 337);
        audioFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 10 * 1024);
        mediaCodec.configure(audioFormat, null, null, 0);
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        mediaCodec.start();
        codecInputBuffers = mediaCodec.getInputBuffers();
        codecOutputBuffers = mediaCodec.getOutputBuffers();
        decoding = true;
        outputWorker = executor.submit(new Runnable() {
            @Override
            public void run() {
                int index;
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                while (decoding) {
                    Log.d(TAG, "output.dequeue...");
                    if ((index = mediaCodec.dequeueOutputBuffer(bufferInfo, -1)) >= 0) {
                        Log.d(TAG, "output.dequeue: " + index);
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
                        Log.w(TAG, "output buffers have changed.");
                    } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat oformat = mediaCodec.getOutputFormat();
                        Log.w(TAG, "output format has changed to " + oformat);
                    } else {
                        Log.w(TAG, "dequeueOutputBuffer returned " + index);
                    }
                }
            }
        });
        inputWorker = executor.submit(new Runnable() {
            @Override
            public void run() {
                int index;
                try {
                    while (decoding) {
                        Log.d(TAG, "input.take...");
                        byte[] take = data.take();
                        Log.d(TAG, "input.dequeue...");
                        if (((index = mediaCodec.dequeueInputBuffer(-1)) >= 0)) {
                            Log.d(TAG, "input.dequeue: " + index);
//            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
                            ByteBuffer inputBuffer = codecInputBuffers[index];
                            inputBuffer.clear();
                            inputBuffer.put(take, 4, take.length - 4);
                            mediaCodec.queueInputBuffer(index, 4, take.length - 4, System.nanoTime(), 0);
                        } else {
                            Log.d(TAG, "input.exit: " + index);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        decoding = false;
        inputWorker.cancel(true);
        outputWorker.cancel(true);
        mediaCodec.stop();
        mediaCodec.release();
    }

    public void input(final byte[] data, final int offset, final int length, final long presentationTimeUs) throws Exception {
        this.data.put(data);
        Log.d(TAG, "queue.put: " + this.data.size());
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onOutput(byte[] bytes, int offset, int size);
    }
}
