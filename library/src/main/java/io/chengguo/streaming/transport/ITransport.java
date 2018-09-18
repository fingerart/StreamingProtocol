package io.chengguo.streaming.transport;

import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;

/**
 * Created by fingerart on 2018-09-08.
 */
public interface ITransport {

    void setTransportListener(ITransportListener listener);

    void connect();

    boolean isConnected();

    void disconnect();

    void send(IMessage message);

    void setRtspResolver(IResolver resolver);

    IResolver getResolver();
}
