package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;

import java.util.ArrayList;

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

    public static final String DEFAULT_NAME = "public";

    public SupportMethodHeader(@NonNull String value) {
        super(DEFAULT_NAME, value);
    }

    @Override
    public void setRawValue(String value) {
        super.setRawValue(value);
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
     * 添加支持的Method
     *
     * @param method
     */
    public void addSupportMethod(Method method) {
        if (!methods.contains(method)) {
            methods.add(method);
            updateRawValue();
        }
    }

    /**
     * 更新
     */
    protected void updateRawValue() {
        final String separator = ", ";
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            sb.append(method.name()).append(separator);
        }
        sb.substring(0, sb.length() - separator.length());
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
}
