package com.xiaojukeji.know.streaming.test.km.env;

public interface KMEnv {
    String SEPARATOR = ":";

    void init();

    void cleanup();

    static boolean initES() {
        return true;
    }

    static boolean initMySQL() {
        return true;
    }
}
