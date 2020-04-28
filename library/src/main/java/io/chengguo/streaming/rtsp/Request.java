package io.chengguo.streaming.rtsp;

import java.net.URI;
import java.util.HashMap;

import io.chengguo.streaming.rtsp.header.Header;
import io.chengguo.streaming.rtsp.header.IntegerHeader;
import io.chengguo.streaming.rtsp.header.StringHeader;
import io.chengguo.streaming.transport.IMessage;

/**
 * Created by fingerart on 2018-07-17.
 */
public class Request implements IMessage {

    private Line line;
    private HashMap<String, Header> headers = new HashMap<>();

    public Request() {
    }

    public Request(Builder builder) {
        line = new Line(builder.method, builder.uri, builder.version);
        headers.putAll(builder.headers);
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public HashMap<String, Header> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Header> headers) {
        this.headers = headers;
    }

    public void addHeader(Header header) {
        this.headers.put(header.getName(), header);
    }

    public <T extends Header> T getHeader(String key) {
        Header header = headers.get(key);
        return header == null ? null : (T) header;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(line.toString());
        buffer.append("\r\n");
        for (Header header : headers.values()) {
            buffer.append(header).append("\r\n");
        }
        buffer.append("\r\n").append("\r\n");
        return buffer.toString();
    }

    @Override
    public byte[] toRaw() {
        return toString().getBytes();
    }

    /**
     * 请求行
     */
    public static class Line {
        private Method method;
        private URI uri;
        private Version version;

        public Line(Method method, URI uri) {
            this.method = method;
            this.uri = uri;
        }

        public Line(Method method, URI uri, Version version) {
            this.method = method;
            this.uri = uri;
            this.version = version;
        }

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

    public static class Builder {

        private Method method;
        private URI uri;
        private HashMap<String, Header> headers = new HashMap<>();
        private Version version = new Version();

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = URI.create(uri);
            return this;
        }

        public Builder version(String version) {
            this.version.setVersion(version);
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, new StringHeader(key, value));
            return this;
        }

        public Builder addHeader(String key, Integer value) {
            headers.put(key, new IntegerHeader(key, value));
            return this;
        }

        public Builder addHeader(Header header) {
            headers.put(header.getName(), header);
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}