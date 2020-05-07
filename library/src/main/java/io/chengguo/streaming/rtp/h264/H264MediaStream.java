package io.chengguo.streaming.rtp.h264;

import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import io.chengguo.streaming.MediaStream;
import io.chengguo.streaming.codec.Decoder;
import io.chengguo.streaming.codec.VideoConfig;
import io.chengguo.streaming.codec.VideoDecoder;
import io.chengguo.streaming.codec.h264.SPS;
import io.chengguo.streaming.exceptions.NotSupportException;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.sdp.SDP;

/**
 * H264媒体流
 * 从RTP中组帧H264流媒体数据，送入解码器
 */
public class H264MediaStream extends MediaStream {
    public static final int TYPE = 96;

    private static final String TAG = H264MediaStream.class.getSimpleName();
    private static final byte[] START_CODE = new byte[]{0, 0, 0, 1};
    private static final byte[] START_CODE_SLICE = new byte[]{0, 0, 1};
    private final Surface mSurface;
    private byte[] sliceCache = new byte[0];
    private final SDP mSdp;
    public int sampleRate;
    public int packetizationMode;
    public SPS sps;
    public byte[] pps;
    private VideoDecoder mVideoDecoder;

    public H264MediaStream(@NonNull SDP sdp, @NonNull Surface surface) {
        mSdp = Objects.requireNonNull(sdp, "sdp must not is null");
        mSurface = Objects.requireNonNull(surface, "surface must not is null");
    }

    @Override
    public void prepare() throws Exception {
        VideoConfig.Builder vBuilder = new VideoConfig.Builder();
        SDP.MediaDescription md = mSdp.findVideoMediaDescription();
        if (md == null) {
            throw new IllegalStateException("Not found video media description");
        }
        // rtpmap:96 H264/90000
        String rtpmap = md.attributes.get("rtpmap");
        if (rtpmap != null) {
            String[] value = rtpmap.split(" ");
            if (value.length == 2) {
                String[] split = value[1].split("/");
                if (Integer.parseInt(split[0]) != TYPE) {
                    throw new IllegalStateException("media type is not " + TYPE);
                }
                vBuilder.setBitRate(Integer.parseInt(split[1]));
            }
        }
        String fmtp = md.attributes.get("fmtp");
        if (fmtp != null) {
            String[] value = fmtp.split(" ");
            if (value.length == 2) {
                String[] split = value[1].split(";");
                for (int i = 0; i < split.length; i++) {
                    String[] items = split[i].split("=", 2);
                    if (items.length == 2) {
                        if ("packetization-mode".equals(items[0])) {
                            packetizationMode = Integer.parseInt(items[1]);
                        } else if ("sprop-parameter-sets".equals(items[0])) {
                            String[] sets = items[1].split(",");
                            if (sets.length == 2) {
                                sps = SPS.valueOf(sets[0]);
                                vBuilder.setCsd0(sps.raw);
                                pps = Base64.decode(sets[1], Base64.DEFAULT);
                                vBuilder.setCsd1(pps);
                                vBuilder.setWidth(sps.width);
                                vBuilder.setHeight(sps.height);
                                vBuilder.setFrameRate(30);
                            }
                        }
                    }
                }
            }
        }
        mVideoDecoder = new VideoDecoder(vBuilder.build(), mSurface);
        mVideoDecoder.prepare();
    }

    private byte[] fuFrame = new byte[0];

    @Override
    public void feed(RtpPacket packet) {
        byte[] fragment = packet.getPayload();
        // 解H264帧
        byte nalutype = getNALUType(fragment[0]);
        if (nalutype > 0 && nalutype < 24) {// 1~23 单个 NAL 单元包
            handNALU(nalutype, fragment);
        } else if (nalutype == 24) {//STAP-A 单一时间聚合包
            throw new NotSupportException("STAP-A 单一时间聚合包");
        } else if (nalutype == 25) {//STAP-B 单一时间聚合包
            throw new NotSupportException("STAP-B 单一时间聚合包");
        } else if (nalutype == 26) {//MTAP16 多时间聚合包
            throw new NotSupportException("MTAP16 多时间聚合包");
        } else if (nalutype == 27) {//MTAP24 多时间聚合包
            throw new NotSupportException("MTAP24 多时间聚合包");
        } else if (nalutype == 28) {//FU-A 分片包
            Log.d(TAG, "FU-A 分片包");
            int fuHeader = fragment[1] & 0xFF;
            int startMark = fuHeader >> 7;
            int endMark = fuHeader >> 6 & 0x1;

            if (startMark == 1) {//分片的第一个包
                Log.d(TAG, "FU-A 第一个包");
                byte fuIndicator = fragment[0];
                byte naluType = (byte) (fuHeader & 0x1f);
                byte naluHeader = (byte) (fuIndicator & 0xE0 | naluType);

                fuFrame = new byte[1 + (fragment.length - 2)];//start code + header + payload
                fuFrame[0] = naluHeader;
                System.arraycopy(fragment, 2, fuFrame, 1, fragment.length - 2);
                return;
            } else {//分片的最后一个包 或者 中间包
                byte[] _frame = new byte[fuFrame.length + (fragment.length - 2)];
                System.arraycopy(fuFrame, 0, _frame, 0, fuFrame.length);
                System.arraycopy(fragment, 2, _frame, fuFrame.length, fragment.length - 2);
                if (endMark != 1) {//分片的中间包
                    Log.d(TAG, "FU-A 中间包");
                    fuFrame = _frame;
                    return;
                } else {//最后一个包
                    Log.d(TAG, "FU-A 最后一个包");
                    handNALU(getNALUType(_frame[0]), _frame);
                    fuFrame = new byte[0];
                }
            }
        } else if (nalutype == 29) {//FU-B 分片包
            throw new NotSupportException("FU-B 分片包");
        } else {
            throw new NotSupportException("NALU header flag `type`(" + nalutype + ") is invalid.");
        }
    }

    @Override
    public Decoder getDecoder() {
        return mVideoDecoder;
    }

    /**
     * 处理NALU
     *
     * @param type
     * @param data
     */
    private void handNALU(int type, byte[] data) {
        if (type >= 1 && type <= 6) {//关键帧、非关键帧等，把这些Slice存起来
            byte[] slice = new byte[sliceCache.length + START_CODE_SLICE.length + data.length];
            System.arraycopy(sliceCache, 0, slice, 0, sliceCache.length);
            System.arraycopy(START_CODE_SLICE, 0, slice, sliceCache.length, START_CODE_SLICE.length);
            System.arraycopy(data, 0, slice, sliceCache.length + START_CODE_SLICE.length, data.length);
            sliceCache = slice;
        } else if (type == 9) {//遇到分割符，将存储的Slice送入解码器解码
//            intoDecoder(sliceCache);
            getDecoder().feed(sliceCache);
            sliceCache = new byte[0];
        }

        if (type >= 7 && type < 24) {//单包
            Log.d(TAG, "单包");
            byte[] frame = new byte[4 + data.length];
            System.arraycopy(START_CODE, 0, frame, 0, 4);
            System.arraycopy(data, 0, frame, 4, data.length);
//            intoDecoder(frame);
            getDecoder().feed(sliceCache);
        }
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
}