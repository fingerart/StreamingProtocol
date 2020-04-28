package io.chengguo.streaming.rtsp;

public interface ISessionStateObserver {
    void onConnected();

    void onConnectFailure(Throwable throwable);

    void onDisconnected();
}