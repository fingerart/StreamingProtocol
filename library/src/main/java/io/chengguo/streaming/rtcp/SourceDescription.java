package io.chengguo.streaming.rtcp;

import java.nio.ByteBuffer;
import java.util.Arrays;

//@formatter:off
//        0                   1                   2                   3
//         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// header |V=2|P|    SC   |  PT=SDES=202  |             length            |
//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
// chunk  |                          SSRC/CSRC_1                          |
//   1    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |                           SDES items                          |
//        |                              ...                              |
//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
// chunk  |                          SSRC/CSRC_2                          |
//   2    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |                           SDES items                          |
//        |                              ...                              |
//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
//@formatter:on

/**
 * SDES 源点描述
 */
public class SourceDescription implements IPacket {
    public static final byte PACKET_TYPE = (byte) 202;

    private int version;
    private boolean padding;
    private int counter;
    private int pt;
    private int length;
    private SourceChunk[] sources;

    @Override
    public byte[] toRaw() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "SourceDescription{" +
                "version=" + version +
                ", padding=" + padding +
                ", counter=" + counter +
                ", pt=" + pt +
                ", length=" + length +
                ", sources=" + Arrays.toString(sources) +
                '}';
    }

    public static class Resolver {

        public static SourceDescription resolve(ByteBuffer buffer) {
            SourceDescription sourceDescription = new SourceDescription();
            byte vpc = buffer.get();
            sourceDescription.version = vpc >> 6 & 0x3;
            sourceDescription.padding = (vpc >> 5 & 0x1) == 1;
            sourceDescription.counter = vpc & 0x1f;
            sourceDescription.pt = buffer.get() & 0xff;
            sourceDescription.length = buffer.getShort();

            // TODO: 2019/1/6 直接读取暂不处理
            buffer.get(new byte[sourceDescription.length * 4]);

            return sourceDescription;
        }
    }
}
