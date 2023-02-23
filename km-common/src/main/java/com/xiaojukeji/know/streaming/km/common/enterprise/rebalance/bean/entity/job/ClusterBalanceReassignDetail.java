package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job.detail.ClusterBalanceDetailDataGroupByTopic;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceReassignDetail {
    /**
     * 限流值
     */
    private Long throttleUnitB;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 完成时间
     */
    private Date finishedTime;

    /**
     * 详细信息
     */
    private List<ClusterBalanceDetailDataGroupByTopic> reassignTopicDetailsList;
}
