package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.task.*;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskStatus;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.KafkaFileEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterTaskActionDTO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import com.xiaojukeji.kafka.manager.kcm.ClusterTaskService;
import com.xiaojukeji.kafka.manager.kcm.KafkaFileService;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.web.converters.ClusterTaskModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/4/26
 */
@Api(tags = "OP-Cluster升级部署相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpClusterTaskController {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KafkaFileService kafkaFileService;

    @Autowired
    private ClusterTaskService clusterTaskService;

    @ApiOperation(value = "集群任务类型", notes = "")
    @RequestMapping(value = "cluster-tasks/enums", method = RequestMethod.GET)
    @ResponseBody
    public Result getClusterTaskEnums() {
        Map<String, Object> enumMap = new HashMap<>(1);
        enumMap.put(AbstractClusterTaskDTO.TASK_TYPE_PROPERTY_FIELD_NAME, JsonUtils.toJson(ClusterTaskTypeEnum.class));
        return new Result<>(enumMap);
    }

    @ApiOperation(value = "创建集群任务", notes = "")
    @RequestMapping(value = "cluster-tasks", method = RequestMethod.POST)
    @ResponseBody
    public Result createTask(@RequestBody AbstractClusterTaskDTO dto) {
        dto.setKafkaRoleBrokerHostMap(PhysicalClusterMetadataManager.getKafkaRoleBrokerHostMap(dto.getClusterId()));
        dto.setKafkaFileBaseUrl(kafkaFileService.getDownloadBaseUrl());
        return clusterTaskService.createTask(dto, SpringTool.getUserName());
    }

    @ApiOperation(value = "集群任务列表", notes = "")
    @RequestMapping(value = "cluster-tasks", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ClusterTaskVO>> getTaskList() {
        List<ClusterTaskDO> doList = clusterTaskService.listAll();
        if (ValidateUtils.isNull(doList)) {
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        }
        Map<Long, String> clusterNameMap = clusterService.listAll().stream().collect(
                Collectors.toMap(ClusterDO::getId, ClusterDO::getClusterName, (key1, key2) -> key2)
        );
        return new Result<>(ClusterTaskModelConverter.convert2ClusterTaskVOList(doList, clusterNameMap));
    }

    @ApiOperation(value = "触发集群任务", notes = "")
    @RequestMapping(value = "cluster-tasks", method = RequestMethod.PUT)
    @ResponseBody
    public Result executeTask(@RequestBody ClusterTaskActionDTO dto) {
        return Result.buildFrom(clusterTaskService.executeTask(dto.getTaskId(), dto.getAction(), dto.getHostname()));
    }

    @ApiOperation(value = "集群任务元信息")
    @RequestMapping(value = "cluster-tasks/{taskId}/metadata", method = RequestMethod.GET)
    @ResponseBody
    public Result<ClusterTaskMetadataVO> getTaskMetadata(@PathVariable Long taskId) {
        ClusterTaskDO clusterTaskDO = clusterTaskService.getById(taskId);
        if (ValidateUtils.isNull(clusterTaskDO)) {
            return Result.buildFrom(ResultStatus.RESOURCE_NOT_EXIST);
        }
        return new Result<>(ClusterTaskModelConverter.convert2ClusterTaskMetadataVO(
                clusterTaskDO,
                clusterService.getById(clusterTaskDO.getClusterId()),
                kafkaFileService.getFileByFileName(clusterTaskDO.getServerProperties())
        ));
    }

    @ApiOperation(value = "集群任务状态", notes = "整个任务的状态")
    @RequestMapping(value = "cluster-tasks/{taskId}/status", method = RequestMethod.GET)
    @ResponseBody
    public Result<ClusterTaskStatusVO> getTaskStatus(@PathVariable Long taskId) {
        Result<ClusterTaskStatus> dtoResult = clusterTaskService.getTaskStatus(taskId);
        if (!Constant.SUCCESS.equals(dtoResult.getCode())) {
            return new Result<>(dtoResult.getCode(), dtoResult.getMessage());
        }
        return new Result<>(ClusterTaskModelConverter.convert2ClusterTaskStatusVO(
                dtoResult.getData(),
                PhysicalClusterMetadataManager.getBrokerHostKafkaRoleMap(dtoResult.getData().getClusterId())
        ));
    }

    @ApiOperation(value = "集群任务日志", notes = "具体机器的日志")
    @RequestMapping(value = "cluster-tasks/{taskId}/log", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getTaskLog(@PathVariable Long taskId,
                                     @RequestParam("hostname") String hostname) {
        return clusterTaskService.getTaskLog(taskId, hostname);
    }

    @ApiOperation(value = "文件选择", notes = "")
    @RequestMapping(value = "cluster-tasks/kafka-files", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ClusterTaskKafkaFilesVO>> getKafkaFiles(
            @RequestParam(value = "clusterId", required = false) Long clusterId) {
        List<KafkaFileDO> kafkaFileDOList = kafkaFileService.getKafkaFiles();
        if (ValidateUtils.isEmptyList(kafkaFileDOList)) {
            return new Result<>();
        }
        List<ClusterTaskKafkaFilesVO> voList = new ArrayList<>();
        for (KafkaFileDO kafkaFileDO: kafkaFileDOList) {
            if (KafkaFileEnum.SERVER_CONFIG.getCode().equals(kafkaFileDO.getFileType())
                    && !kafkaFileDO.getClusterId().equals(clusterId)) {
                continue;
            }
            ClusterTaskKafkaFilesVO vo = new ClusterTaskKafkaFilesVO();
            vo.setFileName(kafkaFileDO.getFileName());
            vo.setFileMd5(kafkaFileDO.getFileMd5());
            vo.setFileType(kafkaFileDO.getFileType());
            voList.add(vo);
        }
        return new Result<>(voList);
    }
}