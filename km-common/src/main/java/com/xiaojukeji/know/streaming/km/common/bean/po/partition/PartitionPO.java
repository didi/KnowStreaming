package com.xiaojukeji.know.streaming.km.common.bean.po.partition;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "partition")
public class PartitionPO extends BasePO {
    /**
     * 集群Id
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
     * LeaderBroker
     */
    private Integer leaderBrokerId;

    /**
     * ISR
     */
    private String inSyncReplicas;

    /**
     * AR
     */
    private String assignReplicas;
}
