package com.xiaojukeji.know.streaming.test.kafka.env;

public interface KafkaEnv {
    void init();

    void cleanup();

    String getBootstrapServers();

    String getZKUrl();

    String getVersion();
}
