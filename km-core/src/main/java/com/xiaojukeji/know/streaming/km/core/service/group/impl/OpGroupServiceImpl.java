package com.xiaojukeji.know.streaming.km.core.service.group.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupTopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.group.DeleteGroupTopicPartitionParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.group.OpGroupService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.group.GroupDAO;
import com.xiaojukeji.know.streaming.km.persistence.mysql.group.GroupMemberDAO;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_HANDLE_NOT_EXIST;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_GROUP;

/**
 * @author didi
 */
@Service
public class OpGroupServiceImpl extends BaseKafkaVersionControlService implements OpGroupService {
    private static final ILog LOGGER = LogFactory.getLog(OpGroupServiceImpl.class);

    private static final String DELETE_GROUP_OFFSET                     = "deleteGroupOffset";
    private static final String DELETE_GROUP_TOPIC_OFFSET               = "deleteGroupTopicOffset";
    private static final String DELETE_GROUP_TP_OFFSET                  = "deleteGroupTopicPartitionOffset";

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private GroupMemberDAO groupMemberDAO;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_GROUP;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(DELETE_GROUP_OFFSET,          V_2_0_0, V_MAX, "deleteGroupOffsetByClient",                this::deleteGroupOffsetByClient);
        registerVCHandler(DELETE_GROUP_TOPIC_OFFSET,    V_2_4_0, V_MAX, "deleteGroupTopicOffsetByClient",           this::deleteGroupTopicOffsetByClient);
        registerVCHandler(DELETE_GROUP_TP_OFFSET,       V_2_4_0, V_MAX, "deleteGroupTopicPartitionOffsetByClient",  this::deleteGroupTopicPartitionOffsetByClient);
    }

    @Override
    public Result<Void> deleteGroupOffset(DeleteGroupParam param, String operator) {
        // 日志记录
        LOGGER.info("method=deleteGroupOffset||param={}||operator={}||msg=delete group offset", ConvertUtil.obj2Json(param), operator);

        try {
            Result<Void> rv = (Result<Void>) doVCHandler(param.getClusterPhyId(), DELETE_GROUP_OFFSET, param);
            if (rv == null || rv.failed()) {
                return rv;
            }

            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.KAFKA_GROUP.getDesc(),
                    String.format("集群ID:[%d] Group名称:[%s]", param.getClusterPhyId(), param.getGroupName()),
                    String.format("删除Offset:[%s]", ConvertUtil.obj2Json(param))
            );
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

            // 清理Group数据
            this.deleteGroupInDB(param.getClusterPhyId(), param.getGroupName());
            this.deleteGroupMemberInDB(param.getClusterPhyId(), param.getGroupName());

            return rv;
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Void> deleteGroupTopicOffset(DeleteGroupTopicParam param, String operator) {
        // 日志记录
        LOGGER.info("method=deleteGroupTopicOffset||param={}||operator={}||msg=delete group topic offset", ConvertUtil.obj2Json(param), operator);

        try {
            Result<Void> rv = (Result<Void>) doVCHandler(param.getClusterPhyId(), DELETE_GROUP_TOPIC_OFFSET, param);
            if (rv == null || rv.failed()) {
                return rv;
            }

            // 清理数据库中的数据
            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.KAFKA_GROUP.getDesc(),
                    String.format("集群ID:[%d] Group名称:[%s] Topic名称:[%s]", param.getClusterPhyId(), param.getGroupName(), param.getTopicName()),
                    String.format("删除Offset:[%s]", ConvertUtil.obj2Json(param))
            );
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

            // 清理group + topic 数据
            this.deleteGroupMemberInDB(param.getClusterPhyId(), param.getGroupName(), param.getTopicName());

            return rv;
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Void> deleteGroupTopicPartitionOffset(DeleteGroupTopicPartitionParam param, String operator) {
        // 日志记录
        LOGGER.info("method=deleteGroupTopicPartitionOffset||param={}||operator={}||msg=delete group topic partition offset", ConvertUtil.obj2Json(param), operator);

        try {
            Result<Void> rv = (Result<Void>) doVCHandler(param.getClusterPhyId(), DELETE_GROUP_TP_OFFSET, param);
            if (rv == null || rv.failed()) {
                return rv;
            }

            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.KAFKA_GROUP.getDesc(),
                    String.format("集群ID:[%d] Group名称:[%s] Topic名称:[%s] PartitionID:[%d]", param.getClusterPhyId(), param.getGroupName(), param.getTopicName(), param.getPartitionId()),
                    String.format("删除Offset:[%s]", ConvertUtil.obj2Json(param))
            );
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

            return rv;
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    /**************************************************** private method ****************************************************/

    private Result<Void> deleteGroupOffsetByClient(VersionItemParam itemParam) {
        DeleteGroupParam param = (DeleteGroupParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            DeleteConsumerGroupsResult deleteConsumerGroupsResult = adminClient.deleteConsumerGroups(
                    Collections.singletonList(param.getGroupName()),
                    new DeleteConsumerGroupsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            deleteConsumerGroupsResult.all().get();
        } catch (Exception e) {
            LOGGER.error(
                    "method=deleteGroupOffsetByClient||clusterPhyId={}||groupName={}||errMsg=delete group failed||msg=exception!",
                    param.getClusterPhyId(), param.getGroupName(), e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> deleteGroupTopicOffsetByClient(VersionItemParam itemParam) {
        DeleteGroupTopicParam param = (DeleteGroupTopicParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(
                            param.getTopicName()),
                    new DescribeTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            List<TopicPartition> tpList = describeTopicsResult
                    .all()
                    .get()
                    .get(param.getTopicName())
                    .partitions()
                    .stream()
                    .map(elem -> new TopicPartition(param.getTopicName(), elem.partition()))
                    .collect(Collectors.toList());

            DeleteConsumerGroupOffsetsResult deleteConsumerGroupOffsetsResult = adminClient.deleteConsumerGroupOffsets(
                    param.getGroupName(),
                    new HashSet<>(tpList),
                    new DeleteConsumerGroupOffsetsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            deleteConsumerGroupOffsetsResult.all().get();
        } catch (Exception e) {
            LOGGER.error(
                    "method=deleteGroupTopicOffsetByClient||clusterPhyId={}||groupName={}||topicName={}||errMsg=delete group failed||msg=exception!",
                    param.getClusterPhyId(), param.getGroupName(), param.getTopicName(), e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> deleteGroupTopicPartitionOffsetByClient(VersionItemParam itemParam) {
        DeleteGroupTopicPartitionParam param = (DeleteGroupTopicPartitionParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            DeleteConsumerGroupOffsetsResult deleteConsumerGroupOffsetsResult = adminClient.deleteConsumerGroupOffsets(
                    param.getGroupName(),
                    new HashSet<>(Arrays.asList(new TopicPartition(param.getTopicName(), param.getPartitionId()))),
                    new DeleteConsumerGroupOffsetsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            deleteConsumerGroupOffsetsResult.all().get();
        } catch (Exception e) {
            LOGGER.error(
                    "method=deleteGroupTopicPartitionOffsetByClient||clusterPhyId={}||groupName={}||topicName={}||partitionId={}||errMsg=delete group failed||msg=exception!",
                    param.getClusterPhyId(), param.getGroupName(), param.getTopicName(), param.getPartitionId(), e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private int deleteGroupInDB(Long clusterPhyId, String groupName) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupPO::getName, groupName);

        return groupDAO.delete(lambdaQueryWrapper);
    }

    private int deleteGroupMemberInDB(Long clusterPhyId, String groupName) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupMemberPO::getGroupName, groupName);

        return groupMemberDAO.delete(lambdaQueryWrapper);
    }

    private int deleteGroupMemberInDB(Long clusterPhyId, String groupName, String topicName) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupMemberPO::getGroupName, groupName);
        lambdaQueryWrapper.eq(GroupMemberPO::getTopicName, topicName);

        return groupMemberDAO.delete(lambdaQueryWrapper);
    }
}
