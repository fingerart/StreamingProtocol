package io.chengguo.streaming.utils;

import androidx.annotation.IntRange;

import org.jetbrains.annotations.Contract;

import java.nio.ByteBuffer;
import java.util.Locale;

public class Bits {

    private Bits() {
    }

    /**
     * 获取4byte，转成long类型
     *
     * @param bb
     * @return
     */
    public static long getLongByInt(ByteBuffer bb) {
        byte[] buffer = new byte[Integer.SIZE / Byte.SIZE];
        bb.get(buffer);
        long result = 0;
        for (int i = 0; i < buffer.length; i++) {
            result |= ((long) buffer[i] & 0xff) << ((buffer.length - 1 - i) * Byte.SIZE);
        }
        return result;
    }

    /**
     * int 转 byte[]
     *
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * int转byte[]
     *
     * @param l
     * @param byteLength 需要转的字节数
     * @return
     */
    public static byte[] intToByteArray(int l, @IntRange(from = 1, to = 4) int byteLength) {
        byte[] result = new byte[byteLength];
        for (int i = 0; i < byteLength; i++) {
            //由高位到低位
            result[i] = (byte) ((l >> (byteLength - i - 1) * Byte.SIZE) & 0xFF);
        }
        return result;
    }

    /**
     * byte[] 转 int
     *
     * @param arr
     * @return
     */
    public static int byteArrayToInt(byte[] arr) {
        int result = 0;
        for (int i = 0; i < arr.length && i < 4; i++) {
            result |= arr[i] << Byte.SIZE * (arr.length - 1 - i);
        }
        return result;
    }

    /**
     * long 转 byte[]
     *
     * @param l
     * @return
     */
    public static byte[] longToByteArray(long l) {
        byte[] result = new byte[Long.SIZE / Byte.SIZE];
        //由高位到低位
        result[0] = (byte) ((l >> 56) & 0xFF);
        result[1] = (byte) ((l >> 48) & 0xFF);
        result[2] = (byte) ((l >> 40) & 0xFF);
        result[3] = (byte) ((l >> 32) & 0xFF);
        result[4] = (byte) ((l >> 24) & 0xFF);
        result[5] = (byte) ((l >> 16) & 0xFF);
        result[6] = (byte) ((l >> 8) & 0xFF);
        result[7] = (byte) (l & 0xFF);
        return result;
    }

    /**
     * long转byte[]
     *
     * @param l
     * @param byteLength 需要转的字节数
     * @return
     */
    public static byte[] longToByteArray(long l, @IntRange(from = 1, to = 8) int byteLength) {
        byte[] result = new byte[byteLength];
        for (int i = 0; i < byteLength; i++) {
            //由高位到低位
            result[i] = (byte) ((l >> (byteLength - i - 1) * Byte.SIZE) & 0xFF);
        }
        return result;
    }

    public static String dumpBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("----+---------------------------------+---------------------------------+").append("\r" + "\n");
        sb.append("    | 0   1   2   3   4   5   6   7   | 0   1   2   3   4   5   6   7   |").append("\r" + "\n");
        sb.append("----+---------------------------------+---------------------------------+").append("\r" + "\n");
        for (int i = 0, l = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i != 0) {
                    sb.append("\r\n");
                }
                sb.append(String.format(Locale.getDefault(), "%04d", l++)).append("| ");
            }
            sb.append(String.format(Locale.getDefault(),"%3d", bytes[i] & 0xFF)).append(" ");
            if ((i + 1) % 8 == 0) {
                sb.append("| ");
            }
        }
        sb.append("\r\n").append("----+---------------------------------+---------------------------------+");
        return sb.toString();
    }

    public static String dumpBytesToBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("    0           1           2           3           4").append("\r\n");
        sb.append("----+-----------+-----------+-----------+-----------+").append("\r\n");
        for (int i = 0, l = 0; i < bytes.length; i++) {
            if (i % 4 == 0) {
                if (i != 0) {
                    sb.append("\r\n");
                }
                sb.append(String.format(Locale.getDefault(), "%04d", l++)).append("| ");
            }
            String raw = String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(" ", "0");
            sb.append(raw.substring(0, 4)).append(" ").append(raw.substring(4, 8)).append(" | ");
        }
        sb.append("\r\n").append("----+-----------+-----------+-----------+-----------+");
        return sb.toString();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String dumpBytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("----+-------------------------+-------------------------+").append("\r\n");
        sb.append("    | 0  1  2  3  4  5  6  7  | 0  1  2  3  4  5  6  7  |").append("\r\n");
        sb.append("----+-------------------------+-------------------------+").append("\r\n");
        for (int i = 0, l = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i != 0) {
                    sb.append("\r\n");
                }
                sb.append(String.format(Locale.getDefault(), "%04d", l++)).append("| ");
            }
            int v = bytes[i] & 0xFF;
            sb.append(HEX_ARRAY[v >>> 4]).append(HEX_ARRAY[v & 0x0F]).append(" ");
            if ((i + 1) % 8 == 0) {
                sb.append("| ");
            }
        }
        sb.append("\r\n").append("----+-------------------------+-------------------------+");
        return sb.toString();
    }
}
