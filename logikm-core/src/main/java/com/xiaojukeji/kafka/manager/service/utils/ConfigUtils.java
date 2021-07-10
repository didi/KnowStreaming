package com.xiaojukeji.kafka.manager.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author zengqiao
 * @date 20/4/26
 */
@Service("configUtils")
public class ConfigUtils {
    @Value(value = "${custom.idc}")
    private String idc;

    @Value(value = "${spring.profiles.active}")
    private String kafkaManagerEnv;

    @Value(value = "${custom.store-metrics-task.save-days}")
    private Long maxMetricsSaveDays;

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getKafkaManagerEnv() {
        return kafkaManagerEnv;
    }

    public void setKafkaManagerEnv(String kafkaManagerEnv) {
        this.kafkaManagerEnv = kafkaManagerEnv;
    }

    public Long getMaxMetricsSaveDays() {
        return maxMetricsSaveDays;
    }

    public void setMaxMetricsSaveDays(Long maxMetricsSaveDays) {
        this.maxMetricsSaveDays = maxMetricsSaveDays;
    }
}
