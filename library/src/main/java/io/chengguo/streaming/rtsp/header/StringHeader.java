package io.chengguo.streaming.rtsp.header;

import androidx.annotation.NonNull;

import io.chengguo.streaming.utils.Utils;

/**
 * 值为字符串的Header
 * Created by fingerart on 2018-07-15.
 */
public class StringHeader extends Header<String> {
    public StringHeader() {
    }

    public StringHeader(String name, String value) {
        super(name, value);
    }

    public StringHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }

    /**
     * 尝试拆分Key: Value
     *
     * @param rawHeader
     * @return
     */
    protected String trySplitKV(String rawHeader) {
        String[] splitValue = Utils.splitSafely(rawHeader, ":");
        if (splitValue.length == 2) {
            return Utils.trimSafely(splitValue[1]);
        }
        return rawHeader;
    }

    @Override
    protected String parseValue(String value) {
        return value;
    }
}
