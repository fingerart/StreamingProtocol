package io.chengguo.streaming.rtsp.header;

/**
 * Created by fingerart on 2018-07-15.
 */
public class AcceptHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Accept";

    public AcceptHeader(String value) {
        super(DEFAULT_NAME, value);
    }

    public AcceptHeader(ContentTypeHeader.Type value) {
        this(value.description);
    }
}