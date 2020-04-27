package io.chengguo.streaming.rtsp;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;

import io.chengguo.streaming.utils.Bits;

/**
 * Created by fingerart on 2018-07-17.
 */
public class RequestTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws Exception {
        String s = "Content-Base: rtsp://172.17.0.2/NeverPlay.mp3/";
        int index = s.indexOf(":");
        System.out.println(index);
        String key = s.substring(0, index);
        System.out.println(key);
        String value = s.substring(index + 1).trim();
        System.out.println(value);

        System.out.println("result[" + key + ": " + value + "]");
    }


}