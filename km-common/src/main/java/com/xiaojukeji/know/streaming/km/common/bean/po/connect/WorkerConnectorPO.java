package com.xiaojukeji.know.streaming.km.common.bean.po.connect;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;

@Data
@TableName(Constant.MYSQL_KC_TABLE_NAME_PREFIX + "worker_connector")
public class WorkerConnectorPO extends BasePO {
    /**
     * connect集群ID
     */
    private Long connectClusterId;

    /**
     * kafka集群ID
     */
    private Long kafkaClusterPhyId;

    /**
     * connector名称
     */
    private String connectorName;

    /**
     * worker成员ID
     */
    private String workerMemberId;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * task状态
     */
    private String state;

    /**
     * worker信息
     */
    private String workerId;
}
