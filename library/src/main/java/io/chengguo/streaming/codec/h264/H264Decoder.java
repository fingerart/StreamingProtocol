package io.chengguo.streaming.codec.h264;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.annotation.RequiresApi;
import io.chengguo.streaming.exceptions.NotSupportException;
import io.chengguo.streaming.utils.Bits;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class H264Decoder {

    private static final String TAG = "H264Decoder";
    private static final byte[] START_CODE = new byte[]{0, 0, 0, 1};
    private static final byte[] START_CODE_SLICE = new byte[]{0, 0, 1};
    private final MediaFormat videoFormat;
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
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 90000);
//        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
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
                try {
                    while (decoding) {
                        Log.d(TAG, "input.take...");
                        byte[] take = rawRtpPackets.take();
                        Log.d(TAG, "input.dequeue...");
                        //handle h264

                        byte type = getNALUType(take[0]);

                        Log.d(TAG, "First Type: " + type);

                        if (type > 0 && type < 24) {// 1~23 单个 NAL 单元包
                            handNALU(type, take);
                        } else if (type == 24) {//STAP-A 单一时间聚合包
                            throw new NotSupportException("STAP-A 单一时间聚合包");
                        } else if (type == 25) {//STAP-B 单一时间聚合包
                            throw new NotSupportException("STAP-B 单一时间聚合包");
                        } else if (type == 26) {//MTAP16 多时间聚合包
                            throw new NotSupportException("MTAP16 多时间聚合包");
                        } else if (type == 27) {//MTAP24 多时间聚合包
                            throw new NotSupportException("MTAP24 多时间聚合包");
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

                                fuFrame = new byte[1 + (take.length - 2)];//start code + header + payload
                                fuFrame[0] = naluHeader;
                                System.arraycopy(take, 2, fuFrame, 1, take.length - 2);
                                continue;
                            } else {//分片的最后一个包 或者 中间包
                                byte[] _frame = new byte[fuFrame.length + (take.length - 2)];
                                System.arraycopy(fuFrame, 0, _frame, 0, fuFrame.length);
                                System.arraycopy(take, 2, _frame, fuFrame.length, take.length - 2);
                                if (endMark != 1) {//分片的中间包
                                    Log.d(TAG, "FU-A 中间包");
                                    fuFrame = _frame;
                                    continue;
                                } else {//最后一个包
                                    Log.d(TAG, "FU-A 最后一个包");
                                    handNALU(getNALUType(_frame[0]), _frame);
                                    fuFrame = new byte[0];
                                }
                            }
                        } else if (type == 29) {//FU-B 分片包
                            throw new NotSupportException("FU-B 分片包");
                        } else {
                            throw new NotSupportException("NALU header flag `type`(" + type + ") is invalid.");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    stop();
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

    /**
     * 获取数据中的type
     *
     * @param take
     * @return
     */
    private byte getNALUType(byte take) {
        return (byte) (take & 0x1F);
    }

    private byte[] sliceCache = new byte[0];

    /**
     * 处理NALU
     *
     * @param type
     * @param data
     */
    private void handNALU(int type, byte[] data) {
        if (type >= 1 && type <= 6) {//把这些Slice存起来
            byte[] slice = new byte[sliceCache.length + START_CODE_SLICE.length + data.length];
            System.arraycopy(sliceCache, 0, slice, 0, sliceCache.length);
            System.arraycopy(START_CODE_SLICE, 0, slice, sliceCache.length, START_CODE_SLICE.length);
            System.arraycopy(data, 0, slice, sliceCache.length + START_CODE_SLICE.length, data.length);
            sliceCache = slice;
        } else if (type == 9) {//将存储的Slice送入解码器
            intoDecoder(sliceCache);
            sliceCache = new byte[0];
        }

        if (type >= 7 && type < 24) {//单包
            Log.d(TAG, "单包");
            byte[] frame = new byte[4 + data.length];
            System.arraycopy(START_CODE, 0, frame, 0, 4);
            System.arraycopy(data, 0, frame, 4, data.length);
            intoDecoder(frame);
        }
    }

    /**
     * 送入解码器
     *
     * @param frame
     */
    private void intoDecoder(byte[] frame) {
        int index;
        if ((index = mediaCodec.dequeueInputBuffer(-1)) >= 0) {
            Log.d(TAG, "input.dequeue [" + index + "] \r\n" + Bits.dumpBytesToHex(frame));
            ByteBuffer inputBuffer = codecInputBuffers[index];
            inputBuffer.clear();
            inputBuffer.put(frame, 0, frame.length);
            mediaCodec.queueInputBuffer(index, 0, frame.length, System.nanoTime(), 0);
        } else {
            Log.d(TAG, "input.exit: " + index);
        }
    }

    public void stop() {
        decoding = false;
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

    public void input(byte[] data, final long presentationTimeUs, boolean marker) throws Exception {
        Log.d(TAG, "RTP Payload: \r\n" + Bits.dumpBytesToHex(data));

        if (decoding) {
            rawRtpPackets.put(data);
            Log.d(TAG, "queue.put: " + rawRtpPackets.size());
        } else {
            byte type = (byte) (data[0] & 0x1F);
            if (type == 7) {//SPS
                byte[] sps = new byte[4 + data.length];
                System.arraycopy(START_CODE, 0, sps, 0, 4);
                System.arraycopy(data, 0, sps, 4, data.length);
                videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
                System.out.println("SPS:\r\n" + Bits.dumpBytesToHex(data));
                System.out.println("SPS:\r\n" + java.util.Base64.getEncoder().encodeToString(data));
            } else if (type == 8) {//PPS
                byte[] pps = new byte[4 + data.length];
                System.arraycopy(START_CODE, 0, pps, 0, 4);
                System.arraycopy(data, 0, pps, 4, data.length);
                videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
                System.out.println("PPS:\r\n" + Bits.dumpBytesToHex(data));
                System.out.println("PPS:\r\n" + java.util.Base64.getEncoder().encodeToString(data));
            }

            if (videoFormat.containsKey("csd-0") && videoFormat.containsKey("csd-1")) {
                //start decode
                startDecode();
            }
        }
    }
}
