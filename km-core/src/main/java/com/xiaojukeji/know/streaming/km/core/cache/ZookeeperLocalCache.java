package com.xiaojukeji.know.streaming.km.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.fourletterword.BaseFourLetterWordCmdData;

import java.util.concurrent.TimeUnit;

public class ZookeeperLocalCache {
    private static final Cache<String, String> fourLetterCmdFailedServerCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    private static final Cache<String, BaseFourLetterWordCmdData> fourLetterCmdDataCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    public static boolean canUse(String host, int port, String cmd) {
        String data = fourLetterCmdFailedServerCache.getIfPresent(gen4lwFailedKey(host, port, cmd));

        return data == null;
    }

    public static void setFailed(String host, int port, String cmd) {
        fourLetterCmdFailedServerCache.put(gen4lwFailedKey(host, port, cmd), "");
    }

    public static BaseFourLetterWordCmdData getData(String host, int port, String cmd) {
        return fourLetterCmdDataCache.getIfPresent(gen4lwFailedKey(host, port, cmd));
    }

    public static void putData(String host, int port, String cmd, BaseFourLetterWordCmdData cmdData) {
        fourLetterCmdDataCache.put(gen4lwFailedKey(host, port, cmd), cmdData);
    }

    /**************************************************** private method ****************************************************/

    private static String gen4lwFailedKey(String host, int port, String cmd) {
        return host + "@" + port + "@" + cmd;
    }



}
