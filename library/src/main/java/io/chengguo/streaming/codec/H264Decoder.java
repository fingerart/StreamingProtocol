package io.chengguo.streaming.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class H264Decoder {

    private static final String TAG = "H264Decoder";
    private static final byte[] START_CODE = new byte[]{0, 0, 0, 1};
    private final MediaFormat videoFormat;
    private final DataOutputStream file;
    private Surface mSurface;

    private LinkedBlockingQueue<byte[]> rawRtpPackets = new LinkedBlockingQueue<>();
    private MediaCodec mediaCodec;
    private ByteBuffer[] codecInputBuffers;
    private ByteBuffer[] codecOutputBuffers;
    private boolean decoding;
    private ExecutorService executor;
    private Future<?> outputWorker;
    private Future<?> inputWorker;

    public H264Decoder(Surface surface, int width, int height) throws IOException {
        mSurface = surface;
        executor = Executors.newCachedThreadPool();
        videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 40000);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        file = new DataOutputStream(new FileOutputStream("/sdcard/test.264"));
    }

    private void startDecode() throws IOException {
        decoding = true;
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mediaCodec.configure(videoFormat, mSurface, null, 0);
        mediaCodec.start();
        codecInputBuffers = mediaCodec.getInputBuffers();
        codecOutputBuffers = mediaCodec.getOutputBuffers();

        inputWorker = executor.submit(new Runnable() {
            byte[] fuFrame = new byte[0];

            @Override
            public void run() {
                int index;
                try {
                    while (decoding) {
                        Log.d(TAG, "input.take...");
                        byte[] take = rawRtpPackets.take();
                        Log.d(TAG, "input.dequeue...");
                        //handle h264

                        byte type = (byte) (take[0] & 0x1F);

                        Log.d(TAG, "First Type: " + type);

                        byte[] frame = new byte[0];
                        if (type > 0 && type < 24) {//单包
                            Log.d(TAG, "单包");
                            frame = new byte[4 + take.length];
                            System.arraycopy(START_CODE, 0, frame, 0, 4);
                            System.arraycopy(take, 0, frame, 4, take.length);
                        } else if (type == 24) {//STAP-A 单一时间聚合包
                            Log.d(TAG, "STAP-A 单一时间聚合包");
                            continue;
                        } else if (type == 25) {//STAP-B 单一时间聚合包
                            Log.d(TAG, "STAP-B 单一时间聚合包");
                            continue;
                        } else if (type == 26) {//MTAP16 多时间聚合包
                            Log.d(TAG, "MTAP16 多时间聚合包");
                            continue;
                        } else if (type == 27) {//MTAP24 多时间聚合包
                            Log.d(TAG, "MTAP24 多时间聚合包");
                            continue;
                        } else if (type == 28) {//FU-A 分片包
                            Log.d(TAG, "FU-A 分片包");
                            int fuHeader = take[1] & 0xFF;
                            int startMark = fuHeader >> 7;
                            int endMark = fuHeader >> 6 & 0x1;

                            if (startMark == 1) {//分片的第一个包
                                Log.d(TAG, "FU-A 第一个包");
                                byte fuIndicator = take[0];
                                byte naluType = (byte) (fuHeader & 0x1f);
                                byte naluHeader = (byte) (fuIndicator & 0xE0 | naluType);

                                fuFrame = new byte[4 + 1 + (take.length - 2)];//start code + header + payload
                                System.arraycopy(START_CODE, 0, fuFrame, 0, 4);
                                fuFrame[4] = naluHeader;
                                System.arraycopy(take, 2, fuFrame, 5, take.length - 2);
                                continue;
                            } else {//分片的最后一个包 或者 中间包
                                byte[] _frame = new byte[fuFrame.length + (take.length - 2)];
                                System.arraycopy(fuFrame, 0, _frame, 0, fuFrame.length);
                                System.arraycopy(take, 2, _frame, fuFrame.length, take.length - 2);
                                if (endMark != 1) {//分片的中间包
                                    Log.d(TAG, "FU-A 中间包");
                                    fuFrame = _frame;
                                    continue;
                                } else {//最后一个包 送进解码器
                                    Log.d(TAG, "FU-A 最后一个包");
                                    frame = _frame;
                                    fuFrame = new byte[0];
                                }
                            }
                        } else if (type == 29) {//FU-B 分片包
                            Log.d(TAG, "FU-B 分片包");
                            continue;
                        } else {
                            Log.w(TAG, "NALU header flag `type`(" + type + ") is invalid.");
                            continue;
                        }

                        try {
                            Log.d(TAG, "write file");
                            file.write(frame);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (((index = mediaCodec.dequeueInputBuffer(10)) >= 0)) {
                            Log.d(TAG, "input.dequeue [" + index + "] " + Arrays.toString(frame));
                            ByteBuffer inputBuffer = codecInputBuffers[index];
                            inputBuffer.clear();
                            inputBuffer.put(frame, 0, frame.length);
                            mediaCodec.queueInputBuffer(index, 0, take.length, System.nanoTime(), 0);
                        } else {
                            Log.d(TAG, "input.exit: " + index);
                        }
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
                        mediaCodec.releaseOutputBuffer(index, true);
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

    public void stop() {
        decoding = false;
        try {
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputWorker != null) {
            inputWorker.cancel(true);
        }
        if (outputWorker != null) {
            outputWorker.cancel(true);
        }
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }
    }

    public void input(final byte[] data, final int offset, final int length, final long presentationTimeUs) throws Exception {
        Log.d(TAG, "RTP Payload: " + Arrays.toString(data));
        
        rawRtpPackets.put(data);
        Log.d(TAG, "queue.put: " + rawRtpPackets.size());

        if (!decoding) {
            byte type = (byte) (data[0] & 0x1F);
            if (type == 7) {//SPS
                byte[] sps = new byte[4 + data.length];
                System.arraycopy(START_CODE, 0, sps, 0, 4);
                System.arraycopy(data, 0, sps, 4, data.length);
                videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
            } else if (type == 8) {//PPS
                byte[] pps = new byte[4 + data.length];
                System.arraycopy(START_CODE, 0, pps, 0, 4);
                System.arraycopy(data, 0, pps, 4, data.length);
                videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
            }

            if (videoFormat.containsKey("csd-0") && videoFormat.containsKey("csd-1")) {
                //start decode
                startDecode();
            }
        }


    }
}
