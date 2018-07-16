package io.chengguo.streaming.rtsp;

import java.net.URI;

import io.chengguo.streaming.rtsp.header.Header;

/**
 * Created by fingerart on 2018-07-17.
 */
public class Request {

    private Header header;

    /**
     * 请求行
     */
    public class Line {
        private Method method;
        private URI uri;
        private Version version;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public URI getUri() {
            return uri;
        }

        public void setUri(URI uri) {
            this.uri = uri;
        }

        public Version getVersion() {
            return version;
        }

        public void setVersion(Version version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return method + " " + uri + " " + version;
        }
    }
}