package com.xiaojukeji.know.streaming.km.core.service.ha;

import com.xiaojukeji.know.streaming.km.common.bean.entity.ha.HaActiveStandbyRelation;
import com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum;

import java.util.List;

public interface HaActiveStandbyRelationService {
    /**
     * 新增或者变更，支持幂等
     */
    void batchReplaceTopicHA(Long activeClusterPhyId, Long standbyClusterPhyId, List<String> topicNameList);

    /**
     * 删除
     */
    void batchDeleteTopicHA(Long activeClusterPhyId, Long standbyClusterPhyId, List<String> topicNameList);

    /**
     * 按照集群ID查询
     */
    List<HaActiveStandbyRelation> listByClusterAndType(Long firstClusterId, HaResTypeEnum haResTypeEnum);

    List<HaActiveStandbyRelation> listAllTopicHa();
}
