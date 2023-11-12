package com.xiaojukeji.know.streaming.km.core.service.acl;

import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.core.service.meta.MetaDataService;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.resource.ResourceType;

import java.util.List;

public interface KafkaAclService extends MetaDataService<AclBinding> {
    List<KafkaAclPO> getKafkaAclFromDB(Long clusterPhyId);

    Integer countKafkaAclFromDB(Long clusterPhyId);

    Integer countResTypeAndDistinctFromDB(Long clusterPhyId, ResourceType resourceType);

    Integer countKafkaUserAndDistinctFromDB(Long clusterPhyId);
    List<KafkaAclPO> getTopicAclFromDB(Long clusterPhyId, String topicName);
}
