package com.xiaojukeji.kafka.manager.common.entity.dto.op;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 21/01/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="优选为Controller的候选者")
public class ControllerPreferredCandidateDTO {
    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="优选为controller的BrokerId")
    private List<Integer> brokerIdList;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    @Override
    public String toString() {
        return "ControllerPreferredCandidateDTO{" +
                "clusterId=" + clusterId +
                ", brokerIdList=" + brokerIdList +
                '}';
    }
}
