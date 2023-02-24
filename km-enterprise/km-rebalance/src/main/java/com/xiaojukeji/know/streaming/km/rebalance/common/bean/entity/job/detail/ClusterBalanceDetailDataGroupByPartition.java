package com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.detail;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceDetailDataGroupByPartition extends AbstractClusterBalanceDetailData {
    /**
     * 分区ID
     */
    private Integer partitionId;
}
