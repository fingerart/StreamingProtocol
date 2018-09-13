package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

import io.chengguo.streaming.utils.Utils;

/**
 * Created by fingerart on 2018-07-15.
 */
public class SessionHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Session";
    //Session: E7E25DEC;timeout=65

    private String session;
    private int timeout;

    public SessionHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
        deformat(getRawValue());
    }

    public SessionHeader(String session, int timeout) {
        setName(DEFAULT_NAME);
        this.session = session;
        this.timeout = timeout;
        setRawValue(buildSession());
    }

    private String buildSession() {
        StringBuilder sb = new StringBuilder();
        if (!Utils.isEmpty(session)) {
            sb.append(session).append(";");
        }
        if (timeout != 0) {
            sb.append("timeout=").append(timeout);
        }
        return sb.toString();
    }

    private void deformat(String rawValue) {
        int index = rawValue.indexOf(";");
        if (index != -1) {
            session = rawValue.substring(0, index);
            String[] to = rawValue.substring(index + 1).split("=");
            timeout = Integer.valueOf(to[1]);
        } else {
            session = rawValue;
        }
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}