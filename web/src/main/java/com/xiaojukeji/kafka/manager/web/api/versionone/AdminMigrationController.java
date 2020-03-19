package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.web.model.MigrationModel;
import com.xiaojukeji.kafka.manager.web.vo.MigrationDetailVO;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.ReassignmentStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.MigrationService;
import com.xiaojukeji.kafka.manager.web.converters.AdminMigrationConverter;
import com.xiaojukeji.kafka.manager.web.model.MigrationCreateModel;
import com.xiaojukeji.kafka.manager.web.vo.MigrationTaskVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 迁移相关接口
 * @author zengqiao_cn@163.com,huangyiminghappy@163.com
 * @date 19/4/3
 */
@Api(value = "AdminMigrationController", description = "AdminMigration相关接口")
@Controller
@RequestMapping("api/v1/admin/")
public class AdminMigrationController {
    private static final Logger logger = LoggerFactory.getLogger(AdminMigrationController.class);

    @Autowired
    private MigrationService migrationService;

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "创建迁移任务", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = MigrationDetailVO.class)
    @RequestMapping(value = {"migration/tasks"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<MigrationDetailVO> createMigrateTask(@RequestBody MigrationCreateModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        Result<MigrationTaskDO> result = migrationService.createMigrationTask(
                reqObj.getClusterId(),
                reqObj.getTopicName(),
                reqObj.getPartitionIdList(),
                reqObj.getThrottle(),
                reqObj.getBrokerIdList(),
                reqObj.getDescription()
        );
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return new Result<>(result.getCode(), result.getMessage());
        }
        return new Result<>(AdminMigrationConverter.convert2MigrationDetailVO(result.getData(), null));
    }

    @ApiOperation(value = "操作迁移任务[触发执行|修改限流|取消]", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = {"migration/tasks"}, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result executeMigrateTask(@RequestBody MigrationModel reqObj) {
        if (reqObj == null || reqObj.getTaskId() == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        if ("start".equals(reqObj.getAction())) {
            return migrationService.executeMigrationTask(reqObj.getTaskId());
        } else if ("modify".equals(reqObj.getAction())) {
            return migrationService.modifyMigrationTask(reqObj.getTaskId(), reqObj.getThrottle());
        } else if ("cancel".equals(reqObj.getAction())) {
            return migrationService.deleteMigrationTask(reqObj.getTaskId());
        }
        return new Result(StatusCode.PARAM_ERROR, "param illegal");
    }

    @ApiOperation(value = "查看迁移进度详情", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = MigrationDetailVO.class)
    @RequestMapping(value = {"migration/tasks/{taskId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<MigrationDetailVO> getMigrateTaskDetail(@PathVariable Long taskId) {
        MigrationTaskDO migrationTaskDO = migrationService.getMigrationTask(taskId);
        if (migrationTaskDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "taskId illegal");
        }
        ClusterDO cluster = clusterService.getById(migrationTaskDO.getClusterId());
        if (cluster == null) {
            return new Result<>(StatusCode.OPERATION_ERROR, "task illegal, cluster not exist");
        }
        Map<Integer, Integer> migrationStatusMap = ReassignmentStatusEnum.WAITING.getCode().equals(migrationTaskDO.getStatus())? new HashMap<>(): migrationService.getMigrationStatus(cluster, migrationTaskDO.getReassignmentJson());
        return new Result<>(AdminMigrationConverter.convert2MigrationDetailVO(migrationTaskDO, migrationStatusMap));
    }

    @ApiOperation(value = "迁移任务列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = MigrationTaskVO.class)
    @RequestMapping(value = {"migration/tasks"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<MigrationTaskVO>> getMigrationTask() {
        List<ClusterDO> clusterDOList = clusterService.listAll();
        return new Result<>(AdminMigrationConverter.convert2MigrationTaskVOList(migrationService.getMigrationTaskList(), clusterDOList));
    }
}
