package io.chengguo.streaming.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fingerart on 2018-09-08.
 */
public class TCPTransport extends TransportImpl {
    private int timeout;
    private Socket socket;
    private InetSocketAddress address;
    private InputStream inputStream;
    private OutputStream outputStream;

    public TCPTransport(String hostname, int port, int timeout) {
        this.timeout = timeout;
        address = new InetSocketAddress(hostname, port);
    }

    @Override
    public DataInputStream connectSync() throws IOException {
        synchronized (this) {
            if (isConnected()) {
                throw new IllegalStateException("TCP is connected");
            }
            socket = new Socket();
            socket.connect(address, timeout);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
        return new DataInputStream(inputStream);
    }

    @Override
    public void connect() {
        connect(null);
    }

    @Override
    public void connect(final ConnectCallback connectCallback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DataInputStream in = connectSync();
                    try {
                        if (connectCallback != null) {
                            connectCallback.onSuccess();
                        }
                        // Do work
                        dispatchOnConnected(in);
                    } catch (IOException e) {
                        dispatchOnDisconnected();
                    }
                } catch (Exception e) {
                    if (connectCallback != null) {
                        connectCallback.onFailure(e);
                    }
                    dispatchOnConnectFailure(e);
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public void send(final IMessage message) {
        send(message, null);
    }

    @Override
    public void send(final IMessage message, final SendCallback callback) {
        if (message == null) {
            if (callback != null) {
                callback.onFailure(new IllegalArgumentException("Message is null"));
            }
            return;
        }
        final byte[] raw = message.toRaw();
        if (raw.length == 0) {
            if (callback != null) {
                callback.onFailure(new IllegalArgumentException("Message is empty"));
            }
            return;
        }
        if (!isConnected()) {
            if (callback != null) {
                callback.onFailure(new IllegalStateException("TCP is disconnected"));
            }
            return;
        }
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (TCPTransport.this) {
                        outputStream.write(raw);
                    }
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }
        });
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
            socket = null;
        } catch (IOException ignore) {
        }
    }
}