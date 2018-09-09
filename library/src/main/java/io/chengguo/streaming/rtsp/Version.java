package io.chengguo.streaming.rtsp;

/**
 * Created by fingerart on 2018-07-17.
 */
public class Version {
    private String protocol = "RTSP";
    private String version = "1.0";

    public Version(String version) {
        this.version = version;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return protocol + "/" + version;
    }
}
