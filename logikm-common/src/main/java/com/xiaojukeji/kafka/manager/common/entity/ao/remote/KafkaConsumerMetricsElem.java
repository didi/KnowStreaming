package com.xiaojukeji.kafka.manager.common.entity.ao.remote;

/**
 * @author zengqiao
 * @date 20/8/31
 */
public class KafkaConsumerMetricsElem {
    private Integer partitionId;

    private Long partitionOffset;

    private Long consumeOffset;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getPartitionOffset() {
        return partitionOffset;
    }

    public void setPartitionOffset(Long partitionOffset) {
        this.partitionOffset = partitionOffset;
    }

    public Long getConsumeOffset() {
        return consumeOffset;
    }

    public void setConsumeOffset(Long consumeOffset) {
        this.consumeOffset = consumeOffset;
    }

    @Override
    public String toString() {
        return "KafkaConsumerMetricsElem{" +
                "partitionId=" + partitionId +
                ", partitionOffset=" + partitionOffset +
                ", consumeOffset=" + consumeOffset +
                '}';
    }
}