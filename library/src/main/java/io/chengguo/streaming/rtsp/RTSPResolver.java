package io.chengguo.streaming.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.chengguo.streaming.rtsp.header.CSeqHeader;
import io.chengguo.streaming.rtsp.header.ContentLengthHeader;
import io.chengguo.streaming.rtsp.header.SessionHeader;
import io.chengguo.streaming.rtsp.header.StringHeader;
import io.chengguo.streaming.rtsp.header.TransportHeader;

/**
 * RTSP解析器
 * Created by fingerart on 2018-09-09.
 */
class RTSPResolver implements IResolver<Integer, Response> {

    private IResolverCallback<Response> resolverCallback;
    private BufferedReader reader;

    @Override
    public void regist(InputStream inputStream) {
        //转换InputStream的类型
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void resolve(Integer firstByte) throws IOException {
        String sLine;
        ResolverByLine lineResolver = new ResolverByLine();
        while (reader != null && (sLine = reader.readLine()) != null) {
            //拼接上已被读取的第一个字节
            if (firstByte != -1) {
                sLine = Character.toString((char) firstByte.intValue()) + sLine;
                firstByte = -1;
            }
            lineResolver.resolve(sLine);
            if (lineResolver.isCompleted()) {
                if (resolverCallback != null) {
                    resolverCallback.onResolve(lineResolver.response);
                }
                break;
            }
        }
    }

    @Override
    public void setResolverCallback(IResolverCallback<Response> resolverCallback) {
        this.resolverCallback = resolverCallback;
    }

    @Override
    public void release() {
        reader = null;
        resolverCallback = null;
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

        // TODO: 2018/9/26 解析 SDP
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
