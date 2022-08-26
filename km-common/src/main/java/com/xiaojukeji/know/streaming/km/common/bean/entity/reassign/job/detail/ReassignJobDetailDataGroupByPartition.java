package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail;

import lombok.Data;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
public class ReassignJobDetailDataGroupByPartition extends AbstractReassignJobDetailData {
    /**
     * 分区ID
     */
    private Integer partitionId;
}
