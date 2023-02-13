package com.xiaojukeji.kafka.manager.service.biz.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.app.AppRelateTopicsVO;

import java.util.List;


/**
 * Ha App管理
 */
public interface HaAppManager {
    Result<List<AppRelateTopicsVO>> appRelateTopics(Long clusterPhyId, List<String> filterTopicNameList);

    boolean isContainAllRelateAppTopics(Long clusterPhyId, List<String> filterTopicNameList);
}
