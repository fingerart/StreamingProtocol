package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

import io.chengguo.streaming.utils.Utils;

/**
 * 值为字符串的Header
 * Created by fingerart on 2018-07-15.
 */
public class StringHeader extends Header<String> {
    public StringHeader(String name, String value) {
        super(name, value);
    }

    public StringHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }

    @Override
    protected String parseValue(String value) {
        return Utils.trimSafely(value);
    }
}
