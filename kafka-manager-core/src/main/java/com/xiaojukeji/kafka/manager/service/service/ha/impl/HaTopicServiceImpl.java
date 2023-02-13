package com.xiaojukeji.kafka.manager.service.service.ha.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaRelationTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.constant.MsgConstant;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxAttributeEnum;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConnectorWrap;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaKafkaUserService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaTopicService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import com.xiaojukeji.kafka.manager.service.utils.HaTopicCommands;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import com.xiaojukeji.kafka.manager.service.utils.TopicCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.management.Attribute;
import javax.management.ObjectName;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HaTopicServiceImpl implements HaTopicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaTopicServiceImpl.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private HaKafkaUserService haKafkaUserService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private TopicManagerService topicManagerService;

    @Override
    public Result<Void> createHA(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName, String operator) {
        ClusterDO activeClusterDO = PhysicalClusterMetadataManager.getClusterFromCache(activeClusterPhyId);
        if (activeClusterDO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, "主集群不存在");
        }

        ClusterDO standbyClusterDO = PhysicalClusterMetadataManager.getClusterFromCache(standbyClusterPhyId);
        if (standbyClusterDO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, "备集群不存在");
        }

        // 查询关系是否已经存在
        HaASRelationDO relationDO = haASRelationService.getSpecifiedHAFromDB(
                activeClusterPhyId,
                topicName,
                standbyClusterPhyId,
                topicName,
                HaResTypeEnum.TOPIC
        );
        if (relationDO != null) {
            // 如果已存在该高可用Topic，则直接返回成功
            return Result.buildSuc();
        }

        Result<TopicDO> checkResult = this.checkHaTopicAndGetBizInfo(activeClusterPhyId, standbyClusterPhyId, topicName);
        if (checkResult.failed()){
            return Result.buildFromIgnoreData(checkResult);
        }

        //更新高可用Topic配置
        Result<Void> rv = this.modifyHaConfig(
                activeClusterDO,
                topicName,
                standbyClusterDO,
                topicName,
                operator
        );
        if (rv.failed()){
            return rv;
        }

        // 新增备Topic
        rv = this.addStandbyTopic(checkResult.getData(), activeClusterDO, standbyClusterDO, operator);
        if (rv.failed()) {
            return rv;
        }

        // 备topic添加权限以及quota
        rv = this.addStandbyTopicAuthorityAndQuota(activeClusterPhyId, standbyClusterPhyId, topicName);
        if (rv.failed()){
            return rv;
        }

        //添加db业务信息
        return haASRelationService.addHAToDB(
                new HaASRelationDO(
                        activeClusterPhyId,
                        topicName,
                        standbyClusterPhyId,
                        topicName,
                        HaResTypeEnum.TOPIC.getCode(),
                        HaStatusEnum.STABLE.getCode()
                )
        );
    }

    private Result<Void> addStandbyTopic(TopicDO activeTopicDO, ClusterDO activeClusterDO, ClusterDO standbyClusterDO, String operator){
        // 获取主Topic配置信息
        Properties activeTopicProps = TopicCommands.fetchTopicConfig(activeClusterDO, activeTopicDO.getTopicName());
        if (activeTopicProps == null){
            return Result.buildFromRSAndMsg(ResultStatus.FAIL, "创建备Topic时，获取主Topic配置失败");
        }

        TopicDO newTopicDO = new TopicDO(
                activeTopicDO.getAppId(),
                standbyClusterDO.getId(),
                activeTopicDO.getTopicName(),
                activeTopicDO.getDescription(),
                TopicCreationConstant.DEFAULT_QUOTA
        );
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(activeClusterDO.getId(), activeTopicDO.getTopicName());

        ResultStatus rs = adminService.createTopic(standbyClusterDO,
                newTopicDO,
                topicMetadata.getPartitionNum(),
                topicMetadata.getReplicaNum(),
                null,
                PhysicalClusterMetadataManager.getBrokerIdList(standbyClusterDO.getId()),
                activeTopicProps,
                operator,
                operator
        );

        if (ResultStatus.SUCCESS.equals(rs)) {
            LOGGER.error(
                    "method=createHA||activeClusterPhyId={}||standbyClusterPhyId={}||activeTopicDO={}||result={}||msg=create haTopic create topic failed.",
                    activeClusterDO.getId(), standbyClusterDO.getId(), activeTopicDO, rs
            );
            return Result.buildFromRSAndMsg(rs, String.format("创建备Topic失败，原因：%s", rs.getMessage()));
        }

        return Result.buildSuc();
    }

    @Override
    public Result<Void> activeHAInKafka(ClusterDO activeClusterDO, String activeTopicName, ClusterDO standbyClusterDO, String standbyTopicName, String operator) {
        if (!PhysicalClusterMetadataManager.isTopicExist(activeClusterDO.getId(), activeTopicName)) {
            // 主Topic不存在
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(standbyClusterDO.getId(), standbyTopicName)) {
            // 备Topic不存在
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }

        return this.activeTopicHAConfigInKafka(activeClusterDO, activeTopicName, standbyClusterDO, standbyTopicName);
    }

    @Override
    public Result<Void> activeHAInKafkaNotCheck(ClusterDO activeClusterDO, String activeTopicName, ClusterDO standbyClusterDO, String standbyTopicName, String operator) {
        //更新开启topic高可用配置，并将备集群的配置信息指向主集群
        Result<Void> rv = activeTopicHAConfigInKafka(activeClusterDO, activeTopicName, standbyClusterDO, standbyTopicName);
        if (rv.failed()){
            return rv;
        }
        return Result.buildSuc();
    }

    @Override
    @Transactional
    public Result<Void> deleteHA(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName, String operator) {
        ClusterDO activeClusterDO = clusterService.getById(activeClusterPhyId);
        if (activeClusterDO == null){
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, "主集群不存在");
        }

        ClusterDO standbyClusterDO = clusterService.getById(standbyClusterPhyId);
        if (standbyClusterDO == null){
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, "备集群不存在");
        }

        HaASRelationDO relationDO = haASRelationService.getHAFromDB(
                activeClusterPhyId,
                topicName,
                HaResTypeEnum.TOPIC
        );
        if (relationDO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, "主备关系不存在");
        }
        if (!relationDO.getStatus().equals(HaStatusEnum.STABLE_CODE)) {
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FORBIDDEN, "主备切换中，不允许解绑");
        }

        // 删除高可用配置信息
        Result<Void> rv = this.stopHAInKafka(standbyClusterDO, topicName, operator);
        if(rv.failed()){
            return rv;
        }

        rv = haASRelationService.deleteById(relationDO.getId());
        if(rv.failed()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return rv;
        }

        return rv;
    }

    @Override
    public Result<Void> stopHAInKafka(ClusterDO standbyClusterDO, String standbyTopicName, String operator) {
        //删除副集群同步主集群topic配置
        ResultStatus rs = HaTopicCommands.deleteHaTopicConfig(
                standbyClusterDO,
                standbyTopicName,
                Arrays.asList(KafkaConstant.DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED, KafkaConstant.DIDI_HA_REMOTE_CLUSTER)
        );
        if (!ResultStatus.SUCCESS.equals(rs)) {
            LOGGER.error(
                    "method=deleteHAInKafka||standbyClusterId={}||standbyTopicName={}||rs={}||msg=delete topic ha failed.",
                    standbyClusterDO.getId(), standbyTopicName, rs
            );
            return Result.buildFromRSAndMsg(rs, "delete topic ha failed");
        }

        return Result.buildSuc();
    }

    @Override
    public Map<String, Integer> getRelation(Long clusterId) {
        Map<String, Integer> relationMap = new HashMap<>();
        List<HaASRelationDO> relationDOS = haASRelationService.listAllHAFromDB(clusterId, HaResTypeEnum.TOPIC);
        if (relationDOS.isEmpty()){
            return relationMap;
        }

        //主topic
        List<String> activeTopics = relationDOS.stream().filter(haASRelationDO -> haASRelationDO.getActiveClusterPhyId().equals(clusterId)).map(HaASRelationDO::getActiveResName).collect(Collectors.toList());
        activeTopics.stream().forEach(topicName -> relationMap.put(topicName, HaRelationTypeEnum.ACTIVE.getCode()));

        //备topic
        List<String> standbyTopics = relationDOS.stream().filter(haASRelationDO -> haASRelationDO.getStandbyClusterPhyId().equals(clusterId)).map(HaASRelationDO::getStandbyResName).collect(Collectors.toList());
        standbyTopics.stream().forEach(topicName -> relationMap.put(topicName, HaRelationTypeEnum.STANDBY.getCode()));

        //互备
        relationMap.put(KafkaConstant.COORDINATOR_TOPIC_NAME, HaRelationTypeEnum.MUTUAL_BACKUP.getCode());

        return relationMap;
    }

    @Override
    public Map<Long, List<String>> getClusterStandbyTopicMap() {
        Map<Long, List<String>> clusterStandbyTopicMap = new HashMap<>();
        List<HaASRelationDO> relationDOS = haASRelationService.listAllHAFromDB(HaResTypeEnum.TOPIC);
        if (relationDOS.isEmpty()){
            return clusterStandbyTopicMap;
        }
        return relationDOS.stream().collect(Collectors.groupingBy(HaASRelationDO::getStandbyClusterPhyId, Collectors.mapping(HaASRelationDO::getStandbyResName, Collectors.toList())));
    }

    @Override
    public Result<Void> activeUserHAInKafka(ClusterDO activeClusterDO, ClusterDO standbyClusterDO, String kafkaUser, String operator) {
        Result<Void> rv;
        rv = haKafkaUserService.activeHAInKafka(activeClusterDO.getZookeeper(), activeClusterDO.getId(), kafkaUser);
        if (rv.failed()) {
            return rv;
        }

        rv = haKafkaUserService.activeHAInKafka(standbyClusterDO.getZookeeper(), activeClusterDO.getId(), kafkaUser);
        if (rv.failed()) {
            return rv;
        }

        rv = haKafkaUserService.activeHAInKafka(configUtils.getDKafkaGatewayZK(), activeClusterDO.getId(), kafkaUser);
        if (rv.failed()) {
            return rv;
        }
        return rv;
    }

    @Override
    public Result<Long> getStandbyTopicFetchLag(Long standbyClusterPhyId, String topicName) {
        TopicMetadata metadata = PhysicalClusterMetadataManager.getTopicMetadata(standbyClusterPhyId, topicName);
        if (metadata == null) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getTopicNotExist(standbyClusterPhyId, topicName));
        }

        List<Integer> partitionIdList = new ArrayList<>(metadata.getPartitionMap().getPartitions().keySet());

        List<PartitionState> partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
                PhysicalClusterMetadataManager.getZKConfig(standbyClusterPhyId),
                topicName,
                partitionIdList
        );

        if (partitionStateList.size() != partitionIdList.size()) {
            return Result.buildFromRSAndMsg(ResultStatus.ZOOKEEPER_READ_FAILED, "读取ZK的分区元信息失败");
        }

        Long sumLag = 0L;
        for (Integer leaderBrokerId: partitionStateList.stream().map(elem -> elem.getLeader()).collect(Collectors.toSet())) {
            JmxConnectorWrap jmxConnectorWrap = PhysicalClusterMetadataManager.getJmxConnectorWrap(standbyClusterPhyId, leaderBrokerId);
            if (jmxConnectorWrap == null || !jmxConnectorWrap.checkJmxConnectionAndInitIfNeed()) {
                return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, String.format("获取BrokerId=%d的jmx客户端失败", leaderBrokerId));
            }


            try {
                ObjectName objectName = new ObjectName(
                        "kafka.server:type=FetcherLagMetrics,name=ConsumerLag,clientId=MirrorFetcherThread-*" + "-" + standbyClusterPhyId + "*" + ",topic=" + topicName + ",partition=*"
                );

                Set<ObjectName> objectNameSet = jmxConnectorWrap.queryNames(objectName, null);
                for (ObjectName name: objectNameSet) {
                    List<Attribute> attributeList = jmxConnectorWrap.getAttributes(name, JmxAttributeEnum.VALUE_ATTRIBUTE.getAttribute()).asList();
                    for (Attribute attribute: attributeList) {
                        sumLag += Long.valueOf(attribute.getValue().toString());
                    }
                }
            } catch (Exception e) {
                LOGGER.error(
                        "class=HaTopicServiceImpl||method=getStandbyTopicFetchLag||standbyClusterPhyId={}||topicName={}||leaderBrokerId={}||errMsg=exception.",
                        standbyClusterPhyId, topicName, leaderBrokerId, e
                );

                return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, e.getMessage());
            }
        }

        return Result.buildSuc(sumLag);
    }

    /**************************************************** private method ****************************************************/

    private Result<Void> activeTopicHAConfigInKafka(ClusterDO activeClusterDO, String activeTopicName, ClusterDO standbyClusterDO, String standbyTopicName) {
        //更新ha-topic配置
        Properties standbyTopicProps = new Properties();
        standbyTopicProps.put(KafkaConstant.DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED, Boolean.TRUE.toString());
        standbyTopicProps.put(KafkaConstant.DIDI_HA_REMOTE_CLUSTER, activeClusterDO.getId().toString());
        if (!activeTopicName.equals(standbyTopicName)) {
            standbyTopicProps.put(KafkaConstant.DIDI_HA_REMOTE_TOPIC, activeTopicName);
        }
        ResultStatus rs = HaTopicCommands.modifyHaTopicConfig(standbyClusterDO, standbyTopicName, standbyTopicProps);
        if (!ResultStatus.SUCCESS.equals(rs)) {
            LOGGER.error(
                    "method=createHAInKafka||activeClusterId={}||activeTopicName={}||standbyClusterId={}||standbyTopicName={}||rs={}||msg=create topic ha failed.",
                    activeClusterDO.getId(), activeTopicName, standbyClusterDO.getId(), standbyTopicName, rs
            );
            return Result.buildFromRSAndMsg(rs, "modify ha topic config failed");
        }

        return Result.buildSuc();
    }

    public Result<Void> addStandbyTopicAuthorityAndQuota(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName) {
        List<AuthorityDO> authorityDOS = authorityService.getAuthorityByTopic(activeClusterPhyId, topicName);
        try {
            for (AuthorityDO authorityDO : authorityDOS) {
                //权限
                AuthorityDO newAuthorityDO = new AuthorityDO();
                newAuthorityDO.setAppId(authorityDO.getAppId());
                newAuthorityDO.setClusterId(standbyClusterPhyId);
                newAuthorityDO.setTopicName(topicName);
                newAuthorityDO.setAccess(authorityDO.getAccess());

                //quota
                TopicQuota activeTopicQuotaDO = quotaService.getQuotaFromZk(
                        activeClusterPhyId,
                        topicName,
                        authorityDO.getAppId()
                );

                TopicQuota standbyTopicQuotaDO = new TopicQuota();
                standbyTopicQuotaDO.setTopicName(topicName);
                standbyTopicQuotaDO.setAppId(activeTopicQuotaDO.getAppId());
                standbyTopicQuotaDO.setClusterId(standbyClusterPhyId);
                standbyTopicQuotaDO.setConsumeQuota(activeTopicQuotaDO.getConsumeQuota());
                standbyTopicQuotaDO.setProduceQuota(activeTopicQuotaDO.getProduceQuota());

                int result = authorityService.addAuthorityAndQuota(newAuthorityDO, standbyTopicQuotaDO);
                if (Constant.INVALID_CODE == result){
                    return Result.buildFrom(ResultStatus.OPERATION_FAILED);
                }
            }
        } catch (Exception e) {
            LOGGER.error(
                    "method=addStandbyTopicAuthorityAndQuota||activeClusterPhyId={}||standbyClusterPhyId={}||topicName={}||errMsg=exception.",
                    activeClusterPhyId, standbyClusterPhyId, topicName, e
            );

            return Result.buildFailure("备Topic复制主Topic权限及配额失败");
        }

        return Result.buildSuc();
    }

    private Result<TopicDO> checkHaTopicAndGetBizInfo(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName){
        if (PhysicalClusterMetadataManager.isTopicExist(standbyClusterPhyId, topicName)) {
            return Result.buildFromRSAndMsg(ResultStatus.TOPIC_ALREADY_EXIST, "备集群已存在该Topic，请先删除,再行绑定！");
        }

        if (!PhysicalClusterMetadataManager.isTopicExist(activeClusterPhyId, topicName)) {
            return Result.buildFromRSAndMsg(ResultStatus.TOPIC_NOT_EXIST, "主集群不存在该Topic");
        }

        TopicDO topicDO = topicManagerService.getByTopicName(activeClusterPhyId, topicName);
        if (ValidateUtils.isNull(topicDO)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, "主集群Topic所属KafkaUser信息不存在");
        }

        return Result.buildSuc(topicDO);
    }

    private Result<Void> modifyHaConfig(ClusterDO activeClusterDO, String activeTopic, ClusterDO standbyClusterDO, String standbyTopic, String operator){
        //更新副集群同步主集群topic配置
        Result<Void> rv = activeHAInKafkaNotCheck(activeClusterDO, activeTopic, standbyClusterDO, standbyTopic, operator);
        if (rv.failed()){
            LOGGER.error("method=createHA||activeTopic:{} standbyTopic:{}||msg=create haTopic modify standby topic config failed!.", activeTopic, standbyTopic);
            return Result.buildFailure("modify standby topic config failed,please try again");
        }

        //更新user配置，通知用户指向主集群
        Set<String> relatedKafkaUserSet = authorityService.getAuthorityByTopic(activeClusterDO.getId(), activeTopic)
                .stream()
                .map(elem -> elem.getAppId())
                .collect(Collectors.toSet());
        for(String kafkaUser: relatedKafkaUserSet) {
            rv = this.activeUserHAInKafka(activeClusterDO, standbyClusterDO, kafkaUser, operator);
            if (rv.failed()) {
                return rv;
            }
        }
        return Result.buildSuc();
    }
}
