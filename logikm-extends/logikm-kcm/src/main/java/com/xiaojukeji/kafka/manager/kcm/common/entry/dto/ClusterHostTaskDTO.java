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
@ApiModel(description="集群[部署|升级|扩容]任务")
public class ClusterHostTaskDTO extends AbstractClusterTaskDTO {
    @ApiModelProperty(value="主机列表")
    private List<String> hostList;

    public List<String> getHostList() {
        return hostList;
    }

    public void setHostList(List<String> hostList) {
        this.hostList = hostList;
    }

    @Override
    public String toString() {
        return "ClusterHostTaskDTO{" +
                "hostList=" + hostList +
                ", clusterId=" + clusterId +
                ", taskType='" + taskType + '\'' +
                ", kafkaPackageName='" + kafkaPackageName + '\'' +
                ", kafkaPackageMd5='" + kafkaPackageMd5 + '\'' +
                ", serverPropertiesName='" + serverPropertiesName + '\'' +
                ", serverPropertiesMd5='" + serverPropertiesMd5 + '\'' +
                '}';
    }

    @Override
    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isBlank(taskType)
                || ValidateUtils.isBlank(kafkaPackageName)
                || ValidateUtils.isBlank(kafkaPackageMd5)
                || ValidateUtils.isBlank(serverPropertiesName)
                || ValidateUtils.isBlank(serverPropertiesMd5)
                || ValidateUtils.isEmptyList(hostList)) {
            return false;
        }
        return true;
    }
}