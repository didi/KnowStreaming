package com.xiaojukeji.know.streaming.test.container.kafka;

import com.xiaojukeji.know.streaming.test.container.BaseTestContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestContainer extends BaseTestContainer {

    // kafka容器
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.1")
    ).withEnv("TZ", "Asia/Shanghai");

    @Override
    public void init() {
        Startables.deepStart(KAFKA_CONTAINER).join();
    }

    @Override
    public void cleanup() {
    }

    public String getBootstrapServers() {
        return KAFKA_CONTAINER.getBootstrapServers();
    }

    public String getZKUrl() {
        return String.format("%s:%d", KAFKA_CONTAINER.getHost(), KAFKA_CONTAINER.getMappedPort(2181));
    }
}
