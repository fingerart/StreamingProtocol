package io.chengguo.streaming.rtsp;

import java.io.InputStream;

/**
 * 解析器
 * Created by fingerart on 2018-09-09.
 */
public interface IResolver<T> {

    /**
     * 告诉解析器目标InputStream
     *
     * @param inputStream
     */
    void target(InputStream inputStream);

    /**
     * 设置回调
     *
     * @param resolverCallback
     */
    void setResolverCallback(IResolverCallback<T> resolverCallback);

    /**
     * 释放资源
     */
    void release();

    interface IResolverCallback<T> {
        void onResolve(T t);
    }
}
