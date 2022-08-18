package com.xiaojukeji.know.streaming.km.common.bean.po.topic;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

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
}