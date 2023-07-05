package com.xiaojukeji.know.streaming.km.core.service.partition.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.partition.BatchPartitionParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.core.service.partition.OpPartitionService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ElectLeadersOptions;
import org.apache.kafka.clients.admin.ElectLeadersResult;
import org.apache.kafka.common.ElectionType;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.jdk.javaapi.CollectionConverters;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_HANDLE_NOT_EXIST;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_PARTITION_LEADER;


/**
 * @author didi
 */
@Service
public class OpPartitionServiceImpl extends BaseKafkaVersionControlService implements OpPartitionService {
    private static final ILog LOGGER = LogFactory.getLog(OpPartitionServiceImpl.class);

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    public static final String PREFERRED_REPLICA_ELECTION = "PreferredReplicaElection";

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_PARTITION_LEADER;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(PREFERRED_REPLICA_ELECTION,     V_0_10_0_0, V_2_8_0,  "preferredReplicaElectionByZKClient",             this::preferredReplicaElectionByZKClient);
        registerVCHandler(PREFERRED_REPLICA_ELECTION,     V_2_8_0, V_MAX,       "preferredReplicaElectionByKafkaClient",          this::preferredReplicaElectionByKafkaClient);
    }

    @Override
    public Result<Void> preferredReplicaElection(Long clusterPhyId, List<TopicPartition> tpList) {
        try {
            return (Result<Void>) doVCHandler(
                    clusterPhyId,
                    PREFERRED_REPLICA_ELECTION,
                    new BatchPartitionParam(clusterPhyId, tpList)
            );
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    /**************************************************** private method ****************************************************/

    private Result<Void> preferredReplicaElectionByZKClient(VersionItemParam itemParam) {
        BatchPartitionParam partitionParam = (BatchPartitionParam) itemParam;

        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(partitionParam.getClusterPhyId());

            kafkaZkClient.createPreferredReplicaElection(CollectionConverters.asScala(partitionParam.getTpList()).toSet());

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "method=preferredReplicaElectionByZKClient||clusterPhyId={}||errMsg=exception",
                    partitionParam.getClusterPhyId(), e
            );

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> preferredReplicaElectionByKafkaClient(VersionItemParam itemParam) {
        BatchPartitionParam partitionParam = (BatchPartitionParam) itemParam;

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(partitionParam.getClusterPhyId());

            ElectLeadersResult electLeadersResult = adminClient.electLeaders(
                    ElectionType.PREFERRED,
                    new HashSet<>(partitionParam.getTpList()),
                    new ElectLeadersOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            electLeadersResult.all().get();

            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error(
                    "method=preferredReplicaElectionByKafkaClient||clusterPhyId={}||errMsg=exception",
                    partitionParam.getClusterPhyId(), e
            );

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }
}
