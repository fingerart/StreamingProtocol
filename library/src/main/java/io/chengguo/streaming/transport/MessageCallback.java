package io.chengguo.streaming.transport;

public interface MessageCallback {
    void onSuccess();

    void onFailure(Throwable throwable);
}