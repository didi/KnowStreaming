package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job;

import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail.ReassignJobDetailDataGroupByTopic;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
public class ReassignJobDetail {
    /**
     * 限流值
     */
    private Long throttleUnitB;

    /**
     * 完成时间
     */
    private Date finishedTime;

    /**
     * 备注
     */
    private String description;

    /**
     * 详细信息
     */
    private List<ReassignJobDetailDataGroupByTopic> reassignTopicDetailsList;
}
