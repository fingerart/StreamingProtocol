package io.chengguo.streaming.rtp;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static io.chengguo.streaming.utils.Bits.getLongByInt;

//@formatter:off
//| RFC 3550
//|
//| 0                   1                   2                   3
//|  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//| +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//| |V=2|P|X|  CC   |M|     PT      |       sequence number         |
//| +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//| |                           timestamp                           |
//| +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//| |           synchronization source (SSRC) identifier            |
//| +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
//| |            contributing source (CSRC) identifiers             |
//| |                             ....                              |
//| +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//@formatter:on

/**
 * RTP报
 */
public class RtpPacket {
    private int version;
    private boolean padding;
    private boolean extension;
    private int csrcCount;
    private boolean marker;
    private int payloadType;
    private int sequenceNumber;
    private long timestamp;
    private long ssrc;
    private long[] csrcs;
    private byte[] payload;

    public int getVersion() {
        return version;
    }

    public boolean isPadding() {
        return padding;
    }

    public boolean isExtension() {
        return extension;
    }

    public int getCsrcCount() {
        return csrcCount;
    }

    public boolean isMarker() {
        return marker;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getSsrc() {
        return ssrc;
    }

    public long[] getCsrcs() {
        return csrcs;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "RtpPacket{" +
                "version=" + version +
                ", padding=" + padding +
                ", extension=" + extension +
                ", csrcCount=" + csrcCount +
                ", marker=" + marker +
                ", payloadType=" + payloadType +
                ", sequenceNumber=" + sequenceNumber +
                ", timestamp=" + timestamp +
                ", ssrc=" + ssrc +
                ", csrcs=" + Arrays.toString(csrcs) +
                ", payload=" + payload.length +
                '}';
    }

    public static class Resolver {
        public static RtpPacket resolve(ByteBuffer buffer) {
            RtpPacket rtpPacket = new RtpPacket();
            byte vpx = buffer.get();
            rtpPacket.version = vpx >> 6 & 0x3;
            rtpPacket.padding = (vpx >> 5 & 0x1) == 1;
            rtpPacket.extension = (vpx >> 4 & 0x1) == 1;
            rtpPacket.csrcCount = vpx & 0xf;
            byte mp = buffer.get();
            rtpPacket.marker = (mp >> 7 & 0x1) == 1;
            rtpPacket.payloadType = mp & 0x7f;
            rtpPacket.sequenceNumber = buffer.getShort();
            rtpPacket.timestamp = getLongByInt(buffer);
            rtpPacket.ssrc = getLongByInt(buffer);
            if (rtpPacket.extension) {
                rtpPacket.csrcs = new long[rtpPacket.csrcCount];
                for (int i = 0; i < rtpPacket.csrcCount; i++) {
                    rtpPacket.csrcs[i] = getLongByInt(buffer);
                }
            }
            byte[] payload = new byte[buffer.remaining()];
            System.arraycopy(buffer.array(), buffer.position(), payload, 0, buffer.remaining());
            rtpPacket.payload = payload;
            return rtpPacket;
        }
    }
}