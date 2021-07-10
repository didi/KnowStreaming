package com.xiaojukeji.kafka.manager.common.utils;

import java.util.UUID;

/**
 * @author zengqiao
 * @date 20/9/8
 */
public class UUIDUtils {
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "_");
    }
}