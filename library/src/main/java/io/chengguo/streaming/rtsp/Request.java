package io.chengguo.streaming.rtsp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.chengguo.streaming.rtsp.header.Header;

/**
 * Created by fingerart on 2018-07-17.
 */
public class Request {
    private Line line;
    private List<Header> headers = new ArrayList<>();

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public void addHeader(Header header) {
        this.headers.add(header);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(line.toString());
        buffer.append("\r\n");
        for (Header header : headers) {
            buffer.append(header).append("\r\n");
        }
        buffer.append("\r\n");
        return buffer.toString();
    }

    /**
     * 请求行
     */
    public static class Line {
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