package io.chengguo.streaming.rtcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

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
