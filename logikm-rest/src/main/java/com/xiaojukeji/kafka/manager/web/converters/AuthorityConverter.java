package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.openapi.common.dto.TopicAuthorityDTO;

public class AuthorityConverter {
    public static AuthorityDO convert2AuthorityDO(TopicAuthorityDTO dto) {
      AuthorityDO authorityDO = new AuthorityDO();
      authorityDO.setAppId(dto.getAppId());
      authorityDO.setClusterId(dto.getClusterId());
      authorityDO.setTopicName(dto.getTopicName());
      authorityDO.setAccess(dto.getAccess());
      return authorityDO;
    }
}
