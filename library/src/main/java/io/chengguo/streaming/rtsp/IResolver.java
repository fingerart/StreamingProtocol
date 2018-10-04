package io.chengguo.streaming.rtsp;

import java.io.IOException;
import java.io.InputStream;

/**
 * 解析器
 * Created by fingerart on 2018-09-09.
 */
public interface IResolver<T, Result> {

    /**
     * 告诉解析器目标InputStream
     *
     * @param inputStream
     */
    void regist(InputStream inputStream);

    /**
     * 解析
     */
    void resolve(T t) throws IOException;

    /**
     * 设置回调
     *
     * @param resolverCallback
     */
    void setResolverCallback(IResolverCallback<Result> resolverCallback);

    /**
     * 释放资源
     */
    void release();

    interface IResolverCallback<Result> {
        void onResolve(Result result);
    }
}
