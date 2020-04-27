package io.chengguo.streaming.rtsp;

import androidx.annotation.Nullable;

public interface IInterceptor {
    Request onSend(Request newRequest, @Nullable Response response);

    Response onResponse(Response response);
}
