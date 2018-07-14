package io.chengguo.streaming.rtsp.header;

/**
 * UserAgent
 * Created by fingerart on 2018-07-15.
 */
public class UserAgentHeader extends StringHeader {
    public static final String DEFAULT_NAME = "User-Agent";

    public UserAgentHeader(String value) {
        super(DEFAULT_NAME, value);
    }
}
