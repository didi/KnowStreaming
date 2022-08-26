package com.xiaojukeji.know.streaming.km.task.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.task.AbstractClusterPhyDispatchTask;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;


@Task(name = "SyncKafkaGroupTask",
        description = "KafkaGroup信息同步到DB,",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncKafkaGroupTask extends AbstractClusterPhyDispatchTask {
    private static final ILog log = LogFactory.getLog(SyncKafkaGroupTask.class);

    @Autowired
    private GroupService groupService;

    @Override
    public TaskResult processSubTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) throws Exception {
        TaskResult tr = TaskResult.SUCCESS;

        List<String> groupNameList = groupService.listGroupsFromKafka(clusterPhy.getId());
        for (String groupName: groupNameList) {
            if (!TaskResult.SUCCESS.equals(this.updateGroupMembersTask(clusterPhy, groupName, triggerTimeUnitMs))) {
                tr = TaskResult.FAIL;
            }
        }

        if (!TaskResult.SUCCESS.equals(tr)) {
            return tr;
        }

        // 删除历史的Group
        groupService.deleteByUpdateTimeBeforeInDB(clusterPhy.getId(), new Date(triggerTimeUnitMs - 5 * 60 * 1000));

        return tr;
    }

    private TaskResult updateGroupMembersTask(ClusterPhy clusterPhy, String groupName, long triggerTimeUnitMs) {
        try {
            List<GroupMemberPO> poList = this.getGroupMembers(clusterPhy.getId(), groupName, new Date(triggerTimeUnitMs));
            for (GroupMemberPO po: poList) {
                groupService.replaceDBData(po);
            }
        } catch (Exception e) {
            log.error("method=updateGroupMembersTask||clusterPhyId={}||groupName={}||errMsg={}", clusterPhy.getId(), groupName, e.getMessage());

            return TaskResult.FAIL;
        }

        return TaskResult.SUCCESS;
    }

    private List<GroupMemberPO> getGroupMembers(Long clusterPhyId, String groupName, Date updateTime) throws NotExistException, AdminOperateException {
        Map<String, GroupMemberPO> groupMap = new HashMap<>();

        // 获取消费组消费过哪些Topic
        Map<TopicPartition, Long> offsetMap = groupService.getGroupOffset(clusterPhyId, groupName);
        for (TopicPartition topicPartition: offsetMap.keySet()) {
            GroupMemberPO po = groupMap.get(topicPartition.topic());
            if (po == null) {
                po = new GroupMemberPO(clusterPhyId, topicPartition.topic(), groupName, updateTime);
            }
            groupMap.put(topicPartition.topic(), po);
        }

        // 在上面的基础上，补充消费组的详细信息
        ConsumerGroupDescription consumerGroupDescription = groupService.getGroupDescription(clusterPhyId, groupName);
        if (consumerGroupDescription == null) {
            return new ArrayList<>(groupMap.values());
        }

        groupMap.forEach((key, val) -> val.setState(GroupStateEnum.getByRawState(consumerGroupDescription.state()).getState()));

        for (MemberDescription memberDescription : consumerGroupDescription.members()) {
            Set<TopicPartition> partitionList = new HashSet<>();
            if (!ValidateUtils.isNull(memberDescription.assignment().topicPartitions())) {
                partitionList = memberDescription.assignment().topicPartitions();
            }

            Set<String> topicNameSet = partitionList.stream().map(elem -> elem.topic()).collect(Collectors.toSet());
            for (String topicName: topicNameSet) {
                groupMap.putIfAbsent(topicName, new GroupMemberPO(clusterPhyId, topicName, groupName, updateTime));

                GroupMemberPO po = groupMap.get(topicName);
                po.setMemberCount(po.getMemberCount() + 1);
                po.setState(GroupStateEnum.getByRawState(consumerGroupDescription.state()).getState());
            }
        }

        // 如果该消费组没有正在消费任何Topic的特殊情况，但是这个Group存在
        if (groupMap.isEmpty()) {
            GroupMemberPO po = new GroupMemberPO(clusterPhyId, "", groupName, updateTime);
            po.setState(GroupStateEnum.getByRawState(consumerGroupDescription.state()).getState());
            groupMap.put("", po);
        }

        return new ArrayList<>(groupMap.values());
    }
}
