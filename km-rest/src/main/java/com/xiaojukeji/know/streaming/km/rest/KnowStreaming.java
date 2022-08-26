package com.xiaojukeji.know.streaming.km.rest;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动SpringBoot
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.xiaojukeji.know.streaming.km"})
public class KnowStreaming {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnowStreaming.class);

    public static void main(String[] args) {
        try {
            SpringApplication sa = new SpringApplication(KnowStreaming.class);
            sa.run(args);
            LOGGER.info("KnowStreaming-KM started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * register prometheus
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName){
        return registry -> registry.config().commonTags("application", applicationName);
    }
}
