package io.chengguo.streaming.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                    Response response = null;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (response == null) {
                            response = new Response();
                        }
                        if (line.length() == 0) {
//                            System.out.println(response);
                            if (resolverCallback != null) {
                                resolverCallback.onResolve(response);
                            }
                            response = null;
                            continue;
                        }
                        System.out.println(line);
                        //line
                        if (line.startsWith(Version.PROTOCOL)) {
                            Response.Line.parse(line);
                        }
                        //header
                        //body
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
