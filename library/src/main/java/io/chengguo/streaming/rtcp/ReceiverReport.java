package io.chengguo.streaming.rtcp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.chengguo.streaming.rtsp.IMessage;
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
public class ReceiverReport implements IPacket, IMessage {
    public static final byte PACKET_TYPE = (byte) 201;

    private int version = 2;//2bit
    private boolean padding;//1bit
    private int counter;//
    private int pt = PACKET_TYPE;
    private int length = 1;
    private long ssrcSender;
    private List<ReportBlock> reportBlocks = new ArrayList<>();

    public ReceiverReport() {
    }

    public ReceiverReport(long ssrcSender) {
        this.ssrcSender = ssrcSender;
    }

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

    public void setSsrcSender(long ssrcSender) {
        this.ssrcSender = ssrcSender;
    }

    public void addReportBlock(ReportBlock reportBlock) {
        reportBlocks.add(reportBlock);
        counter = reportBlocks.size();
        length += 6;
    }

    @Override
    public byte[] toRaw() {
        ByteBuffer buffer = ByteBuffer.allocate(8 + (ReportBlock.SIZE * reportBlocks.size()));
        byte vpc = (byte) ((((version & 0x3) << 6) | (((padding ? 1 : 0) & 0x1) << 5) | (counter & 0x1f)) & 0xff);
        buffer.put(vpc);
        buffer.put((byte) (pt & 0xff));
        buffer.put(Bits.intToByteArray(length), 2, 2);
        buffer.put(Bits.longToByteArray(ssrcSender), 4, 4);

        //report block
        for (ReportBlock reportBlock : reportBlocks) {
            buffer.put(reportBlock.toRaw());
        }

        return buffer.array();
    }

        public static ReceiverReport of(ByteBuffer buffer) {
            ReceiverReport receiverReport = new ReceiverReport();
            byte vpc = buffer.get();
            receiverReport.version = vpc >> 6 & 0x3;
            receiverReport.padding = (vpc >> 5 & 0x1) == 1;
            receiverReport.counter = vpc & 0x1f;
            receiverReport.pt = buffer.get() & 0xff;
            receiverReport.length = buffer.getShort();

            // TODO: 2019/1/6 直接读取暂不处理
            buffer.get(new byte[receiverReport.length * 32]);

            return receiverReport;
        }
}