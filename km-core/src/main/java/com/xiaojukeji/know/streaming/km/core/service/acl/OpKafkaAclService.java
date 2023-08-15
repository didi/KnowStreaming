package com.xiaojukeji.know.streaming.km.core.service.acl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.acl.ACLAtomParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;

public interface OpKafkaAclService {
    /**
     * 创建ACL
     */
    Result<Void> createKafkaAcl(ACLAtomParam aclAtomParam, String operator);

    /**
     * 删除ACL
     */
    Result<Void> deleteKafkaAcl(ACLAtomParam aclAtomParam, String operator);

    Result<Void> insertAndIgnoreDuplicate(KafkaAclPO kafkaAclPO);
}
