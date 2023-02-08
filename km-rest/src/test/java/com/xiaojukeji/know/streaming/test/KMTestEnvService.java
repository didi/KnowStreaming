package com.xiaojukeji.know.streaming.test;

import com.xiaojukeji.know.streaming.test.container.es.ESTestContainer;
import com.xiaojukeji.know.streaming.test.container.kafka.KafkaTestContainer;
import com.xiaojukeji.know.streaming.test.container.mysql.MySQLTestContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class KMTestEnvService {
    private static final boolean useES      = true;
    private static final boolean useMysql   = true;
    private static final boolean useKafka   = true;


    private static MySQLTestContainer mySQLTestContainer;

    private static ESTestContainer esTestContainer;

    private static KafkaTestContainer kafkaTestContainer;

    @BeforeAll
    static void init() {
        if (useES) {
            mySQLTestContainer = new MySQLTestContainer();
            mySQLTestContainer.init();
        }

        if (useMysql) {
            esTestContainer = new ESTestContainer();
            esTestContainer.init();
        }

        if (useKafka) {
            kafkaTestContainer = new KafkaTestContainer();
            kafkaTestContainer.init();
        }
    }


    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.know-streaming.jdbc-url", mySQLTestContainer.jdbcUrl());
        registry.add("spring.logi-job.jdbc-url", mySQLTestContainer.jdbcUrl());
        registry.add("spring.logi-security.jdbc-url", mySQLTestContainer.jdbcUrl());

        registry.add("es.client.address", esTestContainer.esUrl());
    }


    @AfterAll
    static void destroy() {
        if (mySQLTestContainer != null) {
            mySQLTestContainer.cleanup();
        }

        if (esTestContainer != null) {
            esTestContainer.cleanup();
        }

        if (kafkaTestContainer != null) {
            kafkaTestContainer.cleanup();
        }
    }

    protected String bootstrapServers() {
        return kafkaTestContainer.getBootstrapServers();
    }

    protected String zookeeperUrl() {
        return kafkaTestContainer.getZKUrl();
    }
}
