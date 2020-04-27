package io.chengguo.streaming.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    /**
     * 安全地Split
     *
     * @param string
     * @param regex
     * @return
     */
    public static String[] splitSafely(@Nullable String string, @NonNull String regex) {
        return string == null ? new String[]{} : string.split(regex);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * 判断字节数组是否为空
     *
     * @param bytes
     * @return
     */
    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }
}
