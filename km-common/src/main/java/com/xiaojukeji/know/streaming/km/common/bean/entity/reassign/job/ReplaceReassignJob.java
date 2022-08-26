package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ReplaceReassignJob extends BaseDTO {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * 限流值
     */
    private Long throttleUnitB;

    /**
     * 备注
     */
    private String description;

    /**
     * 子任务列表
     */
    private List<ReplaceReassignSubJob> subJobList;
}
