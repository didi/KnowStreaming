package com.xiaojukeji.kafka.manager.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动SpringBoot
 * @author huangyiminghappy@163.com
 * @date 2019-04-24
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"com.xiaojukeji.kafka.manager"})
public class MainApplication {
    public static void main(String[] args) {
        try {
            SpringApplication sa = new SpringApplication(MainApplication.class);
            sa.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
