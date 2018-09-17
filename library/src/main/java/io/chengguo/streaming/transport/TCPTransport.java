package io.chengguo.streaming.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;
import io.chengguo.streaming.rtsp.SafeTransportListener;

/**
 * Created by fingerart on 2018-09-08.
 */
public class TCPTransport implements ITransport {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private InetSocketAddress address;
    private int timeout;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SafeTransportListener transportListener;
    private IResolver mResolver;
    private IMessage mMessage;

    public TCPTransport(String hostname, int port, int timeout) {
        this.timeout = timeout;
        address = new InetSocketAddress(hostname, port);
        socket = new Socket();
        transportListener = new SafeTransportListener();
    }

    @Override
    public void setTransportListener(ITransportListener listener) {
        transportListener.setBehaviour(listener);
    }

    @Override
    public void connect() {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect(address, timeout);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    if (mResolver != null) {
                        mResolver.target(inputStream);
                    }
                    transportListener.onConnected();
                } catch (IOException e) {
                    e.printStackTrace();
                    transportListener.onConnectFail(e);
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
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream.write(message.toRaw());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
            transportListener.onDisconnected();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setResolver(IResolver resolver) {
        //如果存在则释放之前的解析器
        if (mResolver != null) {
            mResolver.release();
        }
        mResolver = resolver;
        if (mResolver != null && isConnected()) {
            mResolver.target(inputStream);
        }
    }

    @Override
    public IResolver getResolver() {
        return mResolver;
    }
}