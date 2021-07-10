package com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 迁移(Topic迁移/Partition迁移)
 * @author zengqiao_cn@163.com
 * @date 19/4/9
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Topic迁移")
public class ReassignTopicDTO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "目标BrokerID列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "目标RegionID")
    private Long regionId;

    @ApiModelProperty(value = "分区ID")
    private List<Integer> partitionIdList;

    @ApiModelProperty(value = "限流值(B/s)")
    private Long throttle;

    @ApiModelProperty(value = "限流上限(B/s)")
    private Long maxThrottle;

    @ApiModelProperty(value = "限流下限(B/s)")
    private Long minThrottle;

    @ApiModelProperty(value = "原本的保存时间(ms)")
    private Long originalRetentionTime;

    @ApiModelProperty(value = "迁移时的保存时间(ms)")
    private Long reassignRetentionTime;

    @ApiModelProperty(value = "开始时间(ms, 时间戳)")
    private Long beginTime;

    @ApiModelProperty(value = "备注")
    private String description;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public List<Integer> getPartitionIdList() {
        return partitionIdList;
    }

    public void setPartitionIdList(List<Integer> partitionIdList) {
        this.partitionIdList = partitionIdList;
    }

    public Long getThrottle() {
        return throttle;
    }

    public void setThrottle(Long throttle) {
        this.throttle = throttle;
    }

    public Long getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(Long maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public Long getMinThrottle() {
        return minThrottle;
    }

    public void setMinThrottle(Long minThrottle) {
        this.minThrottle = minThrottle;
    }

    public Long getOriginalRetentionTime() {
        return originalRetentionTime;
    }

    public void setOriginalRetentionTime(Long originalRetentionTime) {
        this.originalRetentionTime = originalRetentionTime;
    }

    public Long getReassignRetentionTime() {
        return reassignRetentionTime;
    }

    public void setReassignRetentionTime(Long reassignRetentionTime) {
        this.reassignRetentionTime = reassignRetentionTime;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isExistBlank(topicName)
                || ValidateUtils.isNullOrLessThanZero(throttle)
                || ValidateUtils.isNullOrLessThanZero(maxThrottle)
                || ValidateUtils.isNullOrLessThanZero(minThrottle)
                || maxThrottle < throttle || throttle < minThrottle
                || ValidateUtils.isNullOrLessThanZero(originalRetentionTime)
                || ValidateUtils.isNullOrLessThanZero(reassignRetentionTime)
                || originalRetentionTime < reassignRetentionTime
                || ValidateUtils.isNullOrLessThanZero(beginTime)) {
            return false;
        }
        if (ValidateUtils.isNull(description)) {
            description = "";
        }

        if (ValidateUtils.isEmptyList(brokerIdList) && ValidateUtils.isNull(regionId)) {
            return false;
        }
        return true;
    }
}