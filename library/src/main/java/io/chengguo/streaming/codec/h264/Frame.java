package io.chengguo.streaming.codec.h264;

public class Frame {
    public Type type;

    public static class Type {
        public int forbidden;
        public int refIdc;
        public int type;

        public Type(byte b) {
            forbidden = b >> 7;
            refIdc = b >> 5 & 0x3;
            type = b & 0x1F;
        }

        @Override
        public String toString() {
            return "Type{" +
                    "forbidden=" + forbidden +
                    ", refIdc=" + refIdc +
                    ", type=" + type +
                    '}';
        }
    }


}
