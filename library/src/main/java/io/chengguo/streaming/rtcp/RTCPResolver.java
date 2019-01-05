package io.chengguo.streaming.rtcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.utils.L;

/**
 *
 */
public class RTCPResolver implements IResolver<Integer, RTCPResolver.RTCPResolverListener> {

    private DataInputStream inputStream;
    private RTCPResolverListener rtcpResolverListener;

    @Override
    public void regist(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    @Override
    public void resolve(Integer rtcpLength) throws IOException {
        L.d("RTCP length: " + rtcpLength);
        ByteBuffer buffer = ByteBuffer.allocate(rtcpLength);
        inputStream.readFully(buffer.array());

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
                    rtcpResolverListener.onSenderReport(SenderReport.Resolver.resolve(buffer));
                    break;
                case ReceiverReport.PACKET_TYPE:
                    rtcpResolverListener.onReceiverReport(ReceiverReport.Resolver.resolve(buffer));
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
        inputStream = null;
        rtcpResolverListener = null;
    }

    public interface RTCPResolverListener {
        void onSenderReport(SenderReport senderReport);

        void onReceiverReport(ReceiverReport receiverReport);

        void onSourceDescription(SourceDescription sourceDescription);
    }
}
