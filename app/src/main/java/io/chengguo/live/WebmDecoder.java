package io.chengguo.live;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaFormat.MIMETYPE_VIDEO_AVC;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WebmDecoder {
    private final MediaCodec mediaCodec;
    private ByteBuffer[] inputBuffers;

    public WebmDecoder(Surface surface) throws IOException {
        mediaCodec = MediaCodec.createDecoderByType(MIMETYPE_VIDEO_AVC);
        MediaFormat format = MediaFormat.createVideoFormat(MIMETYPE_VIDEO_AVC, 848, 480);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        mediaCodec.configure(format, surface, null, 0);
    }

    public void start() {
        mediaCodec.start();
        inputBuffers = mediaCodec.getInputBuffers();
    }

    public void decode(byte[] byteBuffer, int offset, int length, long presentationTimeUs) {
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);//获取输入缓冲区的索引
        if (inputBufferIndex >= 0) {
            System.out.println("decode input: " + inputBufferIndex);
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(byteBuffer);//先获取缓冲区，再放入值
            mediaCodec.queueInputBuffer(inputBufferIndex, offset, length, presentationTimeUs, 0);//四个参数，第一个是输入缓冲区的索引，第二个是放入的数据大小，第三个是时间戳，保证递增就是
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000);
        while (outputBufferIndex >= 0) {
            System.out.println("decode output: " + outputBufferIndex);
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);//释放缓冲区解码的数据到surfaceview，一般到了这一步，surfaceview上就有画面了
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000);
        }
    }

}
