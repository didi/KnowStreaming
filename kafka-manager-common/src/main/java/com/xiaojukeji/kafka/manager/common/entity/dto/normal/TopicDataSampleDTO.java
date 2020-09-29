package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.constant.TopicSampleConstant;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * Topic采样
 * @author zengqiao
 * @date 19/4/8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicDataSampleDTO {
    @ApiModelProperty(value = "分区Id")
    private Integer partitionId;

    @ApiModelProperty(value = "最大采样条数[必须小于100]")
    private Integer maxMsgNum;

    @ApiModelProperty(value = "采样超时时间[必须小于10000]")
    private Integer timeout;

    @ApiModelProperty(value = "采样offset")
    private Long offset;

    @ApiModelProperty(value = "截断")
    private Boolean truncate;

    @ApiModelProperty(value = "是否是集群ID, 默认不是")
    private Boolean isPhysicalClusterId;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Integer getMaxMsgNum() {
        return maxMsgNum;
    }

    public void setMaxMsgNum(Integer maxMsgNum) {
        this.maxMsgNum = maxMsgNum;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Boolean getTruncate() {
        return truncate;
    }

    public void setTruncate(Boolean truncate) {
        this.truncate = truncate;
    }

    public Boolean getIsPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setIsPhysicalClusterId(Boolean isPhysicalClusterId) {
        this.isPhysicalClusterId = isPhysicalClusterId;
    }

    @Override
    public String toString() {
        return "TopicDataSampleDTO{" +
                "partitionId=" + partitionId +
                ", maxMsgNum=" + maxMsgNum +
                ", timeout=" + timeout +
                ", offset=" + offset +
                ", truncate=" + truncate +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                '}';
    }

    public void adjustConfig() {
        timeout = Math.min(timeout, TopicSampleConstant.MAX_TIMEOUT_UNIT_MS);
        maxMsgNum = Math.min(maxMsgNum, TopicSampleConstant.MAX_MSG_NUM);
        if (ValidateUtils.isNull(isPhysicalClusterId)) {
            isPhysicalClusterId = false;
        }
    }
}