package io.chengguo.streaming.rtcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.utils.L;

/**
 *
 */
public class RTCPResolver implements IResolver<Integer, RTCPResolver.RTCPResolverListener> {

    private RTCPResolverListener rtcpResolverListener;

    public RTCPResolver() {
    }

    public RTCPResolver(RTCPResolverListener rtcpResolverListener) {
        this.rtcpResolverListener = rtcpResolverListener;
    }

    @Override
    public void resolve(DataInputStream in, Integer rtcpLength) throws IOException {
        L.d("RTCP length: " + rtcpLength);
        ByteBuffer buffer = ByteBuffer.allocate(rtcpLength);
        in.readFully(buffer.array());

        resolveSingle(buffer);
    }

    private void resolveSingle(ByteBuffer buffer) {
        byte pt = buffer.get(1);//8bit
        short length = buffer.getShort(2);//16bit
        L.d("RTCPResolver#resolve [pt=" + ((int) pt & 0xff) + ", length=" + length + "]");
        if (rtcpResolverListener != null) {
            switch (pt) {
                case SourceDescription.PACKET_TYPE:
                    rtcpResolverListener.onSourceDescription(SourceDescription.Resolver.resolve(buffer));
                    break;
                case SenderReport.PACKET_TYPE:
                    rtcpResolverListener.onSenderReport(SenderReport.of(buffer));
                    break;
                case ReceiverReport.PACKET_TYPE:
                    rtcpResolverListener.onReceiverReport(ReceiverReport.of(buffer));
                    break;
            }
        }
        //如果还未读取完，则继续读
        if (buffer.hasRemaining()) {
            buffer.compact();
            buffer.flip();
            resolveSingle(buffer);
        }
    }

    @Override
    public void setResolverListener(RTCPResolverListener rtcpResolverListener) {
        this.rtcpResolverListener = rtcpResolverListener;
    }

    @Override
    public void release() {
        rtcpResolverListener = null;
    }

    public interface RTCPResolverListener {
        void onSenderReport(SenderReport senderReport);

        void onReceiverReport(ReceiverReport receiverReport);

        void onSourceDescription(SourceDescription sourceDescription);
    }
}
