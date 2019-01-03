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

/**
 * 报告块
 */
public class ReportBlock {
    public static final int SIZE = 24;//byte

    private long identifier;//同步源n的SSRC标识符
    private int fractionLost;//丢包率
    private int numberOfPacketsLost;//累计的包丢失数
    private long exHighestNumber;//扩展最高序列号
    private long interarrivalJitter;//到达间隔抖动
    private long lastSR;//最新接收到SR报文的时间戳
    private long delayLastSR;//接收到SR报文的时刻与发送该RR报文时刻的时间差值

}