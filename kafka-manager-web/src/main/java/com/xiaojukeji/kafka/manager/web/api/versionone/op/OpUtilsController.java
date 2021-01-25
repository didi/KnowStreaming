package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.bizenum.RebalanceDimensionEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.RebalanceDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.TopicOperationResult;
import com.xiaojukeji.kafka.manager.service.utils.TopicCommands;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 运维工具类
 * @author zengqiao
 * @date 20/4/2
 */
@Api(tags = "OP-Utils相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpUtilsController {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TopicManagerService topicManagerService;

    @ApiOperation(value = "创建Topic")
    @RequestMapping(value = {"utils/topics"}, method = RequestMethod.POST)
    @ResponseBody
    public Result createCommonTopic(@RequestBody TopicCreationDTO dto) {
        Result<ClusterDO> rc = checkParamAndGetClusterDO(dto);
        if (rc.getCode() != ResultStatus.SUCCESS.getCode()) {
            return rc;
        }

        Properties properties = dto.getProperties();
        if (ValidateUtils.isNull(properties)) {
            properties = new Properties();
        }
        properties.put(KafkaConstant.RETENTION_MS_KEY, String.valueOf(dto.getRetentionTime()));

        ResultStatus rs = adminService.createTopic(
                rc.getData(),
                TopicDO.buildFrom(dto),
                dto.getPartitionNum(),
                dto.getReplicaNum(),
                dto.getRegionId(),
                dto.getBrokerIdList(),
                properties,
                SpringTool.getUserName(),
                SpringTool.getUserName()
        );
        return Result.buildFrom(rs);
    }

    @ApiOperation(value = "Topic扩分区", notes = "")
    @RequestMapping(value = {"utils/expand-partitions"}, method = RequestMethod.PUT)
    @ResponseBody
    public Result<List<TopicOperationResult>> expandTopics(@RequestBody List<TopicExpansionDTO> dtoList) {
        if (ValidateUtils.isNull(dtoList) || dtoList.size() > Constant.MAX_TOPIC_OPERATION_SIZE_PER_REQUEST) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        List<TopicOperationResult> resultList = new ArrayList<>();
        for (TopicExpansionDTO dto: dtoList) {
            Result<ClusterDO> rc = checkParamAndGetClusterDO(dto);
            if (!Constant.SUCCESS.equals(rc.getCode())) {
                resultList.add(TopicOperationResult.buildFrom(dto.getClusterId(), dto.getTopicName(), rc));
                continue;
            }

            // 参数检查合法, 开始对Topic进行扩分区
            ResultStatus statusEnum = adminService.expandPartitions(
                    rc.getData(),
                    dto.getTopicName(),
                    dto.getPartitionNum(),
                    dto.getRegionId(),
                    dto.getBrokerIdList(),
                    SpringTool.getUserName()
            );
            resultList.add(TopicOperationResult.buildFrom(dto.getClusterId(), dto.getTopicName(), statusEnum));
        }

        for (TopicOperationResult operationResult: resultList) {
            if (!Constant.SUCCESS.equals(operationResult.getCode())) {
                return Result.buildFrom(ResultStatus.OPERATION_FAILED, resultList);
            }
        }
        return new Result<>(resultList);
    }

    @ApiOperation(value = "Topic删除", notes = "单次不允许超过10个Topic")
    @RequestMapping(value = {"utils/topics"}, method = RequestMethod.DELETE)
    @ResponseBody
    public Result<List<TopicOperationResult>> deleteTopics(@RequestBody List<TopicDeletionDTO> dtoList) {
        if (ValidateUtils.isNull(dtoList) || dtoList.size() > Constant.MAX_TOPIC_OPERATION_SIZE_PER_REQUEST) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        String operator = SpringTool.getUserName();

        List<TopicOperationResult> resultList = new ArrayList<>();
        for (TopicDeletionDTO dto: dtoList) {
            Result<ClusterDO> rc = checkParamAndGetClusterDO(dto);
            if (rc.getCode() != ResultStatus.SUCCESS.getCode()) {
                resultList.add(TopicOperationResult.buildFrom(dto.getClusterId(), dto.getTopicName(), rc));
                continue;
            }

            // 参数检查合法, 开始删除Topic
            ResultStatus statusEnum = adminService.deleteTopic(rc.getData(), dto.getTopicName(), operator);
            resultList.add(TopicOperationResult.buildFrom(dto.getClusterId(), dto.getTopicName(), statusEnum));
        }

        for (TopicOperationResult operationResult: resultList) {
            if (!Constant.SUCCESS.equals(operationResult.getCode())) {
                return Result.buildFrom(ResultStatus.OPERATION_FAILED, resultList);
            }
        }
        return new Result<>(resultList);
    }

    @ApiOperation(value = "修改Topic", notes = "")
    @RequestMapping(value = {"utils/topics"}, method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyTopic(@RequestBody TopicModificationDTO dto) {
        Result<ClusterDO> rc = checkParamAndGetClusterDO(dto);
        if (rc.getCode() != ResultStatus.SUCCESS.getCode()) {
            return rc;
        }

        ClusterDO clusterDO = rc.getData();

        // 获取属性
        Properties properties = dto.getProperties();
        if (ValidateUtils.isNull(properties)) {
            properties = new Properties();
        }
        properties.put(KafkaConstant.RETENTION_MS_KEY, String.valueOf(dto.getRetentionTime()));

        // 操作修改
        String operator = SpringTool.getUserName();
        ResultStatus rs = TopicCommands.modifyTopicConfig(clusterDO, dto.getTopicName(), properties);
        if (!ResultStatus.SUCCESS.equals(rs)) {
            return Result.buildFrom(rs);
        }
        topicManagerService.modifyTopicByOp(dto.getClusterId(), dto.getTopicName(), dto.getAppId(), dto.getDescription(), operator);
        return new Result();
    }

    @ApiOperation(value = "优先副本选举状态")
    @RequestMapping(value = "utils/rebalance-status", method = RequestMethod.GET)
    @ResponseBody
    public Result preferredReplicaElectStatus(@RequestParam("clusterId") Long clusterId) {
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        TaskStatusEnum statusEnum = adminService.preferredReplicaElectionStatus(clusterDO);
        return new Result<>(JsonUtils.toJson(statusEnum));
    }

    @ApiOperation(value = "优先副本选举")
    @RequestMapping(value = "utils/rebalance", method = RequestMethod.POST)
    @ResponseBody
    public Result preferredReplicaElect(@RequestBody RebalanceDTO reqObj) {
        if (!reqObj.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        ResultStatus rs = null;
        if (RebalanceDimensionEnum.CLUSTER.getCode().equals(reqObj.getDimension())) {
            // 按照Cluster纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, SpringTool.getUserName());
        } else if (RebalanceDimensionEnum.BROKER.getCode().equals(reqObj.getDimension())) {
            // 按照Broker纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, reqObj.getBrokerId(), SpringTool.getUserName());
        } else if (RebalanceDimensionEnum.TOPIC.getCode().equals(reqObj.getDimension())) {
            // 按照Topic纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, reqObj.getTopicName(), SpringTool.getUserName());
        } else if (RebalanceDimensionEnum.PARTITION.getCode().equals(reqObj.getDimension())) {
            // 按照Partition纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, reqObj.getTopicName(), reqObj.getPartitionId(), SpringTool.getUserName());
        } else {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(rs);
    }

    private Result<ClusterDO> checkParamAndGetClusterDO(ClusterTopicDTO dto) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        ClusterDO clusterDO = clusterService.getById(dto.getClusterId());
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        return new Result<>(clusterDO);
    }
}

