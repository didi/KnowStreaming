package com.xiaojukeji.kafka.manager.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 宽松API请求限制工具
 * @author zengqiao
 * @date 20/7/23
 */
public class EasyApiLimitUtils {
    private static final Long MAX_LIMIT_NUM = 10000L;

    private static final Long DEFAULT_DURATION_TIME = 24 * 60 * 60 * 1000L;

    private static final Map<String, AtomicLong> API_RECORD_MAP = new ConcurrentHashMap<>();

    public static boolean incIfNotOverFlow(String key, long maxNum) {
        AtomicLong atomicLong = API_RECORD_MAP.get(key);
        if (atomicLong == null) {
            API_RECORD_MAP.putIfAbsent(key, new AtomicLong(System.currentTimeMillis() * MAX_LIMIT_NUM));
        }

        while (true) {
            atomicLong = API_RECORD_MAP.get(key);

            long value = atomicLong.longValue();
            long timestamp = value / MAX_LIMIT_NUM;
            long presentNum = value % MAX_LIMIT_NUM;
            if (System.currentTimeMillis() - timestamp < DEFAULT_DURATION_TIME && presentNum > maxNum) {
                // 以及超过限制了
               return false;
            }

            long newValue = System.currentTimeMillis() * MAX_LIMIT_NUM + 1;
            if (System.currentTimeMillis() - timestamp < DEFAULT_DURATION_TIME && presentNum <= maxNum) {
                newValue = timestamp * MAX_LIMIT_NUM + presentNum + 1;
            }

            if (atomicLong.compareAndSet(value, newValue)) {
                return true;
            }
        }
    }

    public static void decIfNotOverFlow(String key) {
        AtomicLong atomicLong = API_RECORD_MAP.get(key);
        if (atomicLong == null) {
            API_RECORD_MAP.putIfAbsent(key, new AtomicLong(System.currentTimeMillis() * MAX_LIMIT_NUM));
        }

        while (true) {
            atomicLong = API_RECORD_MAP.get(key);

            long value = atomicLong.longValue();
            long timestamp = value / MAX_LIMIT_NUM;
            long presentNum = value % MAX_LIMIT_NUM;
            if (presentNum == 0) {
                return;
            }

            long newValue = timestamp * MAX_LIMIT_NUM + presentNum - 1;
            if (atomicLong.compareAndSet(value, newValue)) {
                return;
            }
        }
    }
}