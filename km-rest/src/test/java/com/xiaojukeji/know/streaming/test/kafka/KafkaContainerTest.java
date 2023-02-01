package com.xiaojukeji.know.streaming.test.kafka;

import com.xiaojukeji.know.streaming.test.kafka.env.KafkaEnv;
import com.xiaojukeji.know.streaming.test.km.env.KMEnv;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerTest implements KafkaEnv {
    private static final String KAFKA_VERSION = "7.3.1";
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse(
            "confluentinc/cp-kafka" + KMEnv.SEPARATOR + KAFKA_VERSION);
    static KafkaContainer KAFKA_CONTAINER = new KafkaContainer(KAFKA_IMAGE)
            .withEnv("TZ", "Asia/Shanghai");

    @Override
    public void init() {
        Startables.deepStart(KAFKA_CONTAINER).join();
    }

    @Override
    public void cleanup() {
        /*
         * 不需要手动调用清理容器
         * 1. test执行结束后testcontainer会清理容器
         * 2. junit5的@AfterAll方法会在SpringBoot生命周期结束前执行，导致数据库连接无法关闭
         **/
//        if (KAFKA_CONTAINER != null) {
//            KAFKA_CONTAINER.close();
//        }
    }

    @Override
    public String getBootstrapServers() {
        return KAFKA_CONTAINER.getBootstrapServers();
    }

    @Override
    public String getZKUrl() {
        return String.format("%s:%d", KAFKA_CONTAINER.getHost(), KAFKA_CONTAINER.getMappedPort(2181));
    }

    @Override
    public String getVersion() {
        return KAFKA_VERSION;
    }
}
