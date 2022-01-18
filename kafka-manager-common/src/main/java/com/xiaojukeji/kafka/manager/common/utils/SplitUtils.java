package com.xiaojukeji.kafka.manager.common.utils;

/**
 * @className: SplitUtils
 * @description: Split string of type keyValue
 * @author: Hu.Yue
 * @date: 2021/8/4
 **/
public class SplitUtils {

    public static String keyValueSplit(String keyValue){
        return keyValue.split(":\\s+")[1];
    }
}
