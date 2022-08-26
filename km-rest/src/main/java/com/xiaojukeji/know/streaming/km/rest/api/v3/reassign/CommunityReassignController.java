package com.xiaojukeji.know.streaming.km.rest.api.v3.reassign;

import com.xiaojukeji.know.streaming.km.biz.reassign.ReassignManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.ReassignTopicOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.change.CreateChangeReplicasPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.move.CreateMoveReplicaPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.ReassignTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan.ReassignPlanVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 原生迁移，无高级特性
 * @author zengqiao
 * @date 22/05/06
 */
@Validated
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "迁移(Community)-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class CommunityReassignController {
    @Autowired
    private ReassignManager reassignManager;

    /**************************************************** 获取迁移计划 ****************************************************/

    @ApiOperation(value = "副本扩缩计划-生成", notes = "")
    @PostMapping(value ="reassignment/replicas-change-plan")
    @ResponseBody
    public Result<ReassignPlanVO> createReplicasChangePlanJson(@RequestBody @Valid List<CreateChangeReplicasPlanDTO> dtoList) {
        return reassignManager.createReplicaChangePlanJson(dtoList);
    }

    @ApiOperation(value = "副本迁移计划-生成", notes = "")
    @PostMapping(value ="reassignment/replicas-move-plan")
    @ResponseBody
    public Result<ReassignPlanVO> createReplicasMovePlanJson(@RequestBody @Valid List<CreateMoveReplicaPlanDTO> dtoList) {
        return reassignManager.createReassignmentPlanJson(dtoList);
    }

    /**************************************************** 查询信息 ****************************************************/

    @ApiOperation(value = "迁移任务-Topic概览信息", notes = "")
    @PostMapping(value ="reassignment/topics-overview")
    @ResponseBody
    public Result<List<ReassignTopicOverviewVO>> getReassignmentTopicsOverview(@RequestBody @Valid ReassignTopicOverviewDTO dto) {
        return reassignManager.getReassignmentTopicsOverview(dto);
    }
}
