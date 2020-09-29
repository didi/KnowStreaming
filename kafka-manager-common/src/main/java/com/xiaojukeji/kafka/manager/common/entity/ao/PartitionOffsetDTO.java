package com.xiaojukeji.kafka.manager.common.entity.ao;

/**
 * Topic Offset
 * @author zengqiao
 * @date 19/6/2
 */
public class PartitionOffsetDTO {
    private Integer partitionId;

    private Long offset;

    private Long timestamp;

    public PartitionOffsetDTO() {
    }

    public PartitionOffsetDTO(Integer partitionId, Long offset) {
        this.partitionId = partitionId;
        this.offset = offset;
    }

    public PartitionOffsetDTO(Integer partitionId, Long offset, Long timestamp) {
        this.partitionId = partitionId;
        this.offset = offset;
        this.timestamp = timestamp;
    }

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TopicOffsetDTO{" +
                ", partitionId=" + partitionId +
                ", offset=" + offset +
                ", timestamp=" + timestamp +
                '}';
    }
}

