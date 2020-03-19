package com.xiaojukeji.kafka.manager.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动SpringBoot
 * @author huangyiminghappy@163.com
 * @date 2019-04-24
 */
@SpringBootApplication

@ComponentScan({"com.xiaojukeji.kafka.manager"})
@EnableScheduling
@EnableAutoConfiguration
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(MainApplication.class);
        sa.run(args);
    }
}
