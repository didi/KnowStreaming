package com.xiaojukeji.kafka.manager.common.entity.dto.gateway;

import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "配额调整")
public class TopicQuotaDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "appId")
    private String appId;

    @ApiModelProperty(value = "发送数据速率B/s")
    private Long produceQuota;

    @ApiModelProperty(value = "消费数据速率B/s")
    private Long consumeQuota;

    public String getAppId() {
    return appId;
  }

    public void setAppId(String appId) {
    this.appId = appId;
  }

    public Long getProduceQuota() {
    return produceQuota;
  }

    public void setProduceQuota(Long produceQuota) {
    this.produceQuota = produceQuota;
  }

    public Long getConsumeQuota() {
    return consumeQuota;
  }

    public void setConsumeQuota(Long consumeQuota) {
    this.consumeQuota = consumeQuota;
  }

    @Override
    public boolean paramLegal() {
        return !ValidateUtils.isNullOrLessThanZero(clusterId) && !ValidateUtils.isBlank(topicName) && !ValidateUtils.isBlank(appId);
    }
}
