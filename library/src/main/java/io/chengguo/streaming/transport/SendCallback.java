package io.chengguo.streaming.transport;

public interface SendCallback {
    void onSuccess();

    void onFailure(Throwable throwable);
}