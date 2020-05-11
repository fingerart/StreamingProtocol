package io.chengguo.streaming.rtsp;

import androidx.annotation.IntDef;

public interface ISessionStateObserver {
    int SESSION_STATE_CONNECTED = 0;
    int SESSION_STATE_DISCONNECTED = 1;

    @IntDef({SESSION_STATE_CONNECTED, SESSION_STATE_DISCONNECTED})
    @interface SessionState {
    }

    void onConnectChanged(@SessionState int state);

    void onError(Exception exception);
}