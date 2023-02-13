//package com.xiaojukeji.kafka.manager.task.dispatch.ha;
//
//import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
//import com.xiaojukeji.kafka.manager.common.bizenum.ha.job.HaJobStatusEnum;
//import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
//import com.xiaojukeji.kafka.manager.common.constant.Constant;
//import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobDTO;
//import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
//import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
//import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchJobDO;
//import com.xiaojukeji.kafka.manager.common.utils.HAUtils;
//import com.xiaojukeji.kafka.manager.common.utils.Tuple;
//import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
//import com.xiaojukeji.kafka.manager.service.biz.job.HaASSwitchJobManager;
//import com.xiaojukeji.kafka.manager.service.service.ClusterService;
//import com.xiaojukeji.kafka.manager.service.service.ConfigService;
//import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
//import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
//import com.xiaojukeji.kafka.manager.service.service.ha.HaASSwitchJobService;
//import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
//import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * 主备切换任务
// */
//@Component
//@CustomScheduled(name = "HandleHaClientNewTopic",
//        cron = "0 0/2 * * * ?",
//        threadNum = 1,
//        description = "处理HAClient的新增Topic")
//public class HandleHaClientNewTopic extends AbstractScheduledTask<ClusterDO> {
//    @Autowired
//    private ClusterService clusterService;
//
//    @Autowired
//    private HaASRelationService haASRelationService;
//
//    @Autowired
//    private TopicConnectionService topicConnectionService;
//
//    @Autowired
//    private HaASSwitchJobManager haASSwitchJobManager;
//
//    @Autowired
//    private HaASSwitchJobService haASSwitchJobService;
//
//    @Autowired
//    private ConfigService configService;
//
//    @Override
//    public List<ClusterDO> listAllTasks() {
//        return clusterService.list();
//    }
//
//    @Override
//    public void processTask(ClusterDO clusterDO) {
//        if (this.existRunningTask(clusterDO.getId())) {
//            // 存在运行中的任务
//            return;
//        }
//
//        // 获取已经建立HA的Client
//        List<HaASRelationDO> doList = haASRelationService.listAllHAFromDB(clusterDO.getId(), HaResTypeEnum.KAFKA_USER_AND_CLIENT);
//
//        // 获取已经建立HA的Topic
//        Map<String, HaASRelationDO> nameMap = haASRelationService.listAllHAFromDB(clusterDO.getId(), HaResTypeEnum.TOPIC)
//                .stream()
//                .collect(Collectors.toMap(HaASRelationDO::getActiveResName, Function.identity()));
//
//        // 新的主备集群 & 需要切换的Topic
//        Long newActiveClusterId = null;
//        Long newStandbyClusterId = null;
//        Map<String, HaASRelationDO> needSwitchTopicMap = new HashMap<>();
//
//        // 查找clientId关联的Topic列表
//        for (HaASRelationDO asRelationDO: doList) {
//            if (newActiveClusterId != null && !newActiveClusterId.equals(asRelationDO.getActiveClusterPhyId())) {
//                // 一次切换，仅能有一个主集群ID，不能有多个。不一致时，直接忽略
//                continue;
//            }
//
//            Tuple<String, String> userAndClient = HAUtils.splitKafkaUserAndClient(asRelationDO.getActiveResName());
//            if (userAndClient == null || ValidateUtils.isBlank(userAndClient.getV2())) {
//                continue;
//            }
//
//            // 获取该client对应的Topic
//            Set<String> topicNameSet = topicConnectionService.getKafkaUserAndClientIdTopicNames(
//                    new HashSet<>(Arrays.asList(asRelationDO.getActiveClusterPhyId(), asRelationDO.getStandbyClusterPhyId())),
//                    userAndClient.getV1(),
//                    userAndClient.getV2(),
//                    new Date(System.currentTimeMillis() - configService.getLongValue(ConfigConstant.HA_CONNECTION_ACTIVE_TIME_UNIT_MIN, 20L) * 60L * 1000L),
//                    new Date()
//            );
//
//            // 遍历Topic，判断主备关系是否符合预期
//            for (String topicName: topicNameSet) {
//                HaASRelationDO topicRelation = nameMap.get(topicName);
//                if (topicRelation == null
//                        || asRelationDO.getActiveClusterPhyId().equals(topicRelation.getActiveClusterPhyId())) {
//                    // Topic为空，未建立高可用，忽略该Topic
//                    // 已建立HA，且该Topic的主备信息和当前clientId一致，因此也不需要进行主备切换
//                    continue;
//                }
//
//                // 主备信息不一致时，进行主备切换
//                if (needSwitchTopicMap.isEmpty()) {
//                    newActiveClusterId = asRelationDO.getActiveClusterPhyId();
//                    newStandbyClusterId = asRelationDO.getStandbyClusterPhyId();
//                }
//
//                needSwitchTopicMap.put(topicName, topicRelation);
//            }
//        }
//
//        if (this.existRunningTask(clusterDO.getId())) {
//            // 再次判断是否存在运行中的任务
//            return;
//        }
//
//        // 创建任务
//        haASSwitchJobManager.createJob(
//                this.convert2ASSwitchJobDTO(newActiveClusterId, newStandbyClusterId, new ArrayList<>(needSwitchTopicMap.values())),
//                Constant.DEFAULT_USER_NAME
//        );
//    }
//
//    private ASSwitchJobDTO convert2ASSwitchJobDTO(Long newActiveClusterId, Long newStandbyClusterId, List<HaASRelationDO> doList) {
//        ASSwitchJobDTO dto = new ASSwitchJobDTO();
//        dto.setAll(false);
//        dto.setMustContainAllKafkaUserTopics(false);
//        dto.setActiveClusterPhyId(newActiveClusterId);
//        dto.setStandbyClusterPhyId(newStandbyClusterId);
//        dto.setTopicNameList(doList.stream().map(elem -> elem.getActiveResName()).collect(Collectors.toList()));
//        dto.setKafkaUserAndClientIdList(new ArrayList<>()); // clientId 或者 kafkaUser 已切换好，所以后台任务不需要执行该步骤
//
//        return dto;
//    }
//
//    private boolean existRunningTask(Long clusterPhyId) {
//        Map<Long/*集群ID*/, HaASSwitchJobDO> jobMap = haASSwitchJobService.listClusterLatestJobs();
//
//        HaASSwitchJobDO jobDO = jobMap.remove(clusterPhyId);
//        if (jobDO == null || !HaJobStatusEnum.isRunning(jobDO.getJobStatus())) {
//            return false;
//        }
//
//        return true;
//    }
//}
