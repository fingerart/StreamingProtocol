package io.chengguo.streaming.rtcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import io.chengguo.streaming.rtsp.IResolver;

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
        System.out.println("read rtcp length: " + rtcpLength);
        ByteBuffer buffer = ByteBuffer.allocate(rtcpLength);
        inputStream.readFully(buffer.array());
        byte v = buffer.get();//8bit
        byte pt = buffer.get();//8bit
        short length = buffer.getShort();//16bit
        System.out.println("parse length: " + length);
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
