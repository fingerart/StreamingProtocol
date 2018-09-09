package io.chengguo.streaming.rtsp;

public interface ITransportListener {
    void onConnected();

    void onConnectFail(Exception exception);

    void onDisconnected();
}