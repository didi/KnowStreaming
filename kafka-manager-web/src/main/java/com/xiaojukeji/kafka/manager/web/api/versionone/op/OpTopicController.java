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
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaASRelationManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
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
    
    @Autowired
    private HaASRelationManager haASRelationManager;

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
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        Result rs = topicManagerService.modifyTopic(dto);
        if (rs.failed()){
            return rs;
        }

        //修改备topic
        HaASRelationDO relationDO = haASRelationManager.getASRelation(dto.getClusterId(), dto.getTopicName());
        if (relationDO != null && relationDO.getActiveClusterPhyId().equals(dto.getClusterId())){
            dto.setClusterId(relationDO.getStandbyClusterPhyId());
            dto.setTopicName(relationDO.getStandbyResName());
            rs = topicManagerService.modifyTopic(dto);
        }
        return rs;
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
            TopicOperationResult result;

            HaASRelationDO relationDO = haASRelationManager.getASRelation(dto.getClusterId(), dto.getTopicName());
            if (relationDO != null){
                //用户侧不允许操作备topic
                if (relationDO.getStandbyClusterPhyId().equals(dto.getClusterId())){
                    resultList.add(TopicOperationResult.buildFrom(dto.getClusterId(),
                            dto.getTopicName(),
                            ResultStatus.OPERATION_FORBIDDEN));
                    continue;
                }
                //备topic扩分区
                TopicExpansionDTO standbyDto = new TopicExpansionDTO();
                BeanUtils.copyProperties(dto, standbyDto);
                standbyDto.setClusterId(relationDO.getStandbyClusterPhyId());
                standbyDto.setTopicName(relationDO.getStandbyResName());
                standbyDto.setBrokerIdList(PhysicalClusterMetadataManager.getBrokerIdList(relationDO.getStandbyClusterPhyId()));
                standbyDto.setRegionId(null);
                result = topicManagerService.expandTopic(standbyDto);
                if (ResultStatus.SUCCESS.getCode() != result.getCode()){
                    resultList.add(result);
                    continue;
                }
            }
            resultList.add(topicManagerService.expandTopic(dto));
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

        HaASRelationDO relationDO = haASRelationManager.getASRelation(dto.getClusterId(), dto.getTopicName());
        if (relationDO != null) {
            return Result.buildFrom(ResultStatus.HA_TOPIC_DELETE_FORBIDDEN);
        }

        return new Result<>(clusterDO);
    }
}
