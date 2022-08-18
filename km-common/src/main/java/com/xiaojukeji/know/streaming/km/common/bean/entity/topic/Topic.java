package com.xiaojukeji.know.streaming.km.common.bean.entity.topic;


import com.xiaojukeji.know.streaming.km.common.enums.topic.TopicTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic implements Serializable {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 副本数
     */
    private Integer replicaNum;

    /**
     * 分区数
     */
    private Integer partitionNum;

    /**
     * 修改节点的时间
     */
    private Long updateTime;

    /**
     * 创建节点的时间
     */
    private Long createTime;

    /**
     * 分区AR信息
     */
    private Map<Integer, List<Integer>> partitionMap;

    /**
     * 保存时间
     */
    private Long retentionMs;

    /**
     * Topic所在的broker, 由partitionMap获取得到
     */
    private Set<Integer> brokerIdSet;

    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.topic.TopicTypeEnum
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;

    public boolean isKafkaInternalTopic(){
        return TopicTypeEnum.KAFKA_INTERNAL.getCode().equals(type);
    }
}
