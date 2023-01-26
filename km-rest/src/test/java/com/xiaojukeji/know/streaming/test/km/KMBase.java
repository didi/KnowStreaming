package com.xiaojukeji.know.streaming.test.km;

import com.xiaojukeji.know.streaming.test.kafka.KafkaContainerTest;
import com.xiaojukeji.know.streaming.test.kafka.env.KafkaEnv;
import com.xiaojukeji.know.streaming.test.km.contrainer.KMContainer;
import com.xiaojukeji.know.streaming.test.km.env.KMEnv;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class KMBase {

    private static KMEnv kmEnv;
    protected static KafkaEnv kafkaEnv;

    @BeforeAll
    public static void init() {
        if (SystemUtils.IS_OS_WINDOWS) {
            throw new IllegalStateException("Not Support Current OS: " + SystemUtils.OS_NAME);
        }
        kmEnv = new KMContainer();
        kmEnv.init();

        if (KafkaEnv.initKafka()) {
            kafkaEnv = new KafkaContainerTest();
            kafkaEnv.init();
        }
    }


    @DynamicPropertySource
    public static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.know-streaming.jdbc-url", KMContainer.jdbcUrl());

        registry.add("spring.logi-job.jdbc-url", KMContainer.jdbcUrl());

        registry.add("spring.logi-security.jdbc-url", KMContainer.jdbcUrl());

        registry.add("spring.logi-security.jdbc-url", KMContainer.jdbcUrl());

        registry.add("es.client.address", KMContainer.esUrl());
    }

    @AfterAll
    public static void afterAll() {
//            kmEnv.cleanup();
    }

}
