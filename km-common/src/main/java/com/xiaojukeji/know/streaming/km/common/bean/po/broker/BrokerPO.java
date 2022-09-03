package com.xiaojukeji.know.streaming.km.common.bean.po.broker;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "broker")
public class BrokerPO extends BasePO {
    /**
     * 集群Id
     */
    private Long clusterPhyId;

    /**
     * BrokerId
     */
    private Integer brokerId;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * Jmx端口
     */
    private Integer jmxPort;

    /**
     * 启动时间
     */
    private Long startTimestamp;

    /**
     * Broker状态
     */
    private Integer status;

    /**
     * 监听信息
     */
    private String endpointMap;
}
