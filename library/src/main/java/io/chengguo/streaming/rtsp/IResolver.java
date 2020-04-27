package io.chengguo.streaming.rtsp;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 解析器
 * Created by fingerart on 2018-09-09.
 */
public interface IResolver<T, Listener> {

    /**
     * 解析
     */
    void resolve(DataInputStream in, T t) throws IOException;

    /**
     * 设置回调
     *
     * @param listener
     */
    void setResolverListener(Listener listener);

    /**
     * 释放资源
     */
    void release();

    interface IResolverCallback<Result> {
        void onResolve(Result result);
    }
}
