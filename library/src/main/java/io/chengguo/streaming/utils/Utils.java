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
}
