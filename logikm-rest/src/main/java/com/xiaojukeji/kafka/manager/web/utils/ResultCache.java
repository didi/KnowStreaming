package com.xiaojukeji.kafka.manager.web.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.kafka.manager.common.entity.Result;

import java.util.concurrent.TimeUnit;

/**
 * @author zengqiao
 * @date 20/9/3
 */
public class ResultCache {
    private static final Cache<String, Result> CONTROLLER_RESULT_CACHE = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(60, TimeUnit.SECONDS).build();

    public static void put(String key, Result result) {
        CONTROLLER_RESULT_CACHE.put(key, result);
    }

    public static Result get(String key) {
        return CONTROLLER_RESULT_CACHE.getIfPresent(key);
    }
}