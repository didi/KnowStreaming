package com.xiaojukeji.kafka.manager.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 迁移(Topic迁移/Partition迁移)
 * @author zengqiao_cn@163.com
 * @date 19/4/9
 */
@ApiModel(value = "MigrationCreateModel", description = "创建迁移任务")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrationCreateModel {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "限流值(B/s)")
    private Long throttle;

    @ApiModelProperty(value = "目标BrokerID列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "分区ID")
    private List<Integer> partitionIdList;

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

    public Long getThrottle() {
        return throttle;
    }

    public void setThrottle(Long throttle) {
        this.throttle = throttle;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public List<Integer> getPartitionIdList() {
        return partitionIdList;
    }

    public void setPartitionIdList(List<Integer> partitionIdList) {
        this.partitionIdList = partitionIdList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean legal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || throttle == null || throttle <= 0
                || brokerIdList == null) {
            return false;
        }
        return true;
    }
}