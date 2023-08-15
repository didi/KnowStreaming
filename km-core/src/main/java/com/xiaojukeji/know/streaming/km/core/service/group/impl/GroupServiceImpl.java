package com.xiaojukeji.know.streaming.km.core.service.group.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.Group;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopicMember;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.GroupConverter;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.utils.kafka.KSPartialKafkaAdminClient;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.group.GroupDAO;
import com.xiaojukeji.know.streaming.km.persistence.mysql.group.GroupMemberDAO;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_SEARCH_GROUP;

@Service
public class GroupServiceImpl extends BaseKafkaVersionControlService implements GroupService {
    private static final ILog LOGGER = LogFactory.getLog(GroupServiceImpl.class);

    @Autowired
    private GroupDAO groupDAO;

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
    public List<String> listGroupsFromKafka(ClusterPhy clusterPhy) throws AdminOperateException {
        KSPartialKafkaAdminClient adminClient = null;
        try {
            Properties props = ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class);
            if (props == null) {
                props = new Properties();
            }

            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
            props.put(AdminClientConfig.CLIENT_ID_CONFIG, String.format("KSPartialAdminClient||clusterPhyId=%d||timestamp=%d", clusterPhy.getId(), System.currentTimeMillis()));

            adminClient = KSPartialKafkaAdminClient.create(props);
            KSListGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups(
                    new ListConsumerGroupsOptions()
                            .timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            List<String> groupNameList = new ArrayList<>();
            for (ConsumerGroupListing consumerGroupListing: listConsumerGroupsResult.all().get()) {
                groupNameList.add(consumerGroupListing.groupId());
            }

            return groupNameList;
        } catch (Exception e) {
            LOGGER.error("method=listGroupsFromKafka||clusterPhyId={}||errMsg=exception!", clusterPhy.getId(), e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        } finally {
            if (adminClient != null) {
                try {
                    adminClient.close(Duration.ofSeconds(10));
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public Group getGroupFromKafka(ClusterPhy clusterPhy, String groupName) throws NotExistException, AdminOperateException {
        // 获取消费组的详细信息
        KSGroupDescription groupDescription = this.getGroupDescriptionFromKafka(clusterPhy, groupName);
        if (groupDescription == null) {
            return null;
        }

        Group group = new Group(clusterPhy.getId(), groupName, groupDescription);

        // 获取消费组消费过哪些Topic
        Map<String, GroupTopicMember> memberMap = new HashMap<>();
        for (TopicPartition tp : this.getGroupOffsetFromKafka(clusterPhy.getId(), groupName).keySet()) {
            memberMap.putIfAbsent(tp.topic(), new GroupTopicMember(tp.topic(), 0));
        }

        // 记录成员信息
        for (KSMemberDescription memberDescription : groupDescription.members()) {
            if (group.getType() == GroupTypeEnum.CONNECT_CLUSTER) {
                continue;
            }
            Set<TopicPartition> partitionList = new HashSet<>();

            KSMemberConsumerAssignment assignment = (KSMemberConsumerAssignment) memberDescription.assignment();
            if (!ValidateUtils.isNull(assignment.topicPartitions())) {
                partitionList = assignment.topicPartitions();
            }

            Set<String> topicNameSet = partitionList.stream().map(elem -> elem.topic()).collect(Collectors.toSet());
            for (String topicName : topicNameSet) {
                memberMap.putIfAbsent(topicName, new GroupTopicMember(topicName, 0));

                GroupTopicMember member = memberMap.get(topicName);
                member.setMemberCount(member.getMemberCount() + 1);
            }
        }

        group.setTopicMembers(new ArrayList<>(memberMap.values()));

        return group;
    }

    @Override
    public Map<TopicPartition, Long> getGroupOffsetFromKafka(Long clusterPhyId, String groupName) throws NotExistException, AdminOperateException {
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
            LOGGER.error("method=getGroupOffset||clusterPhyId={}|groupName={}||errMsg=exception!", clusterPhyId, groupName, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    @Override
    public KSGroupDescription getGroupDescriptionFromKafka(ClusterPhy clusterPhy, String groupName) throws AdminOperateException {
        KSPartialKafkaAdminClient adminClient = null;
        try {
            Properties props = ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class);
            if (props == null) {
                props = new Properties();
            }

            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
            props.put(AdminClientConfig.CLIENT_ID_CONFIG, String.format("KSPartialAdminClient||clusterPhyId=%d||timestamp=%d", clusterPhy.getId(), System.currentTimeMillis()));

            adminClient = KSPartialKafkaAdminClient.create(props);

            KSDescribeGroupsResult describeGroupsResult = adminClient.describeConsumerGroups(
                    Arrays.asList(groupName),
                    new DescribeConsumerGroupsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS).includeAuthorizedOperations(false)
            );

            return describeGroupsResult.all().get().get(groupName);
        } catch(Exception e){
            LOGGER.error("method=getGroupDescription||clusterPhyId={}|groupName={}||errMsg=exception!", clusterPhy.getId(), groupName, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        } finally {
            if (adminClient != null) {
                try {
                    adminClient.close(Duration.ofSeconds(10));
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public void batchReplaceGroupsAndMembers(Long clusterPhyId, List<Group> newGroupList, Set<String> getFailedGroupSet) {
        // 更新Group信息
        this.batchReplaceGroups(clusterPhyId, newGroupList, getFailedGroupSet);

        // 更新Group-Topic信息
        this.batchReplaceGroupMembers(clusterPhyId, newGroupList, getFailedGroupSet);
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
    public List<GroupMemberPO> listGroupByCluster(Long clusterPhyId) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);

        return groupMemberDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<GroupMemberPO> listGroupByTopic(Long clusterPhyId, String topicName) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupMemberPO::getTopicName, topicName);

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
    public Group getGroupFromDB(Long clusterPhyId, String groupName) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupPO::getName, groupName);

        GroupPO groupPO = groupDAO.selectOne(lambdaQueryWrapper);
        return GroupConverter.convert2Group(groupPO);
    }

    @Override
    public List<Group> listClusterGroups(Long clusterPhyId) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);

        return groupDAO.selectList(lambdaQueryWrapper).stream().map(elem -> GroupConverter.convert2Group(elem)).collect(Collectors.toList());
    }

    @Override
    public List<String> getGroupsFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);

        List<GroupPO> poList = groupDAO.selectList(lambdaQueryWrapper);
        if (poList == null) {
            poList = new ArrayList<>();
        }

        return new ArrayList<>(poList.stream().map(elem -> elem.getName()).collect(Collectors.toSet()));
    }

    @Override
    public GroupMemberPO getGroupTopicFromDB(Long clusterPhyId, String groupName, String topicName) {
        LambdaQueryWrapper<GroupMemberPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);
        queryWrapper.eq(GroupMemberPO::getTopicName, topicName);
        queryWrapper.eq(GroupMemberPO::getGroupName, groupName);

        return groupMemberDAO.selectOne(queryWrapper);
    }

    @Override
    public Integer calGroupCount(Long clusterPhyId) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);

        return groupDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Integer calGroupStatCount(Long clusterPhyId, GroupStateEnum stateEnum) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(GroupPO::getState, stateEnum.getState());

        return groupDAO.selectCount(lambdaQueryWrapper);
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
            LOGGER.error("method=resetGroupOffsets||clusterPhyId={}|groupName={}||errMsg=exception!", clusterPhyId, groupName, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }


    /**************************************************** private method ****************************************************/


    private void batchReplaceGroupMembers(Long clusterPhyId, List<Group> newGroupList, Set<String> getFailedGroupSet) {
        // DB 中的数据
        Map<String, GroupMemberPO> dbPOMap = this.listClusterGroupsMemberPO(clusterPhyId)
                .stream()
                .collect(Collectors.toMap(elem -> elem.getGroupName() + elem.getTopicName(), Function.identity()));

        // 进行数据的更新
        for (Group group: newGroupList) {
            for (GroupTopicMember member : group.getTopicMembers()) {
                try {
                    GroupMemberPO newPO = new GroupMemberPO(clusterPhyId, member.getTopicName(), group.getName(), group.getState().getState(), member.getMemberCount(), new Date());

                    GroupMemberPO dbPO = dbPOMap.remove(newPO.getGroupName() + newPO.getTopicName());
                    if (dbPO == null) {
                        // 数据不存在则直接写入
                        groupMemberDAO.insert(newPO);
                    } else if (!dbPO.equal2GroupMemberPO(newPO)) {
                        // 数据发生了变化则进行更新
                        newPO.setId(dbPO.getId());
                        groupMemberDAO.updateById(newPO);
                    }
                } catch (Exception e) {
                    LOGGER.error(
                            "method=batchReplaceGroupMembers||clusterPhyId={}||groupName={}||topicName={}||errMsg=exception",
                            clusterPhyId, group.getName(), member.getTopicName(), e
                    );
                }
            }
        }

        // 删除剩余不存在的
        dbPOMap.values().forEach(elem -> {
            try {
                if (getFailedGroupSet.contains(elem.getGroupName())) {
                    // 该group信息获取失败，所以忽略对该数据的删除
                    return;
                }

                groupDAO.deleteById(elem.getId());
            } catch (Exception e) {
                LOGGER.error(
                        "method=batchReplaceGroupMembers||clusterPhyId={}||groupName={}||topicName={}||msg=delete expired group data in db failed||errMsg=exception",
                        clusterPhyId, elem.getGroupName(), elem.getTopicName(), e
                );
            }
        });
    }

    private void batchReplaceGroups(Long clusterPhyId, List<Group> newGroupList, Set<String> getFailedGroupSet) {
        // 获取 DB 中的数据
        Map<String, GroupPO> dbGroupMap = this.listClusterGroupsPO(clusterPhyId)
                .stream()
                .collect(Collectors.toMap(elem -> elem.getName(), Function.identity()));

        // 进行数据的更新
        for (Group newGroup: newGroupList) {
            try {
                GroupPO dbPO = dbGroupMap.remove(newGroup.getName());
                if (dbPO == null) {
                    // 一条新的数据，则直接insert
                    groupDAO.insert(GroupConverter.convert2GroupPO(newGroup));
                    continue;
                }

                GroupPO newPO = GroupConverter.convert2GroupPO(newGroup);
                if (!newPO.equal2GroupPO(dbPO)) {
                    // 如果不相等，则直接更新
                    newPO.setId(dbPO.getId());
                    groupDAO.updateById(newPO);
                }

                // 其他情况，则不需要进行任何操作
            } catch (Exception e) {
                LOGGER.error("method=batchReplaceGroups||clusterPhyId={}||groupName={}||errMsg=exception", clusterPhyId, newGroup.getName(), e);
            }
        }

        // 删除剩余不存在的
        dbGroupMap.values().forEach(elem -> {
            try {
                if (getFailedGroupSet.contains(elem.getName())) {
                    // 该group信息获取失败，所以忽略对该数据的删除
                    return;
                }

                groupDAO.deleteById(elem.getId());
            } catch (Exception e) {
                LOGGER.error("method=batchReplaceGroups||clusterPhyId={}||groupName={}||msg=delete expired group data in db failed||errMsg=exception", clusterPhyId, elem.getName(), e);
            }
        });
    }

    private List<GroupPO> listClusterGroupsPO(Long clusterPhyId) {
        LambdaQueryWrapper<GroupPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupPO::getClusterPhyId, clusterPhyId);
        return groupDAO.selectList(lambdaQueryWrapper);
    }

    private List<GroupMemberPO> listClusterGroupsMemberPO(Long clusterPhyId) {
        LambdaQueryWrapper<GroupMemberPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupMemberPO::getClusterPhyId, clusterPhyId);

        return groupMemberDAO.selectList(lambdaQueryWrapper);
    }
}
