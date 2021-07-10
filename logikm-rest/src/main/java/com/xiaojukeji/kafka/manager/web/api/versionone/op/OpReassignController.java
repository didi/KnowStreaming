package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.reassign.ReassignStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecSubDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignTopicStatusVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.service.service.ReassignService;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignTaskVO;
import com.xiaojukeji.kafka.manager.web.converters.ReassignModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Topic迁移相关接口
 * @author zengqiao
 * @date 19/4/3
 */
@Api(tags = "OP-Topic迁移相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpReassignController {
    @Autowired
    private ReassignService reassignService;

    @ApiOperation(value = "创建迁移任务", notes = "")
    @RequestMapping(value = {"reassign-tasks"}, method = RequestMethod.POST)
    @ResponseBody
    public Result createReassignTopicTask(@RequestBody List<ReassignTopicDTO> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        for (ReassignTopicDTO dto: dtoList) {
            if (!dto.paramLegal()) {
                return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
            }
        }
        return Result.buildFrom(reassignService.createTask(dtoList, SpringTool.getUserName()));
    }

    @ApiOperation(value = "操作迁移任务[启动|修改|取消]", notes = "")
    @RequestMapping(value = {"reassign-tasks"}, method = RequestMethod.PUT)
    @ResponseBody
    public Result executeReassignTopicTask(@RequestBody ReassignExecDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        TopicReassignActionEnum action = TopicReassignActionEnum.getByAction(dto.getAction());
        if (ValidateUtils.isNull(action)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(reassignService.modifyTask(dto, action));
    }

    @ApiOperation(value = "操作迁移子任务[修改|取消]", notes = "")
    @RequestMapping(value = {"reassign-tasks/sub-tasks"}, method = RequestMethod.PUT)
    @ResponseBody
    public Result executeReassignTopicTask(@RequestBody ReassignExecSubDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(reassignService.modifySubTask(dto));
    }

    @ApiOperation(value = "迁移任务列表", notes = "")
    @RequestMapping(value = {"reassign-tasks"}, method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ReassignTaskVO>> getReassignTasks() {
        return new Result<>(ReassignModelConverter.convert2ReassignTaskVOList(
                reassignService.getReassignTaskList()
        ));
    }

    @ApiOperation(value = "迁移任务信息", notes = "")
    @RequestMapping(value = {"reassign-tasks/{taskId}/detail"}, method = RequestMethod.GET)
    @ResponseBody
    public Result<ReassignTaskVO> getReassignTaskDetail(@PathVariable Long taskId) {
        return new Result<>(ReassignModelConverter.convert2ReassignTaskVO(
                taskId, reassignService.getTask(taskId)
        ));
    }

    @ApiOperation(value = "迁移任务状态", notes = "")
    @RequestMapping(value = {"reassign-tasks/{taskId}/status"}, method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ReassignTopicStatusVO>> getReassignTaskStatus(@PathVariable Long taskId) {
        Result<List<ReassignStatus>> statusResult = reassignService.getReassignStatus(taskId);
        if (!Constant.SUCCESS.equals(statusResult.getCode())) {
            return new Result<>(statusResult.getCode(), statusResult.getMessage());
        }
        return new Result<>(ReassignModelConverter.convert2ReassignTopicStatusVOList(statusResult.getData()));
    }
}
