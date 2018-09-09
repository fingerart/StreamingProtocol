package io.chengguo.streaming.transport;

/**
 * Created by fingerart on 2018-09-08.
 */
public enum TransportMethod {
    TCP {
        @Override
        public ITransport createTransport(String hostname, int port, int timeout) {
            return new TCPTransport(hostname, port, timeout);
        }
    },

    UDP {
        @Override
        public ITransport createTransport(String hostname, int port, int timeout) {
            return new UDPTransport();
        }
    };

    public abstract ITransport createTransport(String hostname, int port, int timeout);
}
