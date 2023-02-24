package com.xiaojukeji.know.streaming.km.core.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author zengqiao
 * @date 22/6/14
 */
@Getter
@Service
public class ConfigUtils {
    private ConfigUtils() {
    }

    @Value("${cluster-balance.ignored-topics.time-second:300}")
    private Integer     clusterBalanceIgnoredTopicsTimeSecond;
}
