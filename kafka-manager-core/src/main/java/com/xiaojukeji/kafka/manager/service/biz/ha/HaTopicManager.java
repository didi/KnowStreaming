package com.xiaojukeji.kafka.manager.service.biz.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.TopicOperationResult;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.HaSwitchTopic;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.KafkaUserAndClientDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.HaTopicRelationDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;

import java.util.List;


/**
 * Ha Topic管理
 */
public interface HaTopicManager {
    /**
     * 批量更改主备关系
     */
    Result<List<TopicOperationResult>> batchCreateHaTopic(HaTopicRelationDTO dto, String operator);

    /**
     * 批量更改主备关系
     */
    Result<List<TopicOperationResult>> batchRemoveHaTopic(HaTopicRelationDTO dto, String operator);

    /**
     * 可重试的执行主备切换
     * @param newActiveClusterPhyId 主集群
     * @param newStandbyClusterPhyId 备集群
     * @param switchTopicNameList 切换的Topic列表
     * @param focus 强制切换
     * @param firstTriggerExecute 第一次触发执行
     * @param switchLogTemplate 切换日志模版
     * @param operator 操作人
     * @return 操作结果
     */
    Result<HaSwitchTopic> switchHaWithCanRetry(Long newActiveClusterPhyId,
                                               Long newStandbyClusterPhyId,
                                               List<String> switchTopicNameList,
                                               List<KafkaUserAndClientDTO> kafkaUserAndClientIdList,
                                               boolean focus,
                                               boolean firstTriggerExecute,
                                               JobLogDO switchLogTemplate,
                                               String operator);
}
