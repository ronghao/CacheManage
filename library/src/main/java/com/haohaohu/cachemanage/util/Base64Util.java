package com.haohaohu.cachemanage.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.nio.ByteBuffer;

/**
 * Base64编码
 *
 * @author haohao on 2017/6/22 22:40
 * @version v1.0
 */
public class Base64Util {
    public static final char[] BASE64_CODE = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/', '='
    };
    public static final int MAX_BUFF_SIZE = 4000000;
    private static final char[] legalChars =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public Base64Util() {
    }

    public static String encode(byte[] data) {
        byte start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);
        int end = len - 3;
        int i = start;
        int n = 0;

        int d;
        while (i <= end) {
            d = (data[i] & 255) << 16 | (data[i + 1] & 255) << 8 | data[i + 2] & 255;
            buf.append(legalChars[d >> 18 & 63]);
            buf.append(legalChars[d >> 12 & 63]);
            buf.append(legalChars[d >> 6 & 63]);
            buf.append(legalChars[d & 63]);
            i += 3;
            if (n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }

        if (i == start + len - 2) {
            d = (data[i] & 255) << 16 | (data[i + 1] & 255) << 8;
            buf.append(legalChars[d >> 18 & 63]);
            buf.append(legalChars[d >> 12 & 63]);
            buf.append(legalChars[d >> 6 & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            d = (data[i] & 255) << 16;
            buf.append(legalChars[d >> 18 & 63]);
            buf.append(legalChars[d >> 12 & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    private static int decode(char c) {
        if (c >= 65 && c <= 90) {
            return c - 65;
        } else if (c >= 97 && c <= 122) {
            return c - 97 + 26;
        } else if (c >= 48 && c <= 57) {
            return c - 48 + 26 + 26;
        } else {
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected persondatafragment_code: " + c);
            }
        }
    }

    public static byte[] decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            decode(s, bos);
        } catch (IOException var5) {
            throw new RuntimeException();
        }

        byte[] decodedBytes = bos.toByteArray();

        try {
            bos.close();
            bos = null;
        } catch (IOException var4) {
            System.err.println("Error while decoding BASE64: " + var4.toString());
        }

        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= 32) {
                ++i;
            }

            if (i == len) {
                break;
            }

            int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(
                    s.charAt(i + 2)) << 6) + decode(s.charAt(i + 3));
            os.write(tri >> 16 & 255);
            if (s.charAt(i + 2) == 61) {
                break;
            }

            os.write(tri >> 8 & 255);
            if (s.charAt(i + 3) == 61) {
                break;
            }

            os.write(tri & 255);
            i += 4;
        }
    }

    public static boolean check(String str) throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader(new StringBufferInputStream(str)));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String line = null;
        String lastLine = null;

        byte[] lastLineBytes;
        while ((line = br.readLine()) != null) {
            lastLine = line;
            lastLineBytes = line.getBytes();
            tryAllocate(buffer, lastLineBytes.length);
            buffer.put(lastLineBytes);
        }

        lastLineBytes = lastLine.getBytes();
        int equalsNum = 0;

        for (int src = lastLineBytes.length - 1; src >= lastLineBytes.length - 2; --src) {
            if (lastLineBytes[src] == 61) {
                ++equalsNum;
            }
        }

        byte[] var10 = buffer.toString().getBytes();

        for (int i = 0; i < var10.length - equalsNum; ++i) {
            char c = (char) var10[i];
            if ((c < 97 || c > 122)
                    && (c < 65 || c > 90)
                    && (c < 48 || c > 57)
                    && c != 43
                    && c != 47) {
                return false;
            }
        }

        if ((var10.length - equalsNum) % 4 != 0) {
            return false;
        } else {
            return true;
        }
    }

    public static ByteBuffer tryAllocate(ByteBuffer buffer, int length) {
        if (length > buffer.remaining()) {
            buffer.flip();
            return ByteBuffer.allocate(roundup(buffer.limit() + length)).put(buffer);
        } else {
            return buffer;
        }
    }

    public static int roundup(int length) {
        if (length > 4000000) {
            throw new IllegalArgumentException("length too large!");
        } else {
            int capacity;
            for (capacity = 16; length < capacity; capacity <<= 1) {
                ;
            }

            return capacity;
        }
    }
}
