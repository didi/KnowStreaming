package com.xiaojukeji.kafka.manager.openapi.common.dto;

import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "权限调整")
public class TopicAuthorityDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "appId")
    private String appId;

    @ApiModelProperty(value = "0:无权限, 1:读, 2:写, 3:读写, 4:可管理")
    private Integer access;

    public String getAppId() {
      return appId;
    }

    public void setAppId(String appId) {
      this.appId = appId;
    }

    public Integer getAccess() {
      return access;
    }

    public void setAccess(Integer access) {
      this.access = access;
    }

    @Override
    public boolean paramLegal() {
        return !ValidateUtils.isNullOrLessThanZero(clusterId)
                && !ValidateUtils.isBlank(topicName)
                && !ValidateUtils.isBlank(appId)
                && !ValidateUtils.isNullOrLessThanZero(access);
    }
}
