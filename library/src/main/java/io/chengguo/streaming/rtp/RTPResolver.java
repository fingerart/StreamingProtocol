package io.chengguo.streaming.rtp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import io.chengguo.streaming.rtsp.IResolver;

/**
 * RTP解析器
 * Created by fingerart on 2018-09-13.
 */
public class RTPResolver implements IResolver<Integer, IResolver.IResolverCallback<RtpPacket>> {

    private DataInputStream inputStream;
    private IResolverCallback<RtpPacket> resolverCallback;

    @Override
    public void regist(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    @Override
    public void resolve(Integer rtpLength) throws IOException {
        System.out.println("RTP length: " + rtpLength);
        ByteBuffer buffer = ByteBuffer.allocate(rtpLength);
        inputStream.readFully(buffer.array());
        if (resolverCallback != null) {
            RtpPacket rtpPacket = RtpPacket.Resolver.resolve(buffer);
            resolverCallback.onResolve(rtpPacket);
        }
    }

    @Override
    public void setResolverListener(IResolverCallback<RtpPacket> resolverCallback) {
        this.resolverCallback = resolverCallback;
    }

    @Override
    public void release() {
        inputStream = null;
        resolverCallback = null;
    }
}
