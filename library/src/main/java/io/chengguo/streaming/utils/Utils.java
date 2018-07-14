package io.chengguo.streaming.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

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

    public static boolean equalsSafely(String s1, String s2) {
        return s1 == s2 || (s1 != null && s1.equals(s2));
    }
}
