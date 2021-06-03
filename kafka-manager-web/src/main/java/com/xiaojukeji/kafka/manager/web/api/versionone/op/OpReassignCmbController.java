package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.PaginationResult;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbTaskDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTaskVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTopicProcessVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassigncmbMerticsVO;
import com.xiaojukeji.kafka.manager.service.service.ReassignCmbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Topic迁移相关接口
 */
@Api(tags = "OP-Cmb-Topic迁移相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpReassignCmbController {

  @Autowired
  private ReassignCmbService reassignCmbService;

  @ApiOperation(value = "迁移任务列表", notes = "")
  @RequestMapping(value = {"cmb/reassign-tasks/condition"}, method = RequestMethod.POST)
  @ResponseBody
  public PaginationResult<ReassignCmbVO> getReassignTasksByCondition(@RequestBody ReassignCmbDTO dto){
    return new PaginationResult<>(
        reassignCmbService.getReassignTasksByCondition(dto),
        dto.getPageNo(),dto.getPageSize(),reassignCmbService.getTotal());
  }

  @ApiOperation(value = "创建迁移任务", notes = "")
  @RequestMapping(value = {"cmb/reassign-tasks"}, method = RequestMethod.POST)
  @ResponseBody
  public Result createReassignTask(@RequestBody ReassignCmbTaskDTO dto) {
    return reassignCmbService.createReassignTask(dto);
  }

  @ApiOperation(value = "Topic指标信息", notes = "")
  @RequestMapping(value = {"cmb/reassign-tasks/metrics"}, method = RequestMethod.GET)
  @ResponseBody
  public Result<ReassigncmbMerticsVO> getReassignTopicMetrics(
      @RequestParam Long clusterId,
      @RequestParam String topicName) {
    return reassignCmbService.getReassignTopicMetrics(clusterId, topicName);
  }

  @ApiOperation(value = "编辑迁移任务", notes = "")
  @RequestMapping(value = {"cmb/reassign-tasks"}, method = RequestMethod.PUT)
  @ResponseBody
  public Result modifyReassignTask(@RequestBody ReassignCmbTaskDTO dto) {
    return reassignCmbService.modifyReassignTask(dto);
  }

  @ApiOperation(value = "执行|取消迁移任务", notes = "")
  @RequestMapping(value = {"cmb/operate/reassign-tasks"}, method = RequestMethod.POST)
  @ResponseBody
  public Result executeReassignTask(@RequestBody ReassignCmbExecDTO dto) {
    return reassignCmbService.executeReassignTask(dto);
  }

  @ApiOperation(value = "查询迁移任务详情", notes = "")
  @RequestMapping(value = {"cmb/reassign-tasks"}, method = RequestMethod.GET)
  @ResponseBody
  public Result<ReassignCmbTaskVO> getReassignTasksByTaskId(@RequestParam Long taskId) {
    return reassignCmbService.getReassignTasksByTaskId(taskId);
  }

  @ApiOperation(value = "查询迁移topic进度", notes = "")
  @RequestMapping(value = {"cmb/reassign-tasks/process"}, method = RequestMethod.GET)
  @ResponseBody
  public Result<List<ReassignCmbTopicProcessVO>> getReassignTopicProcess(
      @RequestParam Long clusterId,
      @RequestParam String topicName) {
    return reassignCmbService.getReassignTopicProcess(clusterId, topicName);
  }

}
