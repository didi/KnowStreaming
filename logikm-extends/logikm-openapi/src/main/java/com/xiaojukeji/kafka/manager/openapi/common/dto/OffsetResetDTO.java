package com.xiaojukeji.kafka.manager.openapi.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetResetTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "重置消费偏移")
public class OffsetResetDTO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "消费组")
    private String consumerGroup;

    @ApiModelProperty(value = "消费组位置")
    private String location;

    @ApiModelProperty(value = "重置的方式[0:依据时间进行重置, 1:指定分区offset进行重置]")
    private Integer offsetResetType;

    @ApiModelProperty(value = "依据时间进行重置时, 传的参数, 13位时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "指定分区进行重置时, 传的参数")
    private List<PartitionOffsetDTO> partitionOffsetDTOList;

    @ApiModelProperty(value = "如果消费组不存在则创建")
    private Boolean createIfAbsent = Boolean.FALSE;

    @ApiModelProperty(value = "使用的AppID")
    private String appId;

    @ApiModelProperty(value = "App密码")
    private String password;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "系统code")
    private String systemCode;

    /**
     * 默认使用assign的方式进行重置,
     * 但是使用assign方式对于多个Topic的消费使用同一个消费组的场景, 需要停掉所有的client才可以重置成功, 否则重置失败
     *
     * 使用subscribe重置offset, 针对上面的场景可以重置成功, 但是涉及到poll函数调用, 所以默认是关闭的
     */
    private Boolean subscribeReset = Boolean.FALSE; // 订阅重置, 默认是assign方式重置

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

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getOffsetResetType() {
        return offsetResetType;
    }

    public void setOffsetResetType(Integer offsetResetType) {
        this.offsetResetType = offsetResetType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<PartitionOffsetDTO> getPartitionOffsetDTOList() {
        return partitionOffsetDTOList;
    }

    public void setPartitionOffsetDTOList(List<PartitionOffsetDTO> partitionOffsetDTOList) {
        this.partitionOffsetDTOList = partitionOffsetDTOList;
    }

    public Boolean getCreateIfAbsent() {
        return createIfAbsent;
    }

    public void setCreateIfAbsent(Boolean createIfAbsent) {
        this.createIfAbsent = createIfAbsent;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public Boolean getSubscribeReset() {
        return subscribeReset;
    }

    public void setSubscribeReset(Boolean subscribeReset) {
        this.subscribeReset = subscribeReset;
    }

    @Override
    public String toString() {
        return "OffsetResetModel{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", offsetResetType=" + offsetResetType +
                ", timestamp=" + timestamp +
                ", partitionOffsetDTOList=" + partitionOffsetDTOList +
                ", createIfAbsent=" + createIfAbsent +
                ", appId='" + appId + '\'' +
                ", password='" + password + '\'' +
                ", operator='" + operator + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", subscribeReset=" + subscribeReset +
                '}';
    }

    public boolean legal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || StringUtils.isEmpty(consumerGroup)
                || StringUtils.isEmpty(location)
                || offsetResetType == null
                || StringUtils.isEmpty(operator)) {
            return false;
        }
        appId = (appId == null? "": appId);
        password = (password == null? "": password);
        if (createIfAbsent == null) {
            createIfAbsent = false;
        }
        if (subscribeReset == null) {
            subscribeReset = false;
        }

        // 只能依据时间或者offset中的一个进行重置
        if (OffsetResetTypeEnum.RESET_BY_TIME.getCode().equals(offsetResetType)) {
            return timestamp != null;
        } else if (OffsetResetTypeEnum.RESET_BY_OFFSET.getCode().equals(offsetResetType)) {
            return partitionOffsetDTOList != null;
        }
        return false;
    }
}
