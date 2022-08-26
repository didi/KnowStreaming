package com.xiaojukeji.know.streaming.km.rest.api.v3.job;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobPaginationDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.*;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import com.xiaojukeji.know.streaming.km.core.service.job.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Jobs-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KSJobController {

    @Autowired
    private JobService jobService;

    @ApiOperation(value = "任务类型", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/jobs/type-enums")
    @ResponseBody
    public Result<List<JobTypeVO>> types(@PathVariable Long clusterPhyId) {
        return Result.buildSuc(Arrays.stream(JobTypeEnum.values())
                .map(j -> new JobTypeVO(j.getType(), j.getMessage()))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "创建任务")
    @PostMapping(value = "clusters/{clusterPhyId}/jobs")
    @ResponseBody
    public Result<Void> createJob(@PathVariable Long clusterPhyId, @RequestBody JobDTO jobDTO) {
        return jobService.addTask(clusterPhyId, jobDTO, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "删除任务")
    @DeleteMapping(value = "clusters/{clusterPhyId}/jobs/{jobId}")
    @ResponseBody
    public Result<Void> deleteJobById(@PathVariable Long clusterPhyId, @PathVariable Long jobId) {
        return jobService.deleteById(clusterPhyId, jobId, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "修改任务")
    @PutMapping(value = "clusters/{clusterPhyId}/jobs")
    @ResponseBody
    public Result<Void> modifyJobById(@PathVariable Long clusterPhyId, @RequestBody JobDTO jobDTO) {
        return jobService.updateTask(clusterPhyId, jobDTO, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "集群任务列表")
    @PostMapping(value = "clusters/{clusterPhyId}/jobs-overview")
    @ResponseBody
    public PaginationResult<JobOverViewVO> pagingJobs(@PathVariable Long clusterPhyId, @RequestBody JobPaginationDTO dto) {
        return jobService.pagingJobs(clusterPhyId, dto);
    }

    @ApiOperation(value = "集群任务状态")
    @GetMapping(value = "clusters/{clusterPhyId}/jobs-state")
    @ResponseBody
    public Result<JobStateVO> state(@PathVariable Long clusterPhyId) {
        return jobService.state(clusterPhyId);
    }

    @ApiOperation(value = "任务-详细信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/jobs/{jobId}/modify-detail")
    @ResponseBody
    public Result<JobModifyDetailVO> getJobModifyDetail(@PathVariable Long clusterPhyId, @PathVariable Long jobId) {
        return jobService.getJobModifyDetail(clusterPhyId, jobId);
    }

    @ApiOperation(value = "任务-详细信息", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/jobs/{jobId}/detail")
    @ResponseBody
    public Result<JobDetailVO> getJobDetail(@PathVariable Long clusterPhyId, @PathVariable Long jobId) {
        return jobService.getJobDetail(clusterPhyId, jobId);
    }

    @ApiOperation(value = "子任务-partition-详细信息", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/jobs/{jobId}/{topic}/partition-detail")
    @ResponseBody
    public Result<List<SubJobPartitionDetailVO>> getSubJobPartitionDetail(@PathVariable Long clusterPhyId, @PathVariable Long jobId, @PathVariable String topic) {
        return jobService.getSubJobPartitionDetail(clusterPhyId, jobId, topic);
    }

    @ApiOperation(value = "任务-节点流量", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/jobs/{jobId}/node/traffic")
    @ResponseBody
    public Result<List<JobTrafficBrokerVO>> getJobNodeTraffic(@PathVariable Long clusterPhyId, @PathVariable Long jobId) {
        return jobService.getJobNodeTraffic(clusterPhyId, jobId);
    }

    @ApiOperation(value = "任务-更新节点流量，单位：byte", notes = "")
    @PostMapping(value = "clusters/{clusterPhyId}/jobs/{jobId}/traffic/{limit}")
    @ResponseBody
    public Result<Void> updateJobTrafficLimit(@PathVariable Long clusterPhyId, @PathVariable Long jobId, @PathVariable Long limit) {
        return jobService.updateJobTrafficLimit(clusterPhyId, jobId, limit, HttpRequestUtil.getOperator());
    }
}
