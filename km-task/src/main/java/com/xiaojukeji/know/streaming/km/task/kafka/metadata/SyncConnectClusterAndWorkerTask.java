package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectClusterMetadata;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.Group;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSGroupDescription;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSMemberConnectAssignment;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSMemberDescription;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.jmx.JmxEnum;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.group.GroupTypeEnum.CONNECT_CLUSTER_PROTOCOL_TYPE;

@Task(name = "SyncClusterAndWorkerTask",
        description = "Connect-Cluster&Worker信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncConnectClusterAndWorkerTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog LOGGER = LogFactory.getLog(SyncConnectClusterAndWorkerTask.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ConnectClusterService connectClusterService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        boolean allSuccess = true;

        //获取connect集群
        List<Group> groupList = groupService.listClusterGroups(clusterPhy.getId()).stream().filter(elem->elem.getType()==GroupTypeEnum.CONNECT_CLUSTER).collect(Collectors.toList());
        for (Group group: groupList) {
            try {
                KSGroupDescription ksGroupDescription = groupService.getGroupDescriptionFromKafka(clusterPhy, group.getName());
                if (!ksGroupDescription.protocolType().equals(CONNECT_CLUSTER_PROTOCOL_TYPE)) {
                    continue;
                }

                Result<Long> rl = this.handleConnectClusterMetadata(clusterPhy.getId(), group.getName(), ksGroupDescription);
                if (rl.failed()) {
                    allSuccess = false;
                    continue;
                }

                Result<Void> rv = this.handleWorkerMetadata(rl.getData(), ksGroupDescription);
                if (rv.failed()) {
                    allSuccess = false;
                }

            } catch (Exception e) {
                LOGGER.error(
                        "class=SyncClusterAndWorkerTask||method=processClusterTask||clusterPhyId={}||groupName={}||errMsg=exception.",
                        clusterPhy.getId(), group.getName(), e
                );

                allSuccess = false;
            }
        }

        return allSuccess? TaskResult.SUCCESS: TaskResult.FAIL;
    }


    private Result<Void> handleWorkerMetadata(Long connectClusterId, KSGroupDescription ksGroupDescription) {
        try {
            List<ConnectWorker> workerList = new ArrayList<>();
            ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(connectClusterId);

            for (KSMemberDescription memberDescription: ksGroupDescription.members()) {
                KSMemberConnectAssignment assignment = (KSMemberConnectAssignment) memberDescription.assignment();
                if (assignment != null) {
                    workerList.add(new ConnectWorker(
                            connectCluster.getKafkaClusterPhyId(),
                            connectClusterId,
                            memberDescription.consumerId(),
                            memberDescription.host().substring(1),
                            JmxEnum.UNKNOWN.getPort(),
                            assignment.getWorkerState().url(),
                            assignment.getAssignment().leaderUrl(),
                            memberDescription.consumerId().equals(assignment.getAssignment().leader()) ? Constant.YES : Constant.NO
                    ));
                } else {
                    workerList.add(new ConnectWorker(
                            connectCluster.getKafkaClusterPhyId(),
                            connectClusterId,
                            memberDescription.consumerId(),
                            memberDescription.host().substring(1),
                            JmxEnum.UNKNOWN.getPort(),
                            "",
                            "",
                            Constant.NO
                    ));
                }
            }

            workerService.batchReplaceInDB(connectClusterId, workerList);
        } catch (Exception e) {
            LOGGER.error(
                    "class=SyncClusterAndWorkerTask||method=handleWorkerMetadata||connectClusterId={}||ksGroupDescription={}||errMsg=exception.",
                    connectClusterId, ksGroupDescription, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Long> handleConnectClusterMetadata(Long clusterPhyId, String groupName, KSGroupDescription ksGroupDescription) {
        try {
            for (KSMemberDescription memberDescription: ksGroupDescription.members()) {
                KSMemberConnectAssignment assignment = (KSMemberConnectAssignment) memberDescription.assignment();

                ConnectClusterMetadata metadata = new ConnectClusterMetadata(
                        clusterPhyId,
                        groupName,
                        GroupStateEnum.getByRawState(ksGroupDescription.state()),
                        assignment == null? "": assignment.getAssignment().leaderUrl()
                );

                Long connectClusterId = connectClusterService.replaceAndReturnIdInDB(metadata);

                return Result.buildSuc(connectClusterId);
            }
        } catch (Exception e) {
            LOGGER.error(
                    "class=SyncClusterAndWorkerTask||method=handleConnectClusterMetadata||clusterPhyId={}||groupName={}||ksGroupDescription={}||errMsg=exception.",
                    clusterPhyId, groupName, ksGroupDescription, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, "消费组无成员");
    }
}
