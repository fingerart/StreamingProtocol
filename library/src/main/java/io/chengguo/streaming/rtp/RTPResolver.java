package io.chengguo.streaming.rtp;

import java.io.IOException;
import java.io.InputStream;

import io.chengguo.streaming.rtsp.IResolver;

/**
 * RTP解析器
 * Created by fingerart on 2018-09-13.
 */
public class RTPResolver implements IResolver<Integer, RtpPacket> {
    @Override
    public void regist(InputStream inputStream) {

    }

    @Override
    public void resolve(Integer integer) throws IOException {

    }

    @Override
    public void setResolverCallback(IResolverCallback<RtpPacket> resolverCallback) {

    }

    @Override
    public void release() {

    }
}
