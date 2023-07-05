package com.xiaojukeji.know.streaming.km.common.bean.po.connect;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_KC_TABLE_NAME_PREFIX + "worker")
public class ConnectWorkerPO extends BasePO {
    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * 集群ID
     */
    private Long connectClusterId;

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 主机
     */
    private String host;

    /**
     * Jmx端口
     */
    private Integer jmxPort;

    /**
     * URL
     */
    private String url;

    /**
     * leader的URL
     */
    private String leaderUrl;

    /**
     * 1：是leader，0：不是leader
     */
    private Integer leader;

    /**
     * worker地址
     */
    private String workerId;
}
