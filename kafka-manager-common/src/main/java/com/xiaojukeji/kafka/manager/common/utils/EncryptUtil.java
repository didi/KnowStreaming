package com.xiaojukeji.kafka.manager.common.utils;

import java.security.MessageDigest;

/**
 * @author zengqiao
 * @date 20/3/17
 */
public class EncryptUtil {
    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String md5(String key) {
        return md5(key.getBytes());
    }

    public static String md5(byte[] btInput) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            // 使用指定的字节更新摘要
            mdInst.update(btInput);

            // 获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            char[] str = new char[md.length * 2];
            for (int i = 0, k = 0; i < md.length; i++) {
                str[k++] = HEX_DIGITS[md[i] >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[md[i] & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}