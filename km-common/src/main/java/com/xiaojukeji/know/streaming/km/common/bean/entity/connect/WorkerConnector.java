package com.xiaojukeji.know.streaming.km.common.bean.entity.connect;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class WorkerConnector implements Serializable {
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

    private String workerMemberId;

    /**
     * 任务状态
     */
    private String state;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * worker信息
     */
    private String workerId;

    /**
     * 错误原因
     */
    private String trace;

    public WorkerConnector(Long kafkaClusterPhyId, Long connectClusterId, String connectorName, String workerMemberId, Integer taskId, String state, String workerId, String trace) {
        this.kafkaClusterPhyId = kafkaClusterPhyId;
        this.connectClusterId = connectClusterId;
        this.connectorName = connectorName;
        this.workerMemberId = workerMemberId;
        this.taskId = taskId;
        this.state = state;
        this.workerId = workerId;
        this.trace = trace;
    }
}
