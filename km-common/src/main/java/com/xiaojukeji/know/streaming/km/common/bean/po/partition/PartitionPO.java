package com.xiaojukeji.know.streaming.km.common.bean.po.partition;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

import java.util.Objects;

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

        PartitionPO po = (PartitionPO) o;
        return Objects.equals(clusterPhyId, po.clusterPhyId)
                && Objects.equals(topicName, po.topicName)
                && Objects.equals(partitionId, po.partitionId)
                && Objects.equals(leaderBrokerId, po.leaderBrokerId)
                && Objects.equals(inSyncReplicas, po.inSyncReplicas)
                && Objects.equals(assignReplicas, po.assignReplicas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clusterPhyId, topicName, partitionId, leaderBrokerId, inSyncReplicas, assignReplicas);
    }
}
