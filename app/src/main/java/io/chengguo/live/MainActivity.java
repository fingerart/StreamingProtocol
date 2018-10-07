package io.chengguo.live;

import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import io.chengguo.streaming.rtcp.IReport;
import io.chengguo.streaming.rtp.RtpPacket;
import io.chengguo.streaming.rtsp.IResolver;
import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.rtsp.RTSPSession;
import io.chengguo.streaming.rtsp.Request;
import io.chengguo.streaming.rtsp.Response;
import io.chengguo.streaming.rtsp.header.Header;
import io.chengguo.streaming.rtsp.header.TransportHeader;
import io.chengguo.streaming.transport.TransportMethod;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private SurfaceView mSurfaceView;
    private RTSPSession session;
    private String baseUri;
    private AudioTrack audioTrack;
    private Decoder decoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSurfaceView = findViewById(R.id.surface);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        try {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
            decoder = new Decoder();
            decoder.start();
            audioTrack.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        session = new RTSPSession("192.168.1.4", 554, TransportMethod.TCP);
        session.setRTSPResolverCallback(new IResolver.IResolverCallback<Response>() {
            @Override
            public void onResolve(Response response) {
                Log.d(TAG, "onResolveRTSP: " + response.toString());
                if (response.getLine().isSuccessful()) {
                    Request request = response.getRequest();
                    switch (request.getLine().getMethod()) {
                        case OPTIONS:
                            Request des = new Request.Builder()
                                    .method(Method.DESCRIBE)
                                    .uri(request.getLine().getUri())
                                    .build();
                            session.send(des);
                            break;
                        case DESCRIBE:
                            Header base = response.getHeader("Content-Base");
                            baseUri = (String) base.getRawValue();
                            Request set = new Request.Builder()
                                    .method(Method.SETUP)
                                    .uri(URI.create(baseUri + "track2"))
                                    .addHeader(new TransportHeader.Builder()
                                            .specifier(TransportHeader.Specifier.TCP)
                                            .broadcastType(TransportHeader.BroadcastType.unicast)
                                            .clientPort(50846, 50847)
                                            .build())
                                    .build();
                            session.send(set);
                            break;
                        case SETUP:
                            Request play = new Request.Builder()
                                    .method(Method.PLAY)
                                    .uri(baseUri)
                                    .build();
                            session.send(play);
                            break;
                        case PLAY:
                            break;
                    }
                }
            }
        });
        session.setRTCPCallback(new IResolver.IResolverCallback<IReport>() {
            @Override
            public void onResolve(IReport iReport) {
                System.out.println("iReport = [" + iReport + "]");
            }
        });
        session.setRTPCallback(new IResolver.IResolverCallback<RtpPacket>() {
            @Override
            public void onResolve(RtpPacket rtpPacket) {
//                System.out.println("rtpPacket = [" + rtpPacket + "]");
//                decoder.input(rtpPacket.getPayload(), 0, rtpPacket.getPayload().length, rtpPacket.getTimestamp());
            }
        });
        decoder.setCallback(new Decoder.Callback() {
            @Override
            public void onOutput(byte[] bytes, int offset, int size) {
                System.out.println("MainActivity.onOutput#play");
                audioTrack.write(bytes, offset, size);
            }
        });
        session.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                Request request = new Request.Builder().method(Method.OPTIONS).uri(URI.create("rtsp://172.17.0.2/Tanya.webm")).build();
                session.send(request);
                break;
            case R.id.btn_stop:
                Request stop = new Request.Builder().method(Method.PAUSE).uri(baseUri).build();
                session.send(stop);
                break;
        }
    }

}
