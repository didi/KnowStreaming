package com.xiaojukeji.know.streaming.km.common.utils;

public class BackoffUtils {
    private BackoffUtils() {
    }

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
}
