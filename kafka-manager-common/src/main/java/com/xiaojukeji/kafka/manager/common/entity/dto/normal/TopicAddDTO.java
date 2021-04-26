package com.xiaojukeji.kafka.manager.common.entity.dto.normal;


import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "创建topic")
public class TopicAddDTO extends ClusterTopicDTO {

  @ApiModelProperty(value = "appId")
  private String appId;

  @ApiModelProperty(value = "峰值流量")
  private Long peakBytesIn;

  @ApiModelProperty(value = "备注信息")
  private String description;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public Long getPeakBytesIn() {
    return peakBytesIn;
  }

  public void setPeakBytesIn(Long peakBytesIn) {
    this.peakBytesIn = peakBytesIn;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
