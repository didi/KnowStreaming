package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.bizenum.JobLogBizTypEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaJobState;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.job.HaJobDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.job.JobLogVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.job.JobMulLogVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.job.HaJobStateVO;
import com.xiaojukeji.kafka.manager.common.utils.ConvertUtil;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.service.biz.job.HaASSwitchJobManager;
import com.xiaojukeji.kafka.manager.service.service.JobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zengqiao
 * @date 20/4/23
 */
@Api(tags = "OP-HA-主备切换Job相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpHaASSwitchJobController {
    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private HaASSwitchJobManager haASSwitchJobManager;

    @ApiOperation(value = "任务创建[ActiveStandbySwitch]")
    @PostMapping(value = "as-switch-jobs")
    @ResponseBody
    public Result<Long> createJob(@Validated @RequestBody ASSwitchJobDTO dto) {
        return haASSwitchJobManager.createJob(dto, SpringTool.getUserName());
    }

    @ApiOperation(value = "任务状态[ActiveStandbySwitch]", notes = "最近一个任务")
    @GetMapping(value = "as-switch-jobs/{jobId}/job-state")
    @ResponseBody
    public Result<HaJobStateVO> jobState(@PathVariable Long jobId) {
        Result<HaJobState> haResult = haASSwitchJobManager.jobState(jobId);
        if (haResult.failed()) {
            return Result.buildFromIgnoreData(haResult);
        }

        return Result.buildSuc(new HaJobStateVO(haResult.getData()));
    }

    @ApiOperation(value = "任务详情[ActiveStandbySwitch]", notes = "")
    @GetMapping(value = "as-switch-jobs/{jobId}/job-detail")
    @ResponseBody
    public Result<List<HaJobDetailVO>> jobDetail(@PathVariable Long jobId) {
        return haASSwitchJobManager.jobDetail(jobId);
    }

    @ApiOperation(value = "任务日志[ActiveStandbySwitch]", notes = "")
    @GetMapping(value = "as-switch-jobs/{jobId}/job-logs")
    @ResponseBody
    public Result<JobMulLogVO> jobLog(@PathVariable Long jobId, @RequestParam(required = false) Long startLogId) {
        List<JobLogDO> doList = jobLogService.listLogs(JobLogBizTypEnum.HA_SWITCH_JOB_LOG.getCode(), String.valueOf(jobId), startLogId);
        List<JobLogVO> voList = doList.isEmpty()? new ArrayList<>(): ConvertUtil.list2List(
                doList,
                JobLogVO.class
        );

        return Result.buildSuc(new JobMulLogVO(voList, startLogId));
    }

    @ApiOperation(value = "任务操作[ActiveStandbySwitch]", notes = "")
    @PutMapping(value = "as-switch-jobs/{jobId}/action")
    @ResponseBody
    public Result<Void> actionJob(@PathVariable Long jobId, @Validated @RequestBody ASSwitchJobActionDTO dto) {
        return haASSwitchJobManager.actionJob(jobId, dto);
    }
}
