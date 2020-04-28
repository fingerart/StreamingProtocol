package io.chengguo.streaming.transport;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by fingerart on 2018-09-08.
 */
public class UDPTransport extends TransportImpl {
    @Override
    public DataInputStream connectSync() throws IOException {
        return null;
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
    public void send(IMessage message) {

    }

    @Override
    public void send(IMessage message, SendCallback callback) {

    }
}
