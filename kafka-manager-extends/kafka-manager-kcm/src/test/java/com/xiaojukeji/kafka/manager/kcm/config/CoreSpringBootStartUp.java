package com.xiaojukeji.kafka.manager.kcm.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@ServletComponentScan
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"com.xiaojukeji.kafka.manager"})
public class CoreSpringBootStartUp {
    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(CoreSpringBootStartUp.class);
        sa.run(args);
    }

}
