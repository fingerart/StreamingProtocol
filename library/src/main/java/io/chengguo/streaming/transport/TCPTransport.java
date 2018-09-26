package io.chengguo.streaming.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.chengguo.streaming.rtcp.IReport;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IMessage;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.ITransportListener;
import io.chengguo.streaming.rtsp.Response;
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
    private IResolver<Integer, Response> mRtspResolver;
    private IResolver<Integer, RtpPacket> mRtpResolver;
    private IResolver<Integer, IReport> mRtcpResolver;

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
    public void connect() throws IOException {
        socket.connect(address, timeout);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        transportListener.onConnected();
        DataInputStream in = new DataInputStream(inputStream);
        registerResolver();
        int firstByte;
        while ((firstByte = in.readUnsignedByte()) != -1) {
            System.out.println("First Byte: " + firstByte);
            //'$' beginning is the RTP and RTSP
            if (firstByte == 36) {
                int secondByte = in.readUnsignedByte();
                //channel is rtp
                if (secondByte == 0) {
                    if (mRtpResolver != null) {
                        int rtpLength = in.readUnsignedShort();
                        mRtpResolver.resolve(rtpLength);
                    }
                } else if (secondByte == 1) { //channel is rtcp
                    if (mRtcpResolver != null) {
                        int rtcpLength = in.readUnsignedShort();
                        mRtcpResolver.resolve(rtcpLength);
                    }
                }
            } else {//RTSP
                if (mRtspResolver != null) {
                    mRtspResolver.resolve(firstByte);
                }
            }
        }
    }

    @Override
    public void connectAsync() {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    transportListener.onConnectFail(e);
                }
            }
        });
    }

    /**
     * 将InputStream注册到Resolver中
     */
    private void registerResolver() {
        if (mRtspResolver != null) {
            mRtspResolver.regist(inputStream);
        }
        if (mRtpResolver != null) {
            mRtpResolver.regist(inputStream);
        }
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
            if (mRtspResolver != null) {
                mRtspResolver.release();
            }
            if (mRtpResolver != null) {
                mRtpResolver.release();
            }
            if (mRtcpResolver != null) {
                mRtcpResolver.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            transportListener.onDisconnected();
        }
    }

    @Override
    public void setRtspResolver(IResolver<Integer, Response> resolver) {
        //如果存在则释放之前的解析器
        if (mRtspResolver != null) {
            mRtspResolver.release();
        }
        mRtspResolver = resolver;
    }

    @Override
    public void setRtpResolver(IResolver<Integer, RtpPacket> resolver) {
        if (mRtpResolver != null) {
            mRtpResolver.release();
        }
        mRtpResolver = resolver;
    }

    @Override
    public void setRtcpResolver(IResolver<Integer, IReport> rtcpResolver) {
        if (mRtcpResolver != null) {
            mRtcpResolver.release();
        }
        mRtcpResolver = rtcpResolver;
    }

    @Override
    public IResolver<Integer, Response> getRtspResolver() {
        return mRtspResolver;
    }

    @Override
    public IResolver<Integer, RtpPacket> getRtpResolver() {
        return mRtpResolver;
    }

    @Override
    public IResolver<Integer, IReport> getRtcpResolver() {
        return mRtcpResolver;
    }
}