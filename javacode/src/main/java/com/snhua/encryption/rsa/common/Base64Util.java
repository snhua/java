package com.snhua.encryption.rsa.common;

/**
 * Created by SongpoLiu on 2017/7/18.
 */

import org.apache.commons.net.util.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Util {
    public Base64Util() {
    }

    public static String getBASE64(String s) {
        if (s == null) {
            return null;
        } else {
            try {
                return getBASE64(s.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public static String getBASE64(byte[] b) {
        byte[] rb = Base64.encodeBase64(b);
        if (rb == null) {
            return null;
        } else {
            try {
                return new String(rb, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    public static String getFromBASE64(String s) {
        if (s == null) {
            return null;
        } else {
            try {
                byte[] b = getBytesBASE64(s);
                return b == null ? null : new String(b, "UTF-8");
            } catch (UnsupportedEncodingException var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public static byte[] getBytesBASE64(String s) {
        if (s == null) {
            return null;
        } else {
            try {
                byte[] b = Base64.decodeBase64(s.getBytes("UTF-8"));
                return b;
            } catch (Exception var2) {
                return null;
            }
        }
    }
}
