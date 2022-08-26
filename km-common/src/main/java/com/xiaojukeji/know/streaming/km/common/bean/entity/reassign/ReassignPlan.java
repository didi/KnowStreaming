package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

/**
 * 迁移计划
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReassignPlan {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 迁移计划
     */
    private Map<TopicPartition, List<Integer>> reAssignPlanMap;

    /**
     * 当前已有分配情况
     */
    private Map<TopicPartition, List<Integer>> currentAssignMap;
}
