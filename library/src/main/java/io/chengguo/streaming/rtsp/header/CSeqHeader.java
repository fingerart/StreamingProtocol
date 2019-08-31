package io.chengguo.streaming.rtsp.header;

import androidx.annotation.NonNull;

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

    public CSeqHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }
}
