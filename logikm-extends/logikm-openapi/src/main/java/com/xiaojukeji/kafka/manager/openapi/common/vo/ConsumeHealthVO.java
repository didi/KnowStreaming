package com.xiaojukeji.kafka.manager.openapi.common.vo;

/**
 * @author zengqiao
 * @date 20/10/26
 */
public class ConsumeHealthVO {
    private Integer healthCode;

    public ConsumeHealthVO(Integer healthCode) {
        this.healthCode = healthCode;
    }

    public Integer getHealthCode() {
        return healthCode;
    }

    public void setHealthCode(Integer healthCode) {
        this.healthCode = healthCode;
    }

    @Override
    public String toString() {
        return "ConsumeHealthVO{" +
                "healthCode=" + healthCode +
                '}';
    }
}
