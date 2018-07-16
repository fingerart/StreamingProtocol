package io.chengguo.streaming.rtsp.header;

import java.util.Locale;

import io.chengguo.streaming.utils.L;

import static io.chengguo.streaming.utils.Utils.splitSafely;

/**
 * RangHeader
 * Created by fingerart on 2018-07-15.
 */
public class RangHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Rang";

    private float begin;
    private float end;

    public enum TimeUnit {
        NPT() {
            @Override
            public Float[] parse(String range) {
                String[] rangeDebris = splitSafely(range, "-");
                Float[] result = new Float[rangeDebris.length];
                for (int i = 0; i < rangeDebris.length; i++) {
                    result[i] = Float.parseFloat(rangeDebris[i]);
                }
                return result;
            }
        };

        public abstract <T> T[] parse(String value);
    }

    public RangHeader(String value) {
        super(DEFAULT_NAME, value);
        this.deformat(value);
    }

    public RangHeader(float begin, float end) {
        this(format(begin, end));
        this.begin = begin;
        this.end = end;
    }

    public float getBegin() {
        return begin;
    }

    public void setBegin(float begin) {
        this.begin = begin;
        setRawValue(format(this.begin, this.end));
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
        setRawValue(format(this.begin, this.end));
    }

    /**
     * 从字符串解析
     *
     * @param value
     */
    private void deformat(String value) {
        String[] rangeDebris = splitSafely(value, "=");
        try {
            TimeUnit timeUnit = TimeUnit.valueOf(rangeDebris[0].toUpperCase());
            switch (timeUnit) {
                case NPT:
                    Float[] range = timeUnit.parse(rangeDebris[1]);
                    begin = range.length > 0 ? range[0] : 0;
                    end = range.length > 1 ? range[1] : 0;
                    break;
            }
        } catch (Exception e) {
            L.w(e.getMessage());
        }
    }

    /**
     * 格式化范围为字符串
     *
     * @param begin
     * @param end
     * @return
     */
    private static String format(float begin, float end) {
        String format = end == 0 ?
                TimeUnit.NPT.name().toLowerCase() + "=%.3f-"
                :
                TimeUnit.NPT.name().toLowerCase() + "=%.3f-%.3f";
        //格式化小数点最后一位有四舍五入的问题
        return String.format(Locale.getDefault(), format, begin, end);
    }
}