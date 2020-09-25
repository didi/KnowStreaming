package com.xiaojukeji.kafka.manager.dao.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.datasource.kafka-manager")
public class KafkaManagerProperties {
    private String jdbcUrl;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public boolean hasPG() {
        return jdbcUrl.startsWith("jdbc:postgres");
    }
}
