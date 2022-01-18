package com.xiaojukeji.kafka.manager.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackoffUtils {
    private BackoffUtils() {
    }

    /**
     * 需要进行回退的事件信息
     * <回退事件名，回退结束时间>
     */
    private static final Map<String, Long> NEED_BACK_OFF_EVENT_MAP = new ConcurrentHashMap<>();

    public static void backoff(long timeUnitMs) {
        if (timeUnitMs <= 0) {
            return;
        }

        try {
            Thread.sleep(timeUnitMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 记录回退设置
     * @param backoffEventKey 回退事件key
     * @param backoffTimeUnitMs 回退时间(ms)
     */
    public static void putNeedBackoffEvent(String backoffEventKey, Long backoffTimeUnitMs) {
        if (backoffEventKey == null || backoffTimeUnitMs == null || backoffTimeUnitMs <= 0) {
            return;
        }

        NEED_BACK_OFF_EVENT_MAP.put(backoffEventKey, backoffTimeUnitMs + System.currentTimeMillis());
    }

    /**
     * 移除回退设置
     * @param backoffEventKey 回退事件key
     */
    public static void removeNeedBackoffEvent(String backoffEventKey) {
        NEED_BACK_OFF_EVENT_MAP.remove(backoffEventKey);
    }

    /**
     * 检查是否需要回退
     * @param backoffEventKey 回退事件key
     * @return
     */
    public static boolean isNeedBackoff(String backoffEventKey) {
        Long backoffEventEndTimeUnitMs = NEED_BACK_OFF_EVENT_MAP.get(backoffEventKey);
        if (backoffEventEndTimeUnitMs == null) {
            return false;
        }

        if (backoffEventEndTimeUnitMs > System.currentTimeMillis()) {
            return true;
        }

        // 移除
        try {
            NEED_BACK_OFF_EVENT_MAP.remove(backoffEventKey, backoffEventEndTimeUnitMs);
        } catch (Exception e) {
            // 如果key不存在，这里可能出现NPE，不过不管什么异常都可以忽略
        }

        return false;
    }
}
