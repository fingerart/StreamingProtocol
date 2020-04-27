package io.chengguo.streaming.transport;

import java.io.DataInputStream;
import java.io.IOException;

import androidx.annotation.IntDef;

public interface ITransportListener {
    int STATE_CONNECT_FAILURE = -1;
    int STATE_DISCONNECTED = 1;

    @IntDef({STATE_CONNECT_FAILURE, STATE_DISCONNECTED})
    @interface State {
    }

    void onConnected(DataInputStream in) throws IOException;

    void onConnectChanged(@State int state, Throwable throwable);
}
