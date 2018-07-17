package io.chengguo.streaming.rtsp;

import java.util.ArrayList;
import java.util.List;

import io.chengguo.streaming.rtsp.header.Header;

/**
 *
 * Created by fingerart on 2018-07-17.
 */
public class Response {
    private Line line;
    private List<Header> headers = new ArrayList<>();

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public List<Header> getHeader() {
        return headers;
    }

    public void setHeader(List<Header> header) {
        this.headers = header;
    }

    public void addHeader(Header header) {
        headers.add(header);
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

    public static class Line {
        private Version version;
        private int statusCode;
        private String statusMessage;

        public Version getVersion() {
            return version;
        }

        public void setVersion(Version version) {
            this.version = version;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        @Override
        public String toString() {
            return version + " " + statusCode + " " + statusMessage;
        }
    }

    public static class Body {

    }
}