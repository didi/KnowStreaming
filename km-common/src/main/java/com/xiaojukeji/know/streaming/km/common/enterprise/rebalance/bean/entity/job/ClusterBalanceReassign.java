package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.Data;

import java.util.Date;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceReassign {
    /**
     * jobID
     */
    private Long jobId;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 分区ID
     */
    private Integer partitionId;

    /**
     * 源BrokerId列表
     */
    private String originalBrokerIds;

    /**
     * 目标BrokerId列表
     */
    private String reassignBrokerIds;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务完成时间
     */
    private Date finishedTime;

    /**
     * 扩展数据
     */
    private String extendData;

    /**
     * 任务状态
     */
    private Integer status;
}
