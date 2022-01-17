package com.xiaojukeji.kafka.manager.service.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author zengqiao
 * @date 20/4/26
 */
@Data
@Service("configUtils")
public class ConfigUtils {
    private ConfigUtils() {
    }

    @Value(value = "${custom.idc:cn}")
    private String idc;

    @Value(value = "${spring.profiles.active:dev}")
    private String kafkaManagerEnv;

    @Value(value = "${spring.application.version:unknown}")
    private String applicationVersion;
}
