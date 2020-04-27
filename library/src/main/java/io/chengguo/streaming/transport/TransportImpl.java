package io.chengguo.streaming.transport;

import java.io.DataInputStream;
import java.io.IOException;

import io.chengguo.streaming.rtsp.IMessage;

/**
 * Created by fingerart on 2018-09-08.
 */
public abstract class TransportImpl {
    protected ITransportListener mTransportListener;

    public abstract DataInputStream connectSync() throws IOException;

    public abstract void connect();

    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void send(IMessage message);

    public abstract void send(IMessage message, MessageCallback callback);

    public void setTransportListener(ITransportListener transportListener) {
        mTransportListener = transportListener;
    }

    /**
     * Dispatch connected.
     */
    protected void dispatchOnConnected(DataInputStream in) throws IOException {
        if (mTransportListener != null) {
            mTransportListener.onConnected(in);
        }
    }

    /**
     * Dispatch disconnected.
     */
    protected void dispatchOnDisconnected() {
        if (mTransportListener != null) {
            mTransportListener.onConnectChanged(ITransportListener.STATE_DISCONNECTED, null);
        }
    }

    /**
     * Dispatch connect failure.
     */
    protected void dispatchOnConnectFailure(Throwable throwable) {
        if (mTransportListener != null) {
            mTransportListener.onConnectChanged(ITransportListener.STATE_CONNECT_FAILURE, throwable);
        }
    }
}
