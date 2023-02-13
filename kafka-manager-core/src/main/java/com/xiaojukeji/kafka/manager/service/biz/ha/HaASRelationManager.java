package com.xiaojukeji.kafka.manager.service.biz.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.HaClusterTopicVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.HaClusterTopicHaStatusVO;

import java.util.List;

public interface HaASRelationManager {
    /**
     * 获取集群主备信息
     */
    List<HaClusterTopicVO> getHATopics(Long firstClusterPhyId, Long secondClusterPhyId, boolean filterSystemTopics);

    /**
     * 获取集群Topic的主备状态信息
     */
    Result<List<HaClusterTopicHaStatusVO>> listHaStatusTopics(Long clusterPhyId, Boolean checkMetadata);


    /**
     * 获取获取集群topic高可用关系 0：备topic, 1:主topic, -1非高可用
     */
    Integer getRelation(Long clusterId, String topicName);

    /**
     * 获取获取集群topic高可用关系
     */
    HaASRelationDO getASRelation(Long clusterId, String topicName);

}
