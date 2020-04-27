package io.chengguo.streaming.rtsp;

public interface Interceptor {
    Request onSend(Request request);

    Response onResponse(Response response);
}
