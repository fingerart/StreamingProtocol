package io.chengguo.streaming.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.ContentLengthHeader;
import io.chengguo.streaming.rtsp.header.SessionHeader;
import io.chengguo.streaming.rtsp.header.StringHeader;
import io.chengguo.streaming.rtsp.header.TransportHeader;

/**
 * RTSP解析器
 * Created by fingerart on 2018-09-09.
 */
class RTSPResolver implements IResolver<Response> {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private IResolverCallback<Response> resolverCallback;
    private BufferedReader reader;
    private Future<?> future;

    @Override
    public void target(InputStream inputStream) {
        //转换InputStream的类型
        reader = new BufferedReader(new InputStreamReader(inputStream));
        reading();
    }

    @Override
    public void setResolverCallback(IResolverCallback<Response> resolverCallback) {
        this.resolverCallback = resolverCallback;
    }

    private void reading() {
        future = EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String sLine;
                    ResolverByLine resolver = null;
                    while ((sLine = reader.readLine()) != null) {
//                        System.out.println("receive: [" + sLine + "]");
                        if (resolver == null) {
                            resolver = new ResolverByLine();
                        }
                        resolver.resolve(sLine);
                        if (resolver.isCompleted()) {
//                            System.out.println("resolved:\r\n" + resolver.response);
                            if (resolverCallback != null) {

                                resolverCallback.onResolve(resolver.response);
                            }
                            resolver = null;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void release() {
        if (future != null) {
            future.cancel(true);
            reader = null;
            future = null;
            resolverCallback = null;
        }
    }

    /**
     * 解析单行字符串
     */
    public static class ResolverByLine {

        private static final int STEP_LINE = 1 << 0;
        private static final int STEP_HEADER = 1 << 2;
        private static final int STEP_BODY = 1 << 3;
        private static final int STEP_COMPLETED = 1 << 4;

        private int currentStep = STEP_LINE;

        private Response response;

        public ResolverByLine() {
            response = new Response();
        }

        public void resolve(String sLine) {
            if ((currentStep & STEP_LINE) != 0 && sLine.startsWith(Version.PROTOCOL)) {
                Response.Line line = Response.Line.parse(sLine);
                response.setLine(line);
                currentStep = STEP_HEADER;
            } else if ((currentStep & STEP_HEADER) != 0) {
                if (sLine.length() == 0) {
                    int contentLength = response.getContentLength();
                    if (contentLength > 0) {
                        currentStep = STEP_BODY;
                    } else {
                        currentStep = STEP_COMPLETED;
                    }
                } else {
                    resolveHeader(sLine);
                }
            } else if ((currentStep & STEP_BODY) != 0) {
                response.appendBody(sLine);
                int bodyLength = response.getBody().getLength();
                int contentLength = response.getContentLength();
//                System.out.println("length: " + bodyLength + "/" + contentLength);
                if (bodyLength >= contentLength) {
                    currentStep = STEP_COMPLETED;
                }
            } else {
                System.out.println("未知内容：" + sLine);
            }
        }

        private void resolveHeader(String sLine) {
            if (sLine.startsWith(ContentLengthHeader.DEFAULT_NAME)) {
                response.addHeader(new ContentLengthHeader(sLine));
            } else if (sLine.startsWith(TransportHeader.DEFAULT_NAME)) {
                response.addHeader(new TransportHeader(sLine));
            } else if (sLine.startsWith(SessionHeader.DEFAULT_NAME)) {
                response.addHeader(new SessionHeader(sLine));
            } else if (sLine.startsWith(CSeqHeader.DEFAULT_NAME)) {
                response.addHeader(new CSeqHeader(sLine));
            } else {
                response.addHeader(new StringHeader(sLine));
            }
        }

        public boolean isCompleted() {
            return (currentStep & STEP_COMPLETED) != 0;
        }
    }
}
