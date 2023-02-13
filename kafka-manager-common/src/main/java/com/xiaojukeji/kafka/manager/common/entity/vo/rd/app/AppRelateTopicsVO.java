package com.xiaojukeji.kafka.manager.common.entity.vo.rd.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@Data
@NoArgsConstructor
@ApiModel(description="App关联Topic信息")
public class AppRelateTopicsVO {
    @ApiModelProperty(value="物理集群ID")
    private Long clusterPhyId;

    @ApiModelProperty(value="kafkaUser")
    private String kafkaUser;

    @ApiModelProperty(value="clientId")
    private String clientId;

    @ApiModelProperty(value="已建立HA的Client")
    private List<String> haClientIdList;

    @ApiModelProperty(value="选中的Topic列表")
    private List<String> selectedTopicNameList;

    @ApiModelProperty(value="未选中的Topic列表")
    private List<String> notSelectTopicNameList;

    @ApiModelProperty(value="未建立HA的Topic列表")
    private List<String> notHaTopicNameList;

    public AppRelateTopicsVO(Long clusterPhyId, String kafkaUser, String clientId) {
        this.clusterPhyId = clusterPhyId;
        this.kafkaUser = kafkaUser;
        this.clientId = clientId;
        this.selectedTopicNameList = new ArrayList<>();
        this.notSelectTopicNameList = new ArrayList<>();
        this.notHaTopicNameList = new ArrayList<>();
    }

    public void addSelectedIfNotExist(String topicName) {
        if (selectedTopicNameList.contains(topicName)) {
            return;
        }

        selectedTopicNameList.add(topicName);
    }

    public void addNotSelectedIfNotExist(String topicName) {
        if (notSelectTopicNameList.contains(topicName)) {
            return;
        }

        notSelectTopicNameList.add(topicName);
    }

    public void addNotHaIfNotExist(String topicName) {
        if (notHaTopicNameList.contains(topicName)) {
            return;
        }

        notHaTopicNameList.add(topicName);
    }
}