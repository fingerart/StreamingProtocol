package io.chengguo.streaming.rtsp;

/**
 * Created by fingerart on 2018-07-17.
 */
public class Version {
    public static final String PROTOCOL = "RTSP";
    private String version = "1.0";

    public Version() {
    }

    public Version(String version) {
        this.version = version;
    }

    public String getProtocol() {
        return PROTOCOL;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return PROTOCOL + "/" + version;
    }
}
