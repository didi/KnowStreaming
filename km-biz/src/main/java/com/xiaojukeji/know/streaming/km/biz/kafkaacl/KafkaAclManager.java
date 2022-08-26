package com.xiaojukeji.know.streaming.km.biz.kafkaacl;

import com.xiaojukeji.know.streaming.km.common.bean.dto.acl.AclAtomDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

/**
 *
 */
public interface KafkaAclManager {
    Result<Void> batchCreateKafkaAcl(List<AclAtomDTO> dtoList, String operator);
}
