package com.xiaojukeji.kafka.manager.service.biz.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.app.AppRelateTopicsVO;

import java.util.List;
import java.util.Set;


/**
 * Ha App管理
 */
public interface HaAppManager {
    Result<List<AppRelateTopicsVO>> appRelateTopics(Boolean ha, Long clusterPhyId, List<String> filterTopicNameList);
    Result<List<AppRelateTopicsVO>> appAndClientRelateTopics(Long clusterPhyId, Set<String> filterTopicNameSet);

    boolean isContainAllRelateAppTopics(Long clusterPhyId, List<String> filterTopicNameList);
}
