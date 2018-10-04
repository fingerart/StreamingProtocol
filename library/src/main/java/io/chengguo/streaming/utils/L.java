package io.chengguo.streaming.utils;

/**
 * Created by fingerart on 2018-07-15.
 */
public class L {
    public static void i(String msg) {
        System.out.println(msg);
    }

    public static void d(String msg) {
        System.out.println(msg);
    }

    public static void w(String msg) {
        System.out.println(msg);
    }

    public static void e(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }
}
