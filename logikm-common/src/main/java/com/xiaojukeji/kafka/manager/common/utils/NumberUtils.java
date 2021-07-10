package com.xiaojukeji.kafka.manager.common.utils;

/**
 * @author zhongyuankai
 * @date 2020/6/8
 */
public class NumberUtils {

    public static Long string2Long(String s) {
        if (ValidateUtils.isNull(s)) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
        }
        return null;
    }

    public static Integer string2Integer(String s) {
        if (ValidateUtils.isNull(s)) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
        }
        return null;
    }
}
