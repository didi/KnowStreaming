package com.xiaojukeji.know.streaming.km.core.service.acl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.acl.ACLAtomParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import org.apache.kafka.common.resource.ResourceType;

import java.util.Date;
import java.util.List;

public interface OpKafkaAclService {
    /**
     * 创建ACL
     */
    Result<Void> createKafkaAcl(ACLAtomParam aclAtomParam, String operator);

    /**
     * 删除ACL
     */
    Result<Void> deleteKafkaAcl(ACLAtomParam aclAtomParam, String operator);

    /**
     * 删除ACL
     */
    Result<Void> deleteKafkaAclByResName(ResourceType resourceType, String resourceName, String operator);

    Result<Void> insertAndIgnoreDuplicate(KafkaAclPO kafkaAclPO);

    void batchUpdateAcls(Long clusterPhyId, List<KafkaAclPO> poList);

    int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime);
}
