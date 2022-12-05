package com.xiaojukeji.know.streaming.km.common.bean.po.topic;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

import java.util.Objects;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "topic")
public class TopicPO extends BasePO {
    /**
     * 集群Id
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
     * BrokerId列表
     */
    private String brokerIds;

    /**
     * 分区分布
     */
    private String partitionMap;

    /**
     * 保存时间
     */
    private Long retentionMs;

    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.topic.TopicTypeEnum
     */
    private Integer type;

    /**
     * 备注信息
     */
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        TopicPO topicPO = (TopicPO) o;
        return Objects.equals(clusterPhyId, topicPO.clusterPhyId)
                && Objects.equals(topicName, topicPO.topicName)
                && Objects.equals(replicaNum, topicPO.replicaNum)
                && Objects.equals(partitionNum, topicPO.partitionNum)
                && Objects.equals(brokerIds, topicPO.brokerIds)
                && Objects.equals(partitionMap, topicPO.partitionMap)
                && Objects.equals(retentionMs, topicPO.retentionMs)
                && Objects.equals(type, topicPO.type)
                && Objects.equals(description, topicPO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clusterPhyId, topicName, replicaNum, partitionNum, brokerIds, partitionMap, retentionMs, type, description);
    }
}