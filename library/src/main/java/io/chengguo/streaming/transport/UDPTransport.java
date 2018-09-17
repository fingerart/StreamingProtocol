package io.chengguo.streaming.transport;

import io.chengguo.streaming.rtsp.IMessage;
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
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(IMessage data) {

    }

    @Override
    public void setResolver(IResolver resolver) {

    }

    @Override
    public IResolver getResolver() {
        return null;
    }
}
