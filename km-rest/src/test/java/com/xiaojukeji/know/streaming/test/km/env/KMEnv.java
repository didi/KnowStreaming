package com.xiaojukeji.know.streaming.test.km.env;

import java.util.function.Supplier;

public interface KMEnv {
    String SEPARATOR = ":";

    void init();

    void cleanup();

    default boolean es() {
        return true;
    }

    default boolean mysql() {
        return true;
    }

    default boolean kafka() {
        return true;
    }

    Supplier<Object> jdbcUrl();

    Supplier<Object> esUrl();
}
