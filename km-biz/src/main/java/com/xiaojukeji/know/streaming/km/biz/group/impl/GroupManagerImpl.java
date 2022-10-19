package com.xiaojukeji.know.streaming.km.biz.group.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.group.GroupManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.group.GroupOffsetResetDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.partition.PartitionOffsetDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicPartitionKS;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicConsumedDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.OffsetTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationMetricsUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.GroupMetricVersionItems;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.GroupMetricESDAO;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GroupManagerImpl implements GroupManager {
    private static final ILog log = LogFactory.getLog(GroupManagerImpl.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private GroupMetricService groupMetricService;

    @Autowired
    private GroupMetricESDAO groupMetricESDAO;

    @Override
    public PaginationResult<GroupTopicOverviewVO> pagingGroupMembers(Long clusterPhyId,
                                                                     String topicName,
                                                                     String groupName,
                                                                     String searchTopicKeyword,
                                                                     String searchGroupKeyword,
                                                                     PaginationBaseDTO dto) {
        PaginationResult<GroupMemberPO> paginationResult = groupService.pagingGroupMembers(clusterPhyId, topicName, groupName, searchTopicKeyword, searchGroupKeyword, dto);
        if (paginationResult.failed()) {
            return PaginationResult.buildFailure(paginationResult, dto);
        }

        if (!paginationResult.hasData()) {
            return PaginationResult.buildSuc(new ArrayList<>(), paginationResult);
        }

        // 获取指标
        Result<List<GroupMetrics>> metricsListResult = groupMetricService.listLatestMetricsAggByGroupTopicFromES(
                clusterPhyId,
                paginationResult.getData().getBizData().stream().map(elem -> new GroupTopic(elem.getGroupName(), elem.getTopicName())).collect(Collectors.toList()),
                Arrays.asList(GroupMetricVersionItems.GROUP_METRIC_LAG),
                AggTypeEnum.MAX
        );
        if (metricsListResult.failed()) {
            // 如果查询失败，则输出错误信息，但是依旧进行已有数据的返回
            log.error("method=pagingGroupMembers||clusterPhyId={}||topicName={}||groupName={}||result={}||errMsg=search es failed", clusterPhyId, topicName, groupName, metricsListResult);
        }

        return PaginationResult.buildSuc(
                this.convert2GroupTopicOverviewVOList(paginationResult.getData().getBizData(), metricsListResult.getData()),
                paginationResult
        );
    }

    @Override
    public PaginationResult<GroupTopicConsumedDetailVO> pagingGroupTopicConsumedMetrics(Long clusterPhyId,
                                                                                        String topicName,
                                                                                        String groupName,
                                                                                        List<String> latestMetricNames,
                                                                                        PaginationSortDTO dto) throws NotExistException, AdminOperateException {
        // 获取消费组消费的TopicPartition列表
        Map<TopicPartition, Long> consumedOffsetMap = groupService.getGroupOffset(clusterPhyId, groupName);
        List<Integer> partitionList = consumedOffsetMap.keySet()
                .stream()
                .filter(elem -> elem.topic().equals(topicName))
                .map(elem -> elem.partition())
                .collect(Collectors.toList());
        Collections.sort(partitionList);

        // 获取消费组当前运行信息
        ConsumerGroupDescription groupDescription = groupService.getGroupDescription(clusterPhyId, groupName);

        // 转换存储格式
        Map<TopicPartition, MemberDescription> tpMemberMap = new HashMap<>();
        for (MemberDescription description: groupDescription.members()) {
            for (TopicPartition tp: description.assignment().topicPartitions()) {
                tpMemberMap.put(tp, description);
            }
        }

        // 获取指标
        PaginationResult<GroupMetrics> metricsResult = this.pagingGroupTopicPartitionMetrics(clusterPhyId, groupName, topicName, partitionList, latestMetricNames, dto);
        if (metricsResult.failed()) {
            return PaginationResult.buildFailure(metricsResult, dto);
        }

        // 数据组装
        List<GroupTopicConsumedDetailVO> voList = new ArrayList<>();
        for (GroupMetrics groupMetrics: metricsResult.getData().getBizData()) {
            GroupTopicConsumedDetailVO vo = new GroupTopicConsumedDetailVO();
            vo.setTopicName(topicName);
            vo.setPartitionId(groupMetrics.getPartitionId());

            MemberDescription memberDescription = tpMemberMap.get(new TopicPartition(topicName, groupMetrics.getPartitionId()));
            if (memberDescription != null) {
                vo.setMemberId(memberDescription.consumerId());
                vo.setHost(memberDescription.host());
                vo.setClientId(memberDescription.clientId());
            }

            vo.setLatestMetrics(groupMetrics);
            voList.add(vo);
        }

        return PaginationResult.buildSuc(voList, metricsResult);
    }

    @Override
    public Result<Set<TopicPartitionKS>> listClusterPhyGroupPartitions(Long clusterPhyId, String groupName, Long startTime, Long endTime) {
        try {
            return Result.buildSuc(groupMetricESDAO.listGroupTopicPartitions(clusterPhyId, groupName, startTime, endTime));
        }catch (Exception e){
            return Result.buildFailure(e.getMessage());
        }
    }

    @Override
    public Result<Void> resetGroupOffsets(GroupOffsetResetDTO dto, String operator) throws Exception {
        Result<Void> rv = this.checkFieldLegal(dto);
        if (rv.failed()) {
            return rv;
        }

        ConsumerGroupDescription description = groupService.getGroupDescription(dto.getClusterId(), dto.getGroupName());
        if (ConsumerGroupState.DEAD.equals(description.state()) && !dto.isCreateIfNotExist()) {
            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, "group不存在, 重置失败");
        }

        if (!ConsumerGroupState.EMPTY.equals(description.state()) && !ConsumerGroupState.DEAD.equals(description.state())) {
            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, String.format("group处于%s, 重置失败(仅Empty | Dead 情况可重置)", GroupStateEnum.getByRawState(description.state()).getState()));
        }

        // 获取offset
        Result<Map<TopicPartition, Long>> offsetMapResult = this.getPartitionOffset(dto);
        if (offsetMapResult.failed()) {
            return Result.buildFromIgnoreData(offsetMapResult);
        }

        // 重置offset
        return groupService.resetGroupOffsets(dto.getClusterId(), dto.getGroupName(), offsetMapResult.getData(), operator);
    }


    /**************************************************** private method ****************************************************/


    private Result<Void> checkFieldLegal(GroupOffsetResetDTO dto) {
        if (dto == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "参数为空");
        }

        Topic topic = topicService.getTopic(dto.getClusterId(), dto.getTopicName());
        if (topic == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(dto.getClusterId(), dto.getTopicName()));
        }

        if (OffsetTypeEnum.PRECISE_OFFSET.getResetType() == dto.getResetType()
            && ValidateUtils.isEmptyList(dto.getOffsetList())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "参数错误，指定offset重置需传offset信息");
        }

        if (OffsetTypeEnum.PRECISE_TIMESTAMP.getResetType() == dto.getResetType()
                && ValidateUtils.isNull(dto.getTimestamp())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "参数错误，指定时间重置需传时间信息");
        }

        return Result.buildSuc();
    }

    private Result<Map<TopicPartition, Long>> getPartitionOffset(GroupOffsetResetDTO dto) {
        if (OffsetTypeEnum.PRECISE_OFFSET.getResetType() == dto.getResetType()) {
            return Result.buildSuc(dto.getOffsetList().stream().collect(Collectors.toMap(
                    elem -> new TopicPartition(dto.getTopicName(), elem.getPartitionId()),
                    PartitionOffsetDTO::getOffset,
                    (key1 , key2) -> key2
            )));
        }

        OffsetSpec offsetSpec = null;
        if (OffsetTypeEnum.PRECISE_TIMESTAMP.getResetType() == dto.getResetType()) {
            offsetSpec = OffsetSpec.forTimestamp(dto.getTimestamp());
        } else if (OffsetTypeEnum.EARLIEST.getResetType() == dto.getResetType()) {
            offsetSpec = OffsetSpec.earliest();
        } else {
            offsetSpec = OffsetSpec.latest();
        }

        return partitionService.getPartitionOffsetFromKafka(dto.getClusterId(), dto.getTopicName(), offsetSpec, dto.getTimestamp());
    }

    private List<GroupTopicOverviewVO> convert2GroupTopicOverviewVOList(List<GroupMemberPO> poList, List<GroupMetrics> metricsList) {
        if (metricsList == null) {
            metricsList = new ArrayList<>();
        }

        // <GroupName, <TopicName, GroupMetrics>>
        Map<String, Map<String, GroupMetrics>> metricsMap = new HashMap<>();
        metricsList.stream().forEach(elem -> {
            metricsMap.putIfAbsent(elem.getGroup(), new HashMap<>());
            metricsMap.get(elem.getGroup()).put(elem.getTopic(), elem);
        });

        List<GroupTopicOverviewVO> voList = new ArrayList<>();
        for (GroupMemberPO po: poList) {
            GroupTopicOverviewVO vo = ConvertUtil.obj2Obj(po, GroupTopicOverviewVO.class);
            if (vo == null) {
                continue;
            }

            GroupMetrics metrics = metricsMap.getOrDefault(po.getGroupName(), new HashMap<>()).get(po.getTopicName());
            if (metrics != null) {
                vo.setMaxLag(ConvertUtil.Float2Long(metrics.getMetrics().get(GroupMetricVersionItems.GROUP_METRIC_LAG)));
            }

            voList.add(vo);
        }

        return voList;
    }

    private PaginationResult<GroupMetrics> pagingGroupTopicPartitionMetrics(Long clusterPhyId,
                                                                            String groupName,
                                                                            String topicName,
                                                                            List<Integer> partitionIdList,
                                                                            List<String> latestMetricNames,
                                                                            PaginationSortDTO dto) {


        // 获取Group指标信息
        Result<List<GroupMetrics>> groupMetricsResult = groupMetricService.collectGroupMetricsFromKafka(clusterPhyId, groupName, latestMetricNames == null ? Arrays.asList() : latestMetricNames);


        // 转换Group指标
        List<GroupMetrics> esGroupMetricsList = groupMetricsResult.hasData() ? groupMetricsResult.getData().stream().filter(elem -> topicName.equals(elem.getTopic())).collect(Collectors.toList()) : new ArrayList<>();
        Map<Integer, GroupMetrics> esMetricsMap = new HashMap<>();
        for (GroupMetrics groupMetrics: esGroupMetricsList) {
            esMetricsMap.put(groupMetrics.getPartitionId(), groupMetrics);
        }

        List<GroupMetrics> allPartitionGroupMetrics = new ArrayList<>();
        for (Integer partitionId: partitionIdList) {
            allPartitionGroupMetrics.add(esMetricsMap.getOrDefault(partitionId, new GroupMetrics(clusterPhyId, groupName, topicName, partitionId)));
        }

        return PaginationUtil.pageBySubData(
                (List<GroupMetrics>)PaginationMetricsUtil.sortMetrics(allPartitionGroupMetrics, dto.getSortField(), "partitionId", dto.getSortType()),
                dto
        );
    }

}
