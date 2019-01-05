package io.chengguo.streaming.rtcp;

//@formatter:off
//|        0                   1                   2                   3
//|         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
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
//@formatter:on

import java.nio.ByteBuffer;

import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.utils.Bits;

/**
 * 报告块
 */
public class ReportBlock implements IMessage {
    public static final int SIZE = 24;//byte

    public long identifier;//同步源n的SSRC标识符
    public int fractionLost;//丢包率
    public int numberOfPacketsLost;//累计的包丢失数
    public long exHighestNumber;//扩展最高序列号
    public long interarrivalJitter;//到达间隔抖动
    public long lastSR;//最新接收到SR报文的时间戳
    public long delayLastSR;//接收到SR报文的时刻与发送该RR报文时刻的时间差值

    @Override
    public byte[] toRaw() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE);
        buffer.put(Bits.longToByteArray(identifier, 4));
        buffer.put(Bits.intToByteArray(fractionLost, 1));
        buffer.put(Bits.intToByteArray(numberOfPacketsLost, 3));
        buffer.put(Bits.longToByteArray(exHighestNumber, 4));
        buffer.put(Bits.longToByteArray(interarrivalJitter, 4));
        buffer.put(Bits.longToByteArray(lastSR, 4));
        buffer.put(Bits.longToByteArray(delayLastSR, 4));
        return buffer.array();
    }

    @Override
    public String toString() {
        return "ReportBlock{" +
                "identifier=" + identifier +
                ", fractionLost=" + fractionLost +
                ", numberOfPacketsLost=" + numberOfPacketsLost +
                ", exHighestNumber=" + exHighestNumber +
                ", interarrivalJitter=" + interarrivalJitter +
                ", lastSR=" + lastSR +
                ", delayLastSR=" + delayLastSR +
                '}';
    }
}