package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

/**
 * Created by fingerart on 2018-09-12.
 */
public class ContentLengthHeader extends IntegerHeader {
    public static final String DEFAULT_NAME = "Content-Length";

    public ContentLengthHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }

    public ContentLengthHeader(Integer value) {
        super(DEFAULT_NAME, value);
    }
}