package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

/**
 * Created by fingerart on 2018-07-15.
 */

public class TransportHeader extends StringHeader {
    public static final String DEFAULT_NAME = "Transport";

    public TransportHeader(String value) {
        super(DEFAULT_NAME, value);
    }

    @Override
    public void setRawValue(String value) {
        super.setRawValue(value);

    }
}
