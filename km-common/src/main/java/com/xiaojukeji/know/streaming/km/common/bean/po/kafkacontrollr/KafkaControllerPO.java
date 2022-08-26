package com.xiaojukeji.know.streaming.km.common.bean.po.kafkacontrollr;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "kafka_controller")
public class KafkaControllerPO extends BasePO {
    private Long clusterPhyId;

    private Integer brokerId;

    private String brokerHost;

    private String brokerRack;

    private Long timestamp;
}