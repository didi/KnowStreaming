package com.xiaojukeji.kafka.manager.common.entity.ao.consumer;

/**
 * @author zengqiao
 * @date 20/1/9
 */
public class ConsumeDetailDTO {
    private Integer partitionId;

    private Long offset;

    private Long consumeOffset;

    private String consumerId;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getConsumeOffset() {
        return consumeOffset;
    }

    public void setConsumeOffset(Long consumeOffset) {
        this.consumeOffset = consumeOffset;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public String toString() {
        return "ConsumeDetailDTO{" +
                "partitionId=" + partitionId +
                ", offset=" + offset +
                ", consumeOffset=" + consumeOffset +
                ", consumerId='" + consumerId + '\'' +
                '}';
    }
}