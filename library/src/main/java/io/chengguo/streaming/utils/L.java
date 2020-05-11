package io.chengguo.streaming.utils;

import android.util.Log;

import java.util.logging.Logger;

/**
 * Created by fingerart on 2018-07-15.
 */
public class L {
    private static boolean isEnabled;
    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public static void i(String msg) {
        if (!isEnabled) {
            return;
        }
        System.out.println(msg);
    }

    public static void d(String msg) {
        if (!isEnabled) {
            return;
        }
        System.out.println(msg);
    }

    public static void w(String msg) {
        if (!isEnabled) {
            return;
        }
        System.out.println(msg);
    }

    public static void e(String tag, String message) {
        e(tag, message, null);
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (!isEnabled) {
            return;
        }
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
