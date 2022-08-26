package com.xiaojukeji.know.streaming.km.core.service.acl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.resource.ResourceType;

import java.util.List;

public interface KafkaAclService {
    Result<List<AclBinding>> getAclFromKafka(Long clusterPhyId);

    List<KafkaAclPO> getKafkaAclFromDB(Long clusterPhyId);

    Integer countKafkaAclFromDB(Long clusterPhyId);

    Integer countResTypeAndDistinctFromDB(Long clusterPhyId, ResourceType resourceType);

    Integer countKafkaUserAndDistinctFromDB(Long clusterPhyId);

    List<KafkaAclPO> getKafkaResTypeAclFromDB(Long clusterPhyId, Integer resType);

    List<KafkaAclPO> getTopicAclFromDB(Long clusterPhyId, String topicName);

    List<KafkaAclPO> getGroupAclFromDB(Long clusterPhyId, String groupName);
}
