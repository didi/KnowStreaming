package com.xiaojukeji.kafka.manager.service.service.ha.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaRelationTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.job.HaJobStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.constant.MsgConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchJobDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.HaClusterVO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.dao.ha.HaASRelationDao;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASSwitchJobService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaClusterService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaTopicService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import com.xiaojukeji.kafka.manager.service.utils.HaClusterCommands;
import com.xiaojukeji.kafka.manager.service.utils.HaTopicCommands;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集群主备关系
 */
@Service
public class HaClusterServiceImpl implements HaClusterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaClusterServiceImpl.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Autowired
    private HaASRelationDao haActiveStandbyRelationDao;

    @Autowired
    private HaTopicService haTopicService;

    @Autowired
    private PhysicalClusterMetadataManager physicalClusterMetadataManager;

    @Autowired
    private HaASSwitchJobService haASSwitchJobService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public Result<Void> createHA(Long activeClusterPhyId, Long standbyClusterPhyId, String operator) {
        ClusterDO activeClusterDO = clusterService.getById(activeClusterPhyId);
        if (activeClusterDO == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        ClusterDO standbyClusterDO = clusterService.getById(standbyClusterPhyId);
        if (standbyClusterDO == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        HaASRelationDO oldRelationDO = getHA(activeClusterPhyId);
        if (oldRelationDO != null){
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_ALREADY_USED,
                    MsgConstant.getActiveClusterDuplicate(activeClusterDO.getId(), activeClusterDO.getClusterName()));

        }

        //更新集群配置
        Result<Void> rv = this.modifyHaClusterConfig(activeClusterDO, standbyClusterDO, operator);
        if (rv.failed()){
            return rv;
        }

        //更新__consumer_offsets配置
        rv = this.modifyHaTopicConfig(activeClusterDO, standbyClusterDO, operator);
        if (rv.failed()){
            return rv;
        }

        //添加db数据
        return haASRelationService.addHAToDB(
                new HaASRelationDO(
                        activeClusterPhyId,
                        activeClusterPhyId.toString(),
                        standbyClusterPhyId,
                        standbyClusterPhyId.toString(),
                        HaResTypeEnum.CLUSTER.getCode(),
                        HaStatusEnum.STABLE.getCode()
                )
        );
    }

    @Override
    public Result<Void> createHAInKafka(String zookeeper, ClusterDO needWriteToZKClusterDO, String operator) {
        Properties props = new Properties();
        props.putAll(getSecurityProperties(needWriteToZKClusterDO.getSecurityProperties()));
        props.put(KafkaConstant.BOOTSTRAP_SERVERS, needWriteToZKClusterDO.getBootstrapServers());
        props.put(KafkaConstant.DIDI_KAFKA_ENABLE, "false");

        Result<List<Integer>> rli = zookeeperService.getBrokerIds(needWriteToZKClusterDO.getZookeeper());
        if (rli.failed()){
            return Result.buildFromIgnoreData(rli);
        }

        String kafkaVersion = physicalClusterMetadataManager.getKafkaVersion(needWriteToZKClusterDO.getId(), rli.getData());
        if (kafkaVersion != null && kafkaVersion.contains("-d-")){
            int dVersion = Integer.valueOf(kafkaVersion.split("-")[2]);
            if (dVersion > 200){
                props.put(KafkaConstant.DIDI_KAFKA_ENABLE, "true");
            }
        }

        ResultStatus rs = HaClusterCommands.modifyHaClusterConfig(zookeeper, needWriteToZKClusterDO.getId(), props);
        if (!ResultStatus.SUCCESS.equals(rs)) {
            LOGGER.error("class=HaClusterServiceImpl||method=createHAInKafka||zookeeper={}||firstClusterDO={}||operator={}||msg=add ha-cluster config failed!", zookeeper, needWriteToZKClusterDO, operator);
            return Result.buildFailure("add ha-cluster config failed");
        }

        return Result.buildFrom(rs);
    }

    @Override
    public Result<Void> switchHA(Long newActiveClusterPhyId, Long newStandbyClusterPhyId) {
        return Result.buildSuc();
    }

    @Override
    public Result<Void> deleteHA(Long activeClusterPhyId, Long standbyClusterPhyId) {
        ClusterDO clusterDO = clusterService.getById(activeClusterPhyId);
        if (clusterDO == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        ClusterDO standbyClusterDO = clusterService.getById(standbyClusterPhyId);
        if (standbyClusterDO == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        HaASRelationDO relationDO = getHA(activeClusterPhyId);
        if (relationDO == null){
            return Result.buildSuc();
        }

        //删除配置
        Result delResult = delClusterHaConfig(clusterDO, standbyClusterDO);
        if (delResult.failed()){
            return delResult;
        }

        //删除db
        Result delDbResult = delDBHaCluster(activeClusterPhyId, standbyClusterPhyId);
        if (delDbResult.failed()){
            return delDbResult;
        }

        return Result.buildSuc();
    }

    @Override
    public HaASRelationDO getHA(Long activeClusterPhyId) {
        return haASRelationService.getActiveClusterHAFromDB(activeClusterPhyId);
    }

    @Override
    public Map<Long, Integer> getClusterHARelation() {
        Map<Long, Integer> relationMap = new HashMap<>();
        List<HaASRelationDO> haASRelationDOS = haASRelationService.listAllHAFromDB(HaResTypeEnum.CLUSTER);
        if (haASRelationDOS.isEmpty()){
            return relationMap;
        }
        haASRelationDOS.forEach(haASRelationDO -> {
            relationMap.put(haASRelationDO.getActiveClusterPhyId(), HaRelationTypeEnum.ACTIVE.getCode());
            relationMap.put(haASRelationDO.getStandbyClusterPhyId(), HaRelationTypeEnum.STANDBY.getCode());
        });
        return relationMap;
    }

    @Override
    public Result<List<HaClusterVO>> listAllHA() {
        //高可用集群
        List<HaASRelationDO> clusterRelationDOS = haASRelationService.listAllHAFromDB(HaResTypeEnum.CLUSTER);
        Map<Long, HaASRelationDO> activeMap = clusterRelationDOS.stream().collect(Collectors.toMap(HaASRelationDO::getActiveClusterPhyId, Function.identity()));
        List<Long> standbyList = clusterRelationDOS.stream().map(HaASRelationDO::getStandbyClusterPhyId).collect(Collectors.toList());

        //高可用topic
        List<HaASRelationDO> topicRelationDOS = haASRelationService.listAllHAFromDB(HaResTypeEnum.TOPIC);
        //主集群topic数
        Map<Long,Long> activeTopicCountMap = topicRelationDOS.stream()
                .filter(haASRelationDO -> !haASRelationDO.getActiveResName().startsWith("__"))
                .collect(Collectors.groupingBy(HaASRelationDO::getActiveClusterPhyId, Collectors.counting()));
        Map<Long,Long> standbyTopicCountMap = topicRelationDOS.stream()
                .filter(haASRelationDO -> !haASRelationDO.getStandbyResName().startsWith("__"))
                .collect(Collectors.groupingBy(HaASRelationDO::getStandbyClusterPhyId, Collectors.counting()));

        //切换job
        Map<Long/*集群ID*/, HaASSwitchJobDO>  jobDOS = haASSwitchJobService.listClusterLatestJobs();

        List<HaClusterVO> haClusterVOS = new ArrayList<>();
        Map<Long,ClusterDetailDTO> clusterDetailDTOMap = clusterService.getClusterDetailDTOList(Boolean.TRUE).stream().collect(Collectors.toMap(ClusterDetailDTO::getClusterId, Function.identity()));
        for (Map.Entry<Long,ClusterDetailDTO> entry : clusterDetailDTOMap.entrySet()){
            ClusterDetailDTO clusterDetailDTO = entry.getValue();
            //高可用集群
            if (activeMap.containsKey(entry.getKey())){
                //主集群
                HaASRelationDO relationDO = activeMap.get(clusterDetailDTO.getClusterId());
                HaClusterVO haClusterVO = new HaClusterVO();
                BeanUtils.copyProperties(clusterDetailDTO,haClusterVO);
                haClusterVO.setHaStatus(relationDO.getStatus());
                haClusterVO.setActiveTopicCount(activeTopicCountMap.get(clusterDetailDTO.getClusterId())==null
                        ?0L:activeTopicCountMap.get(clusterDetailDTO.getClusterId()));
                haClusterVO.setStandbyTopicCount(standbyTopicCountMap.get(clusterDetailDTO.getClusterId())==null
                        ?0L:standbyTopicCountMap.get(clusterDetailDTO.getClusterId()));
                HaASSwitchJobDO jobDO = jobDOS.get(haClusterVO.getClusterId());
                haClusterVO.setHaStatus(jobDO != null && HaJobStatusEnum.isRunning(jobDO.getJobStatus())
                        ?HaStatusEnum.SWITCHING_CODE: HaStatusEnum.STABLE_CODE);
                ClusterDetailDTO standbyClusterDetail = clusterDetailDTOMap.get(relationDO.getStandbyClusterPhyId());
                if (standbyClusterDetail != null){
                    //备集群
                    HaClusterVO standbyCluster = new HaClusterVO();
                    BeanUtils.copyProperties(standbyClusterDetail,standbyCluster);
                    standbyCluster.setActiveTopicCount(activeTopicCountMap.get(standbyClusterDetail.getClusterId())==null
                            ?0L:activeTopicCountMap.get(standbyClusterDetail.getClusterId()));
                    standbyCluster.setStandbyTopicCount(standbyTopicCountMap.get(standbyClusterDetail.getClusterId())==null
                            ?0L:standbyTopicCountMap.get(standbyClusterDetail.getClusterId()));

                    standbyCluster.setHaASSwitchJobId(jobDO != null ? jobDO.getId() : null);
                    standbyCluster.setHaStatus(haClusterVO.getHaStatus());
                    haClusterVO.setHaClusterVO(standbyCluster);
                }
                haClusterVOS.add(haClusterVO);
            }else if(!standbyList.contains(clusterDetailDTO.getClusterId())){
                //普通集群
                HaClusterVO haClusterVO = new HaClusterVO();
                BeanUtils.copyProperties(clusterDetailDTO,haClusterVO);
                haClusterVOS.add(haClusterVO);
            }
        }
        return Result.buildSuc(haClusterVOS);
    }

    private Result<Void> modifyHaClusterConfig(ClusterDO activeClusterDO, ClusterDO standbyClusterDO, String operator){
        //更新A集群配置信息
        Result<Void> activeResult = createHAInKafka(activeClusterDO.getZookeeper(), standbyClusterDO, operator);
        if (activeResult.failed()){
            return activeResult;
        }

        //更新gateway上A集群的配置
        Result<Void> activeGatewayResult = this.createHAInKafka(configUtils.getDKafkaGatewayZK(), activeClusterDO, operator);
        if (activeGatewayResult.failed()){
            return activeGatewayResult;
        }

        //更新B集群配置信息
        Result<Void> standbyResult = this.createHAInKafka(standbyClusterDO.getZookeeper(), activeClusterDO, operator);
        if (standbyResult.failed()){
            return standbyResult;
        }
        //更新gateway上B集群的配置
        Result<Void> standbyGatewayResult = this.createHAInKafka(configUtils.getDKafkaGatewayZK(), standbyClusterDO, operator);
        if (standbyGatewayResult.failed()){
            return activeGatewayResult;
        }

        return Result.buildSuc();
    }

    private Result<Void> modifyHaTopicConfig(ClusterDO activeClusterDO, ClusterDO standbyClusterDO, String operator){
        //添加B集群拉取A集群offsets的配置信息
        Result aResult = haTopicService.activeHAInKafkaNotCheck(activeClusterDO, KafkaConstant.COORDINATOR_TOPIC_NAME,
                standbyClusterDO, KafkaConstant.COORDINATOR_TOPIC_NAME, operator);
        if (aResult.failed()){
            return aResult;
        }

        //添加A集群拉取B集群offsets的配置信息
        return haTopicService.activeHAInKafkaNotCheck(standbyClusterDO, KafkaConstant.COORDINATOR_TOPIC_NAME,
                activeClusterDO, KafkaConstant.COORDINATOR_TOPIC_NAME, operator);

    }

    private Result<Void> delClusterHaConfig(ClusterDO clusterDO, ClusterDO standbyClusterDO){
        //删除A集群同步B集群Offset配置
        ResultStatus resultStatus = HaTopicCommands.deleteHaTopicConfig(
                clusterDO,
                KafkaConstant.COORDINATOR_TOPIC_NAME,
                Arrays.asList(KafkaConstant.DIDI_HA_REMOTE_CLUSTER, KafkaConstant.DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED)
        );
        if (resultStatus.getCode() != 0){
            LOGGER.error("delete active cluster config failed! clusterId:{} standbyClusterId:{}" , clusterDO.getId(), standbyClusterDO.getId());
            return Result.buildFailure("删除主集群__consumer_offsets高可用配置失败,请重试！");
        }

        //删除A集群配置信息
        resultStatus = HaClusterCommands.coverHaClusterConfig(clusterDO.getZookeeper(), standbyClusterDO.getId(), new Properties());
        if (resultStatus.getCode() != 0){
            LOGGER.error("delete cluster config failed! clusterId:{} standbyClusterId:{}" , clusterDO.getId(), standbyClusterDO.getId());
            return Result.buildFailure("删除主集群高可用配置失败,请重试！");
        }

        //删除B集群同步A集群Offset配置
        resultStatus = HaTopicCommands.deleteHaTopicConfig(
                standbyClusterDO,
                KafkaConstant.COORDINATOR_TOPIC_NAME,
                Arrays.asList(KafkaConstant.DIDI_HA_REMOTE_CLUSTER, KafkaConstant.DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED)
        );
        if (resultStatus.getCode() != 0){
            LOGGER.error("delete standby cluster config failed! clusterId:{} standbyClusterId:{}" , clusterDO.getId(), standbyClusterDO.getId());
        }

        //删除B集群配置信息
        resultStatus = HaClusterCommands.coverHaClusterConfig(standbyClusterDO.getZookeeper(), standbyClusterDO.getId(), new Properties());
        if (resultStatus.getCode() != 0){
            LOGGER.error("delete standby cluster config failed! clusterId:{} standbyClusterId:{}" , clusterDO.getId(), standbyClusterDO.getId());
        }

        //更新gateway中备集群配置信息
        resultStatus = HaClusterCommands.coverHaClusterConfig(configUtils.getDKafkaGatewayZK(), standbyClusterDO.getId(), new Properties());
        if (resultStatus.getCode() != 0){
            LOGGER.error("delete spare gateway config failed! clusterId:{} standbyClusterId:{}" , clusterDO.getId(), standbyClusterDO.getId());
        }

        //删除gateway中A集群配置信息
        resultStatus = HaClusterCommands.coverHaClusterConfig(configUtils.getDKafkaGatewayZK(), clusterDO.getId(), new Properties());
        if (resultStatus.getCode() != 0){
            LOGGER.error("delete host gateway config failed! clusterId:{} standbyClusterId:{}" , clusterDO.getId(), standbyClusterDO.getId());
        }

        return Result.buildSuc();
    }

    private Result delDBHaCluster(Long activeClusterPhyId, Long standbyClusterPhyId){
        LambdaQueryWrapper<HaASRelationDO> topicQueryWrapper = new LambdaQueryWrapper();
        topicQueryWrapper.eq(HaASRelationDO::getResType, HaResTypeEnum.TOPIC.getCode());
        topicQueryWrapper.eq(HaASRelationDO::getActiveClusterPhyId, activeClusterPhyId);
        List<HaASRelationDO> relationDOS = haActiveStandbyRelationDao.selectList(topicQueryWrapper);
        if (!relationDOS.isEmpty()){
            return Result.buildFrom(ResultStatus.HA_CLUSTER_DELETE_FORBIDDEN);
        }

        try {
            LambdaQueryWrapper<HaASRelationDO> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(HaASRelationDO::getActiveClusterPhyId, activeClusterPhyId);
            queryWrapper.eq(HaASRelationDO::getResType, HaResTypeEnum.CLUSTER.getCode());

            int count = haActiveStandbyRelationDao.delete(queryWrapper);
            if (count < 1){
                LOGGER.error("delete HA failed! clusterId:{} standbyClusterId:{}" , activeClusterPhyId, standbyClusterPhyId);
                return Result.buildFrom(ResultStatus.MYSQL_ERROR);
            }
        }catch (Exception e){
            LOGGER.error("delete HA failed! clusterId:{} standbyClusterId:{}" , activeClusterPhyId, standbyClusterPhyId);
            return Result.buildFrom(ResultStatus.MYSQL_ERROR);
        }
        return Result.buildSuc();
    }

    private Properties getSecurityProperties(String securityPropertiesStr){
        Properties securityProperties = new Properties();
        if (StringUtils.isBlank(securityPropertiesStr)){
            return securityProperties;
        }
        securityProperties.putAll(JsonUtils.stringToObj(securityPropertiesStr, Properties.class));
        securityProperties.put(KafkaConstant.SASL_JAAS_CONFIG, securityProperties.getProperty(KafkaConstant.SASL_JAAS_CONFIG)==null
                ?"":securityProperties.getProperty(KafkaConstant.SASL_JAAS_CONFIG).replaceAll("\"","\\\\\""));
       return securityProperties;
    }
}
