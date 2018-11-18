package io.chengguo.streaming.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by fingerart on 2018-07-15.
 */
public class Utils {

    /**
     * 安全地Trim
     *
     * @param string
     * @return never be null
     */
    public static String trimSafely(@Nullable String string) {
        return string == null ? "" : string.trim();
    }

    public static String[] splitSafely(@Nullable String string, @NonNull String regex) {
        return string == null ? new String[]{} : string.split(regex);
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }
}
