package io.chengguo.streaming.rtcp;

import java.nio.ByteBuffer;

/**
 * SDES 源点描述
 * Created by fingerart on 2018-10-04.
 */
public class SourceDescription implements IReport {
    public static final byte PACKET_TYPE = (byte) 202;



    public static class Resolver {

        public static IReport resolve(ByteBuffer buffer) {
            SourceDescription sourceDescription = new SourceDescription();
            byte vpc = buffer.get();

            return sourceDescription;
        }
    }
}
