package io.chengguo.streaming.rtsp.header;

/**
 * Created by fingerart on 2018-07-15.
 */

public class SessionHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Session";
    //Session: E7E25DEC;timeout=65

    public SessionHeader(String value) {
        super(DEFAULT_NAME, value);
    }


}