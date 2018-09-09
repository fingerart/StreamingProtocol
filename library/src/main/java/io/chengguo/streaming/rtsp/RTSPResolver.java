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
                        resolverCallback.onResolve(response);
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
