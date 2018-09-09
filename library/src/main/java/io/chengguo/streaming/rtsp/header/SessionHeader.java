package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

/**
 * Created by fingerart on 2018-07-15.
 */
public class SessionHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Session";
    //Session: E7E25DEC;timeout=65

    public SessionHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }

}