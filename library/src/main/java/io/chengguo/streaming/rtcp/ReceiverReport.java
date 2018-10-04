package io.chengguo.streaming.rtcp;

import java.nio.ByteBuffer;

/**
 * 接受者报告
 * Created by fingerart on 2018/6/20.
 */
public class ReceiverReport implements IReport {
    public static final byte PACKET_TYPE = (byte) 201;

    public static class Resolver {

        public static IReport resolve(ByteBuffer buffer) {
            ReceiverReport receiverReport = new ReceiverReport();
            return receiverReport;
        }
    }
}