package io.chengguo.streaming.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.utils.L;

/**
 * Created by fingerart on 2018-09-08.
 */
public class TCPTransport extends TransportImpl {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private int timeout;
    private Socket socket;
    private InetSocketAddress address;
    private InputStream inputStream;
    private OutputStream outputStream;

    public TCPTransport(String hostname, int port, int timeout) {
        this.timeout = timeout;
        address = new InetSocketAddress(hostname, port);
        socket = new Socket();
    }

    @Override
    public DataInputStream connectSync() throws IOException {
        if (!isConnected()) {
            throw new IllegalStateException("TCP is connected.");
        }
        socket.connect(address, timeout);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        return new DataInputStream(inputStream);
    }

    @Override
    public void connect() {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DataInputStream in = connectSync();
                    try {
                        dispatchOnConnected(in);
                    } catch (IOException e) {
                        dispatchOnDisconnected();
                    }
                } catch (Exception e) {
                    dispatchOnConnectFailure(e);
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void send(final IMessage message) {
        send(message, null);
    }

    @Override
    public void send(final IMessage message, final MessageCallback callback) {
        if (!isOutputShutdown()) {
            if (callback != null) {
                callback.onFailure(new IllegalStateException("Output is Shutdown"));
            }
            return;
        }
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isOutputShutdown()) {
                        outputStream.write(message.toRaw());
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        throw new IllegalStateException("Output is Shutdown");
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }
        });
    }

    private boolean isOutputShutdown() {
        return socket == null || socket.isOutputShutdown();
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ignore) {
        } finally {
            dispatchOnDisconnected();
        }
    }
}