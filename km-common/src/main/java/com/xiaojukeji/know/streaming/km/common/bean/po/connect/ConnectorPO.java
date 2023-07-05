package com.xiaojukeji.know.streaming.km.common.bean.po.connect;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_KC_TABLE_NAME_PREFIX + "connector")
public class ConnectorPO extends BasePO {
    /**
     * Kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * connect集群ID
     */
    private Long connectClusterId;

    /**
     * connector名称
     */
    private String connectorName;

    /**
     * connector类名
     */
    private String connectorClassName;

    /**
     * connector类型
     */
    private String connectorType;

    /**
     * 访问过的Topic列表
     */
    private String topics;

    /**
     * task数
     */
    private Integer taskCount;

    /**
     * 状态
     */
    private String state;

    /**
     *   心跳检测connector
     */
    private String heartbeatConnectorName;

    /**
     *   进度确认connector
     */
    private String checkpointConnectorName;
}
