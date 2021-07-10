package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.TopicOperationResult;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicCreationDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicDeletionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicExpansionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.TopicModificationDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.utils.TopicCommands;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Topic操作相关接口
 * @author zengqiao
 * @date 21/5/18
 */
@Api(tags = "OP-Topic操作相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpTopicController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicManagerService topicManagerService;

    @ApiOperation(value = "创建Topic")
    @RequestMapping(value = {"topics", "utils/topics"}, method = RequestMethod.POST)
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

    @ApiOperation(value = "Topic删除", notes = "单次不允许超过10个Topic")
    @RequestMapping(value = {"topics", "utils/topics"}, method = RequestMethod.DELETE)
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
    @RequestMapping(value = {"topics", "utils/topics"}, method = RequestMethod.PUT)
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

    @ApiOperation(value = "Topic扩分区", notes = "")
    @RequestMapping(value = {"topics/expand-partitions", "utils/expand-partitions"}, method = RequestMethod.PUT)
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
