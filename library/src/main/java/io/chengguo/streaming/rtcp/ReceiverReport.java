package io.chengguo.streaming.rtcp;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.chengguo.streaming.utils.Bits;

//@formatter:off
//|        0                   1                   2                   3
//|         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//| header |V=2|P|    RC   |   PT=RR=201   |             length            |
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|        |                     SSRC of packet sender                     |
//|        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
//| report |                 SSRC_1 (SSRC of first source)                 |
//| block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|   1    | fraction lost |       cumulative number of packets lost       |
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|        |           extended highest sequence number received           |
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|        |                      interarrival jitter                      |
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|        |                         last SR (LSR)                         |
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|        |                   delay since last SR (DLSR)                  |
//|        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
//| report |                 SSRC_2 (SSRC of second source)                |
//| block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//|   2    :                               ...                             :
//|        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
//|        |                  profile-specific extensions                  |
//|        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//@formatter:on

/**
 * 接受端报告
 */
public class ReceiverReport implements IPacket {
    public static final byte PACKET_TYPE = (byte) 201;

    private int version;//2bit
    private boolean padding;//1bit
    private int counter;//
    private int pt;
    private int length;
    private long ssrcSender;
    private List<ReportBlock> reportBlocks = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isPadding() {
        return padding;
    }

    public void setPadding(boolean padding) {
        this.padding = padding;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getPt() {
        return pt;
    }

    public void setPt(int pt) {
        this.pt = pt;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSsrcSender() {
        return ssrcSender;
    }

    public void setSsrcSender(long ssrcSender) {
        this.ssrcSender = ssrcSender;
    }

    public List<ReportBlock> getReportBlocks() {
        return reportBlocks;
    }

    public void setReportBlocks(List<ReportBlock> reportBlocks) {
        this.reportBlocks = reportBlocks;
    }

    @Override
    public byte[] toRaw() {
        ByteBuffer buffer = ByteBuffer.allocate(8 + (ReportBlock.SIZE * reportBlocks.size()));
        byte v = (byte) ((((version & 0x3) << 6) | (((padding ? 1 : 0) & 0x1) << 5) | (counter & 0x1f)) & 0xff);
        buffer.put(v);
        buffer.put((byte) (pt & 0xff));
        buffer.put(Bits.intToByteArray(length), 2, 2);
        buffer.put(Bits.longToByteArray(ssrcSender), 4, 4);
        // TODO: 2018/11/6 report block

        return buffer.array();
    }

    public static class Resolver {

        public static IPacket resolve(ByteBuffer buffer) {
            ReceiverReport receiverReport = new ReceiverReport();
            return receiverReport;
        }
    }
}