package com.xiaojukeji.kafka.manager.web.model.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Topic采样
 * @author zengqiao
 * @date 19/4/8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "TopicDataSampleModel", description = "Topic采样")
public class TopicDataSampleModel {
    @ApiModelProperty(value = "分区Id")
    private int partitionId = 0;

    @ApiModelProperty(value = "最大采样条数[必须小于100]")
    private int maxMsgNum = 10;

    @ApiModelProperty(value = "采样超时时间[必须小于30000]")
    private int timeout = 2000;

    @ApiModelProperty(value = "采样的offset")
    private long offset = -1;

    @ApiModelProperty(value = "是否截断")
    private boolean truncate = true;

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public int getMaxMsgNum() {
        return maxMsgNum;
    }

    public void setMaxMsgNum(int maxMsgNum) {
        this.maxMsgNum = maxMsgNum;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public boolean isTruncate() {
        return truncate;
    }

    public void setTruncate(boolean truncate) {
        this.truncate = truncate;
    }

    public boolean legal() {
        if (partitionId < 0 || maxMsgNum > 100 || timeout > 30000) {
            return false;
        }
        return true;
    }
}