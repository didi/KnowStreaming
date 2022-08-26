package com.xiaojukeji.know.streaming.km.common.bean.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "kafka_user")
public class KafkaUserPO extends BasePO {
    /**
     * 集群Id
     */
    private Long clusterPhyId;

    /**
     * KafkaUser
     */
    private String name;

    /**
     * 密钥
     */
    private String token;

    public KafkaUserPO() {
    }

    public KafkaUserPO(Long clusterPhyId, String name, String token) {
        this.clusterPhyId = clusterPhyId;
        this.name = name;
        this.token = token;
    }
}
