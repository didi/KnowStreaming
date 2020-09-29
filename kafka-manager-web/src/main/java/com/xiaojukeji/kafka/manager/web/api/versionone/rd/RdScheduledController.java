package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.CustomScheduledTaskDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.CustomScheduledTaskVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/8/11
 */
@Api(tags = "RD-Schedule相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdScheduledController {
    @ApiOperation(value = "调度任务列表", notes = "")
    @RequestMapping(value = "scheduled-tasks", method = RequestMethod.GET)
    public Result<List<CustomScheduledTaskVO>> listAllScheduledTasks() {
        Map<String, AbstractScheduledTask> beanMap = SpringTool.getBeansOfType(AbstractScheduledTask.class);

        List<CustomScheduledTaskVO> voList = new ArrayList<>();
        for (Map.Entry<String, AbstractScheduledTask> entry: beanMap.entrySet()) {
            CustomScheduledTaskVO vo = new CustomScheduledTaskVO();
            vo.setName(entry.getKey());
            vo.setCron(entry.getValue().getCron());
            voList.add(vo);
        }
        return new Result<>(voList);
    }

    @ApiOperation(value = "触发执行调度任务", response = Result.class)
    @RequestMapping(value = "scheduled-tasks/{scheduledName}/run", method = RequestMethod.GET)
    public Result triggerScheduledTask(@PathVariable String scheduledName) {
        AbstractScheduledTask scheduledTask = SpringTool.getBean(scheduledName, AbstractScheduledTask.class);
        if(ValidateUtils.isNull(scheduledTask)){
            return Result.buildFrom(ResultStatus.RESOURCE_NOT_EXIST);
        }
        scheduledTask.scheduleAllTaskFunction();
        return Result.buildSuc();
    }

    @ApiOperation(value = "修改任务调度周期", response = Result.class)
    @RequestMapping(value = "scheduled-tasks", method = RequestMethod.PUT)
    public Result modifyScheduledTask(@RequestBody CustomScheduledTaskDTO dto) {
        AbstractScheduledTask scheduledTask = SpringTool.getBean(dto.getName(), AbstractScheduledTask.class);
        if(ValidateUtils.isNull(scheduledTask)){
            return Result.buildFrom(ResultStatus.RESOURCE_NOT_EXIST);
        }
        if (scheduledTask.modifyCron(dto.getName(), dto.getCron())) {
            return Result.buildSuc();
        }
        return Result.buildFrom(ResultStatus.OPERATION_FAILED);
    }
}