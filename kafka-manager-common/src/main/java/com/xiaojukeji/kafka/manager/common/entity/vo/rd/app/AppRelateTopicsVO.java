package com.xiaojukeji.kafka.manager.common.entity.vo.rd.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@Data
@ApiModel(description="App关联Topic信息")
public class AppRelateTopicsVO {
    @ApiModelProperty(value="物理集群ID")
    private Long clusterPhyId;

    @ApiModelProperty(value="kafkaUser")
    private String kafkaUser;

    @ApiModelProperty(value="选中的Topic列表")
    private List<String> selectedTopicNameList;

    @ApiModelProperty(value="未选中的Topic列表")
    private List<String> notSelectTopicNameList;

    @ApiModelProperty(value="未建立HA的Topic列表")
    private List<String> notHaTopicNameList;
}