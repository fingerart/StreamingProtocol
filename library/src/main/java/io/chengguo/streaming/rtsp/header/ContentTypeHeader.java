package io.chengguo.streaming.rtsp.header;

import androidx.annotation.NonNull;

/**
 * Created by fingerart on 2018-07-15.
 */
public class ContentTypeHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Content-Type";

    public ContentTypeHeader(String name, String value) {
        super(name, value);
    }

    public ContentTypeHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }

    public ContentTypeHeader(Type type) {
        this(DEFAULT_NAME, type.description);
    }

    /**
     * 是否支持指定的类型
     *
     * @param type
     * @return
     */
    public boolean isSupportType(Type type) {
        return getRawValue().contains(type.description);
    }

    /**
     * 支持的类型
     */
    public enum Type {
        SDP("application/sdp");

        public final String description;

        Type(String description) {
            this.description = description;
        }
    }
}
