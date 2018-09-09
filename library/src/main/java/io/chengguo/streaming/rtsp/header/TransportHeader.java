package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

import static io.chengguo.streaming.rtsp.header.TransportHeader.Specifier.UDP;

/**
 * Transport
 * Created by fingerart on 2018-07-15.
 */
public class TransportHeader extends StringHeader {
    public static final String DEFAULT_NAME = "Transport";

    private Specifier specifier = UDP;

    private BroadcastType broadcastType = BroadcastType.unicast;

    private String destination;

    private String source;

    private Mode mode = Mode.PLAY;

    private PairPort clientPort;

    private PairPort serverPort;

    public TransportHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
        deformat(getRawValue());
    }

    private void deformat(String rawValue) {

    }

    public TransportHeader(Builder builder) {
        setName(DEFAULT_NAME);
        specifier = builder.specifier;
        broadcastType = builder.broadcastType;
        destination = builder.destination;
        source = builder.source;
        mode = builder.mode;
        clientPort = builder.clientPort;
        serverPort = builder.serverPort;
        rebuildRawValue();
    }

    private void rebuildRawValue() {
        StringBuilder sb = new StringBuilder();
                sb.append(specifier.description).append(";")
                        .append(broadcastType.name()).append(";")
                        .append("destination=").append(destination).append(";")
                        .append("source=").append(source).append(";")
                        .append("client_port=").append(clientPort.toString()).append(";")
                        .append("server_port=").append(serverPort.toString()).append(";")
                        .append("mode=").append(mode.name()).append(";");
        setRawValue(sb.toString());
    }

    public Specifier getSpecifier() {
        return specifier;
    }

    public void setSpecifier(Specifier specifier) {
        this.specifier = specifier;
    }

    public BroadcastType getBroadcastType() {
        return broadcastType;
    }

    public void setBroadcastType(BroadcastType broadcastType) {
        this.broadcastType = broadcastType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public PairPort getClientPort() {
        return clientPort;
    }

    public void setClientPort(PairPort clientPort) {
        this.clientPort = clientPort;
    }

    public PairPort getServerPort() {
        return serverPort;
    }

    public void setServerPort(PairPort serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * 传输描述符
     * transport/profile/lower-transport
     */
    public enum Specifier {
        UDP("RTP/AVP/UDP"), TCP("RTP/AVP/TCP");

        public final String description;

        Specifier(String description) {
            this.description = description;
        }
    }

    public enum Mode {
        PLAY, RECORD
    }

    /**
     * 广播类型
     */
    public enum BroadcastType {
        unicast, multicast
    }

    /**
     * 端口组
     */
    public static class PairPort {
        public int begin;
        public int end;

        public PairPort(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        @Override
        public String toString() {
            return begin + "-" + end;
        }
    }

    public static class Builder {
        private Specifier specifier = UDP;

        private BroadcastType broadcastType = BroadcastType.unicast;

        private String destination;

        private String source;

        private Mode mode = Mode.PLAY;

        private PairPort clientPort;

        private PairPort serverPort;

        public Builder specifier(Specifier specifier) {
            this.specifier = specifier;
            return this;
        }

        public Builder broadcastType(BroadcastType broadcastType) {
            this.broadcastType = broadcastType;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder clientPort(int begin, int end) {
            this.clientPort = new PairPort(begin, end);
            return this;
        }

        public Builder serverPort(int begin, int end) {
            this.serverPort = new PairPort(begin, end);
            return this;
        }

        public TransportHeader build() {
            return new TransportHeader(this);
        }
    }
}