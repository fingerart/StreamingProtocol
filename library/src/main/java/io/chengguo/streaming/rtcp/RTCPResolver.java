package io.chengguo.streaming.rtcp;

import java.io.InputStream;

import io.chengguo.streaming.rtsp.IResolver;

/**
 * Created by fingerart on 2018-09-18.
 */
public class RTCPResolver implements IResolver<RtcpPacket> {
    @Override
    public void regist(InputStream inputStream) {

    }

    @Override
    public void setResolverCallback(IResolverCallback<RtcpPacket> resolverCallback) {

    }

    @Override
    public void release() {

    }
}
