package com.xiaojukeji.know.streaming.test.km;

import com.xiaojukeji.know.streaming.test.kafka.KafkaContainerTest;
import com.xiaojukeji.know.streaming.test.kafka.env.KafkaEnv;
import com.xiaojukeji.know.streaming.test.km.contrainer.KMContainer;
import com.xiaojukeji.know.streaming.test.km.env.KMEnv;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class KMBase {
    private static KMEnv kmEnv;
    private static KafkaEnv kafkaEnv;

    @BeforeAll
    static void init() {
        if (container()) {
            kmEnv = new KMContainer();
            kmEnv.init();

            if (kmEnv.kafka()) {
                kafkaEnv = new KafkaContainerTest();
                kafkaEnv.init();
            }
        }
    }


    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.know-streaming.jdbc-url", KMBase.kmEnv.jdbcUrl());

        registry.add("spring.logi-job.jdbc-url", KMBase.kmEnv.jdbcUrl());

        registry.add("spring.logi-security.jdbc-url", KMBase.kmEnv.jdbcUrl());

        registry.add("spring.logi-security.jdbc-url", KMBase.kmEnv.jdbcUrl());

        registry.add("es.client.address", KMBase.kmEnv.esUrl());
    }


    @AfterAll
    static void destroy() {
        if (kmEnv != null) {
            kmEnv.cleanup();
        }
        if (kafkaEnv != null) {
            kafkaEnv.cleanup();
        }
    }

    static boolean container() {
        return true;
    }

    protected String kafkaVersion() {
        return kafkaEnv.getVersion();
    }

    protected String bootstrapServers() {
        return kafkaEnv.getBootstrapServers();
    }

    protected String zookeeperUrl() {
        return kafkaEnv.getZKUrl();
    }
}
