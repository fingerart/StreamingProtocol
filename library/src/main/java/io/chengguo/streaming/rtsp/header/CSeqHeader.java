package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

/**
 * CSeqHeader
 * <p>
 * Created by fingerart on 2018-07-15.
 */

public class CSeqHeader extends IntegerHeader {

    public static final String DEFAULT_NAME = "CSeq";

    public CSeqHeader(Integer value) {
        super(DEFAULT_NAME, value);
    }
}
