package io.chengguo.streaming.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.chengguo.streaming.rtsp.header.ContentLengthHeader;
import io.chengguo.streaming.rtsp.header.StringHeader;

/**
 * Created by fingerart on 2018-09-09.
 */
class RTSPResolver implements IResolver<Response> {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private IResolverCallback<Response> resolverCallback;
    private BufferedReader reader;

    @Override
    public void target(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
        reading();
    }

    @Override
    public void setResolverCallback(IResolverCallback<Response> resolverCallback) {
        this.resolverCallback = resolverCallback;
    }

    private void reading() {
        EXECUTOR.execute(new Runnable() {
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
                            System.out.println("resolved:\r\n" + resolver.response);
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
                int contentLength = response.getContentLength();
                if (sLine.length() == 0) {
                    if (contentLength > 0) {
                        currentStep = STEP_BODY;
                    } else {
                        currentStep = STEP_COMPLETED;
                    }
                } else if (sLine.startsWith(ContentLengthHeader.DEFAULT_NAME)) {
                    response.addHeader(new ContentLengthHeader(sLine));
                } else {
                    response.addHeader(new StringHeader(sLine));
                }
            } else if ((currentStep & STEP_BODY) != 0) {
                response.appendBody(sLine);
                int bodyLength = response.getBody().getLength();
                int contentLength = response.getContentLength();
//                System.out.println("length: " + bodyLength + "/" + contentLength);
                if (bodyLength >= contentLength) {
                    currentStep = STEP_COMPLETED;
                }
            }
        }

        public boolean isCompleted() {
            return (currentStep & STEP_COMPLETED) != 0;
        }
    }
}
