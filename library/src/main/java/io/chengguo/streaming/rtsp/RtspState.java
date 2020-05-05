package io.chengguo.streaming.rtsp;

import androidx.annotation.IntDef;

@IntDef({RtspState.UNSTART, RtspState.OPTIONS, RtspState.DESCRIBE, RtspState.SETUP, RtspState.PLAY, RtspState.TEARDOWN, RtspState.PAUSE})
@interface RtspState {
    int UNSTART = -1;
    int OPTIONS = 0;
    int DESCRIBE = 1;
    int SETUP = 2;
    int PLAY = 3;
    int TEARDOWN = 4;
    int PAUSE = 5;
}