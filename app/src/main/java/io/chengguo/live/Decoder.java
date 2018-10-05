package io.chengguo.live;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Decoder {

    private MediaCodec decoder;

    public Decoder() {

    }

    public void init() throws IOException {
        decoder = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_MPEG);
        MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 44100, 2);
        decoder.configure(audioFormat, null, null, 0);

        ByteBuffer[] inputBuffers = decoder.getInputBuffers();
        ByteBuffer[] outputBuffers = decoder.getOutputBuffers();


    }

}
