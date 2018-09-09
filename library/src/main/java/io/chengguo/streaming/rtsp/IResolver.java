package io.chengguo.streaming.rtsp;

import java.io.InputStream;

/**
 * Created by fingerart on 2018-09-09.
 */
public interface IResolver<T> {

    void target(InputStream inputStream);

    void setResolverCallback(IResolverCallback<T> resolverCallback);

    interface IResolverCallback<T> {
        void onResolve(T t);
    }
}
