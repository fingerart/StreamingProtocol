package io.chengguo.live;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class H264Decoder {

    private static final int TIMEOUT = -1;
    private static final String TAG = "H264Decoder";
    private final ExecutorService executor;

    private LinkedBlockingQueue<byte[]> data = new LinkedBlockingQueue<>();
    private MediaCodec mediaCodec;
    private Callback mCallback;
    private ByteBuffer[] codecInputBuffers;
    private ByteBuffer[] codecOutputBuffers;
    private boolean decoding;
    private Future<?> outputWorker;
    private Future<?> inputWorker;

    public H264Decoder(Surface surface, int width, int height) throws IOException {
        MediaFormat videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
//        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 337);
//        videoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
//        videoFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 10 * 1024);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mediaCodec.configure(videoFormat, surface, null, 0);
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        mediaCodec.start();
        codecInputBuffers = mediaCodec.getInputBuffers();
        codecOutputBuffers = mediaCodec.getOutputBuffers();
        decoding = true;
        inputWorker = executor.submit(new Runnable() {
            @Override
            public void run() {
                int index;
                try {
                    while (decoding) {
                        Log.d(TAG, "input.take...");
                        byte[] take = data.take();
                        Log.d(TAG, "input.dequeue...");
//                        if (((index = mediaCodec.dequeueInputBuffer(-1)) >= 0)) {
//                            Log.d(TAG, "input.dequeue: " + index);
////            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
//                            ByteBuffer inputBuffer = codecInputBuffers[index];
//                            inputBuffer.clear();
//                            inputBuffer.put(take, 0, take.length);
//                            mediaCodec.queueInputBuffer(index, 0, take.length, System.nanoTime(), 0);
//                        } else {
//                            Log.d(TAG, "input.exit: " + index);
//                        }
//                        ----------------------------------------------------------
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
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
                        if (bufferInfo.size > 0) {
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
    }

    private int KMPMatch(byte[] pattern, byte[] bytes, int start, int remain) {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int[] lsp = computeLspTable(pattern);

        int j = 0;  // Number of chars matched in pattern
        for (int i = start; i < remain; i++) {
            while (j > 0 && bytes[i] != pattern[j]) {
                // Fall back in the pattern
                j = lsp[j - 1];  // Strictly decreasing
            }
            if (bytes[i] == pattern[j]) {
                // Next char matched, increment position
                j++;
                if (j == pattern.length)
                    return i - (j - 1);
            }
        }
        return -1;  // Not found
    }
    private int[] computeLspTable(byte[] pattern) {
        int[] lsp = new int[pattern.length];
        lsp[0] = 0;  // Base case
        for (int i = 1; i < pattern.length; i++) {
            // Start by assuming we're extending the previous LSP
            int j = lsp[i - 1];
            while (j > 0 && pattern[i] != pattern[j])
                j = lsp[j - 1];
            if (pattern[i] == pattern[j])
                j++;
            lsp[i] = j;
        }
        return lsp;
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
