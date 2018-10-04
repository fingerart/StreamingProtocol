package io.chengguo.streaming.rtcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.utils.L;

/**
 * Created by fingerart on 2018-09-18.
 */
public class RTCPResolver implements IResolver<Integer, IReport> {

    private DataInputStream inputStream;
    private IResolverCallback<IReport> resolverCallback;

    @Override
    public void regist(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    @Override
    public void resolve(Integer rtcpLength) throws IOException {
        L.d("RTCP length: " + rtcpLength);
        ByteBuffer buffer = ByteBuffer.allocate(rtcpLength);
        inputStream.readFully(buffer.array());
        byte pt = buffer.get(1);//8bit
        short length = buffer.getShort(2);//16bit
        L.d("RTCPResolver#resolve [pt=" + ((int) pt & 0xff) + ", length=" + length + "]");
        if (resolverCallback != null) {
            IReport report = null;
            switch (pt) {
                case SourceDescription.PACKET_TYPE:
                    report = SourceDescription.Resolver.resolve(buffer);
                    break;
                case SenderReport.PACKET_TYPE:
                    report = SenderReport.Resolver.resolve(buffer);
                    break;
                case ReceiverReport.PACKET_TYPE:
                    report = ReceiverReport.Resolver.resolve(buffer);
                    break;
            }
            resolverCallback.onResolve(report);
        }
    }

    @Override
    public void setResolverCallback(IResolverCallback<IReport> resolverCallback) {
        this.resolverCallback = resolverCallback;
    }

    @Override
    public void release() {
        inputStream = null;
        resolverCallback = null;
    }
}
