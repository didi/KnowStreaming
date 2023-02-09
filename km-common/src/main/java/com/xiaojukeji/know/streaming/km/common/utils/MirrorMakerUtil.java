package com.xiaojukeji.know.streaming.km.common.utils;

public class MirrorMakerUtil {
    public static String genCheckpointName(String sourceName) {
        return sourceName == null? "-checkpoint": sourceName + "-checkpoint";
    }

    public static String genHeartbeatName(String sourceName) {
        return sourceName == null? "-heartbeat": sourceName + "-heartbeat";
    }
}
