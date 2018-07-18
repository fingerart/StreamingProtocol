package io.chengguo.streaming.rtsp.header;

import java.math.BigDecimal;
import java.util.Locale;

import io.chengguo.streaming.utils.L;

import static io.chengguo.streaming.utils.Utils.splitSafely;

/**
 * RangeHeader
 * Created by fingerart on 2018-07-15.
 */
public class RangeHeader extends StringHeader {

    public static final String DEFAULT_NAME = "Range";

    private double begin;
    private double end;
    private TimeUnit timeUnit;

    public RangeHeader(String nameOrRawHeader) {
        super(nameOrRawHeader);
        this.deformat(getRawValue());
    }

    public RangeHeader(double begin, double end) {
        super(DEFAULT_NAME, format(begin, end));
        this.begin = begin;
        this.end = end;
    }

    public double getBegin() {
        return begin;
    }

    public void setBegin(double begin) {
        this.begin = begin;
        setRawValue(format(this.begin, this.end));
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
        setRawValue(format(this.begin, this.end));
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * 从字符串解析
     *
     * @param value
     */
    private void deformat(String value) {
        String[] rangeDebris = splitSafely(value, "=");
        try {
            timeUnit = TimeUnit.valueOf(rangeDebris[0].toUpperCase());
            switch (timeUnit) {
                case NPT:
                    Double[] range = timeUnit.parse(rangeDebris[1]);
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
    private static String format(double begin, double end) {
        String format = end == 0 ?
                TimeUnit.NPT.name().toLowerCase() + "=%.3f-"
                :
                TimeUnit.NPT.name().toLowerCase() + "=%.3f-%.3f";
        //格式化小数点最后一位有四舍五入的问题
        return String.format(Locale.getDefault(), format, begin, end);
    }

    public enum TimeUnit {
        NPT() {
            @Override
            public Double[] parse(String range) {
                String[] rangeDebris = splitSafely(range, "-");
                Double[] result = new Double[rangeDebris.length];
                for (int i = 0; i < rangeDebris.length; i++) {
//                    BigDecimal bd = new BigDecimal();
                    result[i] = Double.parseDouble(rangeDebris[i]);
                }
                return result;
            }
        };

        public abstract <T> T[] parse(String value);
    }
}