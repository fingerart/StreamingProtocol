package io.chengguo.streaming.rtsp.header;

import android.util.SparseArray;

/**
 * Transport
 * Created by fingerart on 2018-07-15.
 */
public class TransportHeader extends StringHeader {
    public static final String DEFAULT_NAME = "Transport";

    //Transport: RTP/AVP/UDP;unicast;client_port=6988-6989
    //Transport: RTP/AVP;unicast;destination=113.104.167.226;source=172.17.0.2;client_port=6988-6989;server_port=6970-6971
    //Transport: RTP/AVP/TCP;unicast;interleaved=0-1
    //Transport: RTP/AVP/TCP;unicast;destination=113.104.167.226;source=172.17.0.2;interleaved=0-1
    public TransportHeader(String value) {
        super(DEFAULT_NAME, value);
    }

    public enum Type {
        UDP("RTP/AVP/UDP"), TCP("RTP/AVP/TCP"), DEFAULT("RTP/AVP");

        public final String description;

        Type(String description) {
            this.description = description;
        }
    }
}