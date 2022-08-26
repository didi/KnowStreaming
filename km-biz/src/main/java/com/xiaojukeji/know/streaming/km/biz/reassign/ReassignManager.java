package com.xiaojukeji.know.streaming.km.biz.reassign;

import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.change.CreateChangeReplicasPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.move.CreateMoveReplicaPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.ReassignTopicOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan.ReassignPlanVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.ReassignTopicOverviewVO;

import java.util.List;

public interface ReassignManager {
    /**
     * 创建迁移计划Json
     * @param dtoList
     * @return
     */
    Result<ReassignPlanVO> createReassignmentPlanJson(List<CreateMoveReplicaPlanDTO> dtoList);

    /**
     * 创建副本变更Json
     * @param dtoList
     * @return
     */
    Result<ReassignPlanVO> createReplicaChangePlanJson(List<CreateChangeReplicasPlanDTO> dtoList);

    /**
     * 迁移Topic的信息
     * @param dto
     * @return
     */
    Result<List<ReassignTopicOverviewVO>> getReassignmentTopicsOverview(ReassignTopicOverviewDTO dto);
}
