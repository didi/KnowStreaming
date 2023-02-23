package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job.detail;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.Data;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceDetailDataGroupByTopic extends AbstractClusterBalanceDetailData {
    /**
     * 分区ID列表
     */
    private List<Integer> partitionIdList;

    private List<ClusterBalanceDetailDataGroupByPartition> reassignPartitionDetailsList;
}
