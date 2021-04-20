package com.xiaojukeji.kafka.manager.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动SpringBoot
 *
 * @author huangyiminghappy@163.com
 * @date 2019-04-24
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.xiaojukeji.kafka.manager"})
public class MainApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication sa = new SpringApplication(MainApplication.class);
            sa.run(args);
            LOGGER.info("MainApplication started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
