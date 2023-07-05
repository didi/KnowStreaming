package com.xiaojukeji.know.streaming.km.common.bean.entity.connect;

import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ConnectClusterMetadata implements Serializable {
    /**
     * Kafka集群名字
     */
    private Long kafkaClusterPhyId;

    /**
     * 集群使用的消费组
     */
    private String groupName;

    /**
     * 集群使用的消费组状态，也表示集群状态
     */
    private GroupStateEnum state;

    /**
     * worker中显示的leader url信息
     */
    private String memberLeaderUrl;

    public ConnectClusterMetadata(Long kafkaClusterPhyId, String groupName, GroupStateEnum state, String memberLeaderUrl) {
        this.kafkaClusterPhyId = kafkaClusterPhyId;
        this.groupName = groupName;
        this.state = state;
        this.memberLeaderUrl = memberLeaderUrl;
    }
}
