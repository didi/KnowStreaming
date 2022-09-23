package com.xiaojukeji.know.streaming.km.core.service.group.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.group.GroupMemberDAO;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_SEARCH_GROUP;

@Service
public class GroupServiceImpl extends BaseVersionControlService implements GroupService {
    private static final ILog log = LogFactory.getLog(GroupServiceImpl.class);

    @Autowired
    private GroupMemberDAO groupMemberDAO;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_SEARCH_GROUP;
    }

    @Override
    public List<String> listGroupsFromKafka(Long clusterPhyId) throws NotExistException, AdminOperateException {
        AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

        try {
            ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups(
                    new ListConsumerGroupsOptions()
                            .timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            List<String> groupNameList = new ArrayList<>();
            for (ConsumerGroupListing consumerGroupListing: listConsumerGroupsResult.all().get()) {
                groupNameList.add(consumerGroupListing.groupId());
            }

            return groupNameList;
        } catch (Exception e) {
            log.error("method=getGroupsFromKafka||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    @Override
    public Map<TopicPartition, Long> getGroupOffset(Long clusterPhyId, String groupName) throws NotExistException, AdminOperateException {
        AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

        Map<TopicPartition, Long> offsetMap = new HashMap<>();
        try {
            ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(groupName);
            Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry: offsetAndMetadataMap.entrySet()) {
                offsetMap.put(entry.getKey(), entry.getValue().offset());
            }

            return offsetMap;
        } catch (Exception e) {
            log.error("method=getGroupOffset||clusterPhyId={}|groupName={}||errMsg=exception!", clusterPhyId, groupName, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    @Override
    public ConsumerGroupDescription getGroupDescription(Long clusterPhyId, String groupName) throws NotExistException, AdminOperateException {
        AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

        try {
            DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(
                    Arrays.asList(groupName),
                    new DescribeConsumerGroupsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS).includeAuthorizedOperations(false)
            );

            return describeConsumerGroupsResult.all().get().get(groupName);
        } catch(Exception e){
            log.error("method=getGroupDescription||clusterPhyId={}|groupName={}||errMsg=exception!", clusterPhyId, groupName, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    @Override
    public int replaceDBData(GroupMemberPO groupMemberPO) {
        return groupMemberDAO.replace(groupMemberPO);
    }

    @Override
    public void batchReplace(List<GroupMemberPO> newGroupMemberList) {
        if (newGroupMemberList == null || newGroupMemberList.isEmpty()) {
            return;
        }

        Long clusterPhyId = newGroupMemberList.get(0).getClusterPhyId();
        if (clusterPhyId == null) {
            return;
        }

        List<GroupMemberPO> dbGroupMemberList = listGroup(clusterPhyId);


        Map<String, GroupMemberPO> dbGroupMemberMap = dbGroupMemberList.stream().collect(Collectors.toMap(elem -> elem.getGroupName() + elem.getTopicName(), Function.identity()));
        for (GroupMemberPO groupMemberPO : newGroupMemberList) {
            GroupMemberPO po = dbGroupMemberMap.remove(groupMemberPO.getGroupName() + groupMemberPO.getTopicName());
            try {
                if (po != null) {
                    groupMemberPO.setId(po.getId());
                    groupMemberDAO.updateById(groupMemberPO);
                } else {
                    groupMemberDAO.insert(groupMemberPO);
                }
            } catch (Exception e) {
                log.error("method=batchReplace||clusterPhyId={}||groupName={}||errMsg=exception", clusterPhyId, groupMemberPO.getGroupName(), e);
            }

        }

    }

    @Override
    public GroupStateEnum getGroupStateFromDB(Long clusterPhyId, String groupName) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupMemberPO::getGroupName, groupName);

        List<GroupMemberPO> poList = groupMemberDAO.selectList(lambdaQueryWrapper);
        if (poList == null || poList.isEmpty()) {
            return GroupStateEnum.UNKNOWN;
        }

        return GroupStateEnum.getByState(poList.get(0).getState());
    }

    @Override
    public List<GroupMemberPO> listGroupByTopic(Long clusterPhyId, String topicName) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupMemberPO::getTopicName, topicName);

        return groupMemberDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<GroupMemberPO> listGroup(Long clusterPhyId) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);

        return groupMemberDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public PaginationResult<GroupMemberPO> pagingGroupMembers(Long clusterPhyId,
                                                              String topicName,
                                                              String groupName,
                                                              String searchTopicKeyword,
                                                              String searchGroupKeyword,
                                                              PaginationBaseDTO dto) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(!ValidateUtils.isBlank(topicName), GroupMemberPO::getTopicName, topicName);
        lambdaQueryWrapper.eq(!ValidateUtils.isBlank(groupName), GroupMemberPO::getGroupName, groupName);
        lambdaQueryWrapper.like(!ValidateUtils.isBlank(searchTopicKeyword), GroupMemberPO::getTopicName, searchTopicKeyword);
        lambdaQueryWrapper.like(!ValidateUtils.isBlank(searchGroupKeyword), GroupMemberPO::getGroupName, searchGroupKeyword);
        lambdaQueryWrapper.orderByDesc(GroupMemberPO::getClusterPhyId, GroupMemberPO::getTopicName);

        IPage<GroupMemberPO> iPage = new Page<>();
        iPage.setCurrent(dto.getPageNo());
        iPage.setSize(dto.getPageSize());

        iPage = groupMemberDAO.selectPage(iPage, lambdaQueryWrapper);

        return PaginationResult.buildSuc(iPage.getRecords(), iPage);
    }

    @Override
    public int deleteByUpdateTimeBeforeInDB(Long clusterPhyId, Date beforeTime) {
        LambdaQueryWrapper<GroupMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        queryWrapper.le(GroupMemberPO::getUpdateTime, beforeTime);
        return groupMemberDAO.delete(queryWrapper);
    }

    @Override
    public List<String> getGroupsFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<GroupMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        List<GroupMemberPO> poList = groupMemberDAO.selectList(queryWrapper);
        if (poList == null) {
            poList = new ArrayList<>();
        }
        return new ArrayList<>(poList.stream().map(elem -> elem.getGroupName()).collect(Collectors.toSet()));
    }

    @Override
    public GroupMemberPO getGroupFromDB(Long clusterPhyId, String groupName, String topicName) {
        LambdaQueryWrapper<GroupMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        queryWrapper.eq(GroupMemberPO::getTopicName, topicName);
        queryWrapper.eq(GroupMemberPO::getGroupName, groupName);

        return groupMemberDAO.selectOne(queryWrapper);
    }

    @Override
    public Integer calGroupCount(Long clusterPhyId) {
        LambdaQueryWrapper<GroupMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        List<GroupMemberPO> poList = groupMemberDAO.selectList(queryWrapper);
        if (poList == null) {
            poList = new ArrayList<>();
        }

        return poList.stream().map(elem -> elem.getGroupName()).collect(Collectors.toSet()).size();
    }

    @Override
    public Integer calGroupStatCount(Long clusterPhyId, GroupStateEnum stateEnum) {
        LambdaQueryWrapper<GroupMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        queryWrapper.eq(GroupMemberPO::getState, stateEnum.getState());

        List<GroupMemberPO> poList = groupMemberDAO.selectList(queryWrapper);
        if (poList == null) {
            poList = new ArrayList<>();
        }

        return poList.stream().map(elem -> elem.getGroupName()).collect(Collectors.toSet()).size();
    }

    @Override
    public Result<Void> resetGroupOffsets(Long clusterPhyId,
                                          String groupName,
                                          Map<TopicPartition, Long> resetOffsetMap,
                                          String operator) throws NotExistException, AdminOperateException {
        AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

        try {
            Map<TopicPartition, OffsetAndMetadata> offsets = resetOffsetMap.entrySet().stream().collect(Collectors.toMap(
                    elem -> elem.getKey(),
                    elem -> new OffsetAndMetadata(elem.getValue()),
                    (key1 , key2) -> key2
            ));

            AlterConsumerGroupOffsetsResult alterConsumerGroupOffsetsResult = adminClient.alterConsumerGroupOffsets(
                    groupName,
                    offsets,
                    new AlterConsumerGroupOffsetsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            alterConsumerGroupOffsetsResult.all().get();
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.EDIT.getDesc(),
                    ModuleEnum.KAFKA_GROUP.getDesc(),
                    String.format("clusterPhyId:%d groupName:%s", clusterPhyId, groupName),
                    ConvertUtil.obj2Json(resetOffsetMap));
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

            return Result.buildSuc();
        } catch(Exception e){
            log.error("method=resetGroupOffsets||clusterPhyId={}|groupName={}||errMsg=exception!", clusterPhyId, groupName, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }


    /**************************************************** private method ****************************************************/


}
