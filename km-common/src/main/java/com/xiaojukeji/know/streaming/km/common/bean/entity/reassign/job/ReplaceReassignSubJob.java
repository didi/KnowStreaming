package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job;

import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
public class ReplaceReassignSubJob {
    /**
     * 物流集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 分区ID
     */
    private Integer partitionId;

    /**
     * 源Broker列表
     */
    private List<Integer> originalBrokerIdList;

    /**
     * 目标Broker列表
     */
    private List<Integer> reassignBrokerIdList;

    /**
     * 原本保存时间
     */
    private Long originalRetentionTimeUnitMs;

    /**
     * 迁移时保存时间
     */
    private Long reassignRetentionTimeUnitMs;

    /**
     * 当前副本数
     */
    private Integer originReplicaNum;

    /**
     * 新的副本数
     */
    private Integer reassignReplicaNum;
}
