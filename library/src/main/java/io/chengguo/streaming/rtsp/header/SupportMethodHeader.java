package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import io.chengguo.streaming.rtsp.Method;
import io.chengguo.streaming.utils.L;

import static io.chengguo.streaming.utils.Utils.splitSafely;
import static io.chengguo.streaming.utils.Utils.trimSafely;

/**
 * 支持的方法Header
 * Created by fingerart on 2018-07-15.
 */
public class SupportMethodHeader extends StringHeader {
    private final ArrayList<Method> methods = new ArrayList<>(Method.values().length);

    public static final String DEFAULT_NAME = "Public";

    public SupportMethodHeader(String name, String value) {
        super(name, value);
    }

    public SupportMethodHeader(@NonNull String nameOrRawHeader) {
        super(nameOrRawHeader);
    }

    public SupportMethodHeader(Method... methods) {
        this(DEFAULT_NAME, parseMethods(methods));
        this.methods.addAll(Arrays.asList(methods));
    }

    @Override
    public void setRawValue(String rawValue) {
        super.setRawValue(rawValue);
        parseMethods(getRawValue());
    }

    /**
     * 解析成Method
     */
    private void parseMethods(String value) {
        String[] methodHybrid = splitSafely(value, ",");
        for (String methodStr : methodHybrid) {
            try {
                methods.add(Method.valueOf(trimSafely(methodStr)));
            } catch (Exception ignored) {
                L.w(ignored.getMessage());
            }
        }
    }

    /**
     * 是否支持指定的Method
     *
     * @param method
     * @return
     */
    public boolean isSupportMethod(Method method) {
        return methods.contains(method);
    }

    /**
     * 解析Method数组
     *
     * @param methods
     * @return
     */
    private static String parseMethods(Method[] methods) {
        StringBuffer result = new StringBuffer();
        if (methods != null) {
            final char separator = ',';
            for (Method method : methods) {
                result.append(method).append(separator);
            }
            result.deleteCharAt(result.length() - 1);//删除末尾的","
        }
        return result.toString();
    }
}
