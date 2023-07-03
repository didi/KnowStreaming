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
public class KSConfigUtils {
    private KSConfigUtils() {
    }

    @Value(value = "${request.api-call.timeout-unit-ms:8000}")
    private Integer apiCallTimeoutUnitMs;

    public Integer getApiCallLeftTimeUnitMs(Long costedUnitMs) {
        return Math.max(1000, (int)(apiCallTimeoutUnitMs - costedUnitMs));
    }
}
