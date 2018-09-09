package io.chengguo.streaming.transport;

import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;

/**
 * Created by fingerart on 2018-09-08.
 */
public class UDPTransport implements ITransport {
    @Override
    public void setTransportListener(ITransportListener listener) {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(byte[] data) {

    }

    @Override
    public void setResolver(IResolver resolver) {

    }

    @Override
    public IResolver getResolver() {
        return null;
    }
}
