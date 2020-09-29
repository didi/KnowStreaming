package com.xiaojukeji.kafka.manager.common.entity.ao;

/**
 * @author zhongyuankai
 * @date 2020/5/26
 */
public class PartitionAttributeDTO {
    private Long logSize;

    public Long getLogSize() {
        return logSize;
    }

    public void setLogSize(Long logSize) {
        this.logSize = logSize;
    }

    @Override
    public String toString() {
        return "PartitionAttributeDTO{" +
                "logSize=" + logSize +
                '}';
    }
}
