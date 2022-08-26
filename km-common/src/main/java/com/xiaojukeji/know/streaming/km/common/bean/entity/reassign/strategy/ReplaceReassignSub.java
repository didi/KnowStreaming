package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy;

import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
public class ReplaceReassignSub {
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
     * 副本logSize
     */
    private Float replaceLogSize;

}
