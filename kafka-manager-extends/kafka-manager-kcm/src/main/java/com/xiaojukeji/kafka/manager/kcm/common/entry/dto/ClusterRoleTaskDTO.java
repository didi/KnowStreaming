package com.xiaojukeji.kafka.manager.kcm.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="集群升级任务")
public class ClusterRoleTaskDTO extends AbstractClusterTaskDTO {
    @ApiModelProperty(value="升级顺序")
    private List<String> upgradeSequenceList;

    @ApiModelProperty(value="忽略的主机")
    private List<String> ignoreList;

    public List<String> getUpgradeSequenceList() {
        return upgradeSequenceList;
    }

    public void setUpgradeSequenceList(List<String> upgradeSequenceList) {
        this.upgradeSequenceList = upgradeSequenceList;
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    @Override
    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isBlank(taskType)
                || ValidateUtils.isBlank(kafkaPackageName)
                || ValidateUtils.isBlank(kafkaPackageMd5)
                || ValidateUtils.isBlank(serverPropertiesName)
                || ValidateUtils.isBlank(serverPropertiesMd5)
                || ValidateUtils.isEmptyList(upgradeSequenceList)
                || ValidateUtils.isNull(ignoreList)) {
            return false;
        }
        return true;
    }
}