package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail;

import lombok.Data;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
public class ReassignJobDetailDataGroupByTopic extends AbstractReassignJobDetailData {
    /**
     * 分区ID列表
     */
    private List<Integer> partitionIdList;

    private List<ReassignJobDetailDataGroupByPartition> reassignPartitionDetailsList;
}
