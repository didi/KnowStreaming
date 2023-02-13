package com.xiaojukeji.kafka.manager.service.biz.ha.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.MsgConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.TopicOperationResult;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.HaSwitchTopic;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.KafkaUserAndClientDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.topic.HaTopicRelationDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;
import com.xiaojukeji.kafka.manager.common.utils.BackoffUtils;
import com.xiaojukeji.kafka.manager.common.utils.ConvertUtil;
import com.xiaojukeji.kafka.manager.common.utils.HAUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaTopicManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JobLogService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaKafkaUserService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaTopicService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class HaTopicManagerImpl implements HaTopicManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaTopicManagerImpl.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private HaTopicService haTopicService;

    @Autowired
    private HaKafkaUserService haKafkaUserService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private AdminService adminService;

    @Override
    public Result<HaSwitchTopic> switchHaWithCanRetry(Long newActiveClusterPhyId,
                                                      Long newStandbyClusterPhyId,
                                                      List<String> switchTopicNameList,
                                                      List<KafkaUserAndClientDTO> kafkaUserAndClientIdList,
                                                      boolean focus,
                                                      boolean firstTriggerExecute,
                                                      JobLogDO switchLogTemplate,
                                                      String operator) {
        LOGGER.info(
                "method=switchHaWithCanRetry||newActiveClusterPhyId={}||newStandbyClusterPhyId={}||switchTopicNameList={}||focus={}||operator={}",
                newActiveClusterPhyId, newStandbyClusterPhyId, ConvertUtil.obj2Json(switchTopicNameList), focus, operator
        );

        // 1、获取集群
        ClusterDO newActiveClusterPhyDO = clusterService.getById(newActiveClusterPhyId);
        if (ValidateUtils.isNull(newActiveClusterPhyDO)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getClusterPhyNotExist(newActiveClusterPhyId));
        }

        ClusterDO newStandbyClusterPhyDO = clusterService.getById(newStandbyClusterPhyId);
        if (ValidateUtils.isNull(newStandbyClusterPhyDO)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getClusterPhyNotExist(newStandbyClusterPhyId));
        }

        // 2、进行参数检查
        Result<List<HaASRelationDO>> doListResult = this.checkParamAndGetASRelation(newActiveClusterPhyId, newStandbyClusterPhyId, switchTopicNameList);
        if (doListResult.failed()) {
            LOGGER.error(
                    "method=switchHaWithCanRetry||newActiveClusterPhyId={}||newStandbyClusterPhyId={}||switchTopicNameList={}||paramErrResult={}||operator={}",
                    newActiveClusterPhyId, newStandbyClusterPhyId, ConvertUtil.obj2Json(switchTopicNameList), doListResult, operator
            );

            return Result.buildFromIgnoreData(doListResult);
        }
        List<HaASRelationDO> doList = doListResult.getData();

        // 3、如果是第一次触发执行，且状态是stable，则修改状态
        for (HaASRelationDO relationDO: doList) {
            if (firstTriggerExecute && relationDO.getStatus().equals(HaStatusEnum.STABLE_CODE)) {
                relationDO.setStatus(HaStatusEnum.SWITCHING_PREPARE_CODE);
                haASRelationService.updateRelationStatus(relationDO.getId(), HaStatusEnum.SWITCHING_PREPARE_CODE);
            }
        }

        // 4、进行切换预处理
        HaSwitchTopic switchTopic = this.prepareSwitching(newStandbyClusterPhyDO, doList, kafkaUserAndClientIdList, focus, switchLogTemplate);

        // 5、直接等待10秒，使得相关数据有机会同步完成
        BackoffUtils.backoff(10000);

        // 6、检查数据同步情况
        for (HaASRelationDO relationDO: doList) {
            switchTopic.addHaSwitchTopic(this.checkTopicInSync(newActiveClusterPhyDO, newStandbyClusterPhyDO, relationDO, focus, switchLogTemplate));
        }

        // 7、删除旧的备Topic的同步配置
        for (HaASRelationDO relationDO: doList) {
            switchTopic.addHaSwitchTopic(this.oldStandbyTopicDelFetchConfig(newActiveClusterPhyDO, newStandbyClusterPhyDO, relationDO, focus, switchLogTemplate, operator));
        }

        // 8、增加新的备Topic的同步配置，
        switchTopic.addHaSwitchTopic(this.newStandbyTopicAddFetchConfig(newActiveClusterPhyDO, newStandbyClusterPhyDO, doList, focus, switchLogTemplate, operator));

        // 9、进行切换收尾
        switchTopic.addHaSwitchTopic(this.closeoutSwitching(
                newActiveClusterPhyDO,
                newStandbyClusterPhyDO,
                configUtils.getDKafkaGatewayZK(),
                doList,
                kafkaUserAndClientIdList,
                focus,
                switchLogTemplate
        ));

        // 10、状态结果汇总记录
        doList.forEach(elem -> switchTopic.addActiveTopicStatus(elem.getActiveResName(), elem.getStatus()));

        // 11、日志记录并返回
        LOGGER.info(
                "method=switchHaWithCanRetry||newActiveClusterPhyId={}||newStandbyClusterPhyId={}||switchTopicNameList={}||switchResult={}||operator={}",
                newActiveClusterPhyId, newStandbyClusterPhyId, ConvertUtil.obj2Json(switchTopicNameList), switchTopic, operator
        );

        if (switchTopic.isFinished()) {
            // 全都切换完成，则更新HA信息
            try {
                updateHAClient(newActiveClusterPhyId, newStandbyClusterPhyId, kafkaUserAndClientIdList);
            } catch (Exception e) {
                LOGGER.error(
                        "method=switchHaWithCanRetry||newActiveClusterPhyId={}||newStandbyClusterPhyId={}||kafkaUserAndClientIdList={}||operator={}||errMsg=exception",
                        newActiveClusterPhyId, newStandbyClusterPhyId, ConvertUtil.obj2Json(kafkaUserAndClientIdList), operator, e
                );
            }
        }

        return Result.buildSuc(switchTopic);
    }

    @Override
    public Result<List<TopicOperationResult>> batchCreateHaTopic(HaTopicRelationDTO dto, String operator) {
        List<HaASRelationDO> relationDOS = haASRelationService.listAllHAFromDB(dto.getActiveClusterId(), dto.getStandbyClusterId(), HaResTypeEnum.CLUSTER);
        if (relationDOS.isEmpty()){
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, "集群高可用关系未建立");
        }

        //获取主集群已有的高可用topic
        Map<String, Integer> haRelationMap = haTopicService.getRelation(dto.getActiveClusterId());
        List<String> topicNames = dto.getTopicNames();
        if (dto.getAll()){
            topicNames = topicManagerService.getByClusterId(dto.getActiveClusterId())
                    .stream()
                    .filter(topicDO -> !topicDO.getTopicName().startsWith("__"))//过滤掉kafka自带topic
                    .filter(topicDO -> !haRelationMap.keySet().contains(topicDO.getTopicName()))//过滤调已成为高可用topic的topic
                    .filter(topicDO -> PhysicalClusterMetadataManager.isTopicExist(dto.getActiveClusterId(), topicDO.getTopicName()))
                    .map(TopicDO::getTopicName)
                    .collect(Collectors.toList());

        }

        List<TopicOperationResult> operationResultList = new ArrayList<>();
        topicNames.forEach(topicName->{
            Result<Void> rv = haTopicService.createHA(dto.getActiveClusterId(), dto.getStandbyClusterId(),topicName, operator);
            operationResultList.add(TopicOperationResult.buildFrom(dto.getActiveClusterId(), topicName, rv));
        });

        return Result.buildSuc(operationResultList);
    }

    @Override
    public Result<List<TopicOperationResult>> batchRemoveHaTopic(HaTopicRelationDTO dto, String operator) {
        List<HaASRelationDO> relationDOS = haASRelationService.listAllHAFromDB(dto.getActiveClusterId(), dto.getStandbyClusterId(), HaResTypeEnum.CLUSTER);
        if (relationDOS.isEmpty()){
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, "集群高可用关系未建立");
        }

        List<TopicOperationResult> operationResultList = new ArrayList<>();
        for(String topicName : dto.getTopicNames()){
            HaASRelationDO relationDO = haASRelationService.getHAFromDB(
                    dto.getActiveClusterId(),
                    topicName,
                    HaResTypeEnum.TOPIC
            );
            if (relationDO == null) {
                return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, "主备关系不存在");
            }

            Result<Void> rv = haTopicService.deleteHA(relationDO.getActiveClusterPhyId(), relationDO.getStandbyClusterPhyId(), topicName, operator);

            //删除备topic资源
            if (dto.getRetainStandbyResource() != null && !dto.getRetainStandbyResource()) {
                ResultStatus statusEnum = adminService.deleteTopic(
                        PhysicalClusterMetadataManager.getClusterFromCache(dto.getStandbyClusterId()),
                        topicName,
                        operator);
                if (statusEnum.getCode() != ResultStatus.SUCCESS.getCode()){
                    LOGGER.error(
                            "method=batchRemoveHaTopic||activeClusterPhyId={}||standbyClusterPhyId={}||topicName={}||result={}||msg=delete standby topic failed.",
                            dto.getActiveClusterId(), dto.getStandbyClusterId(), topicName, statusEnum
                    );
                }
            }
            operationResultList.add(TopicOperationResult.buildFrom(dto.getActiveClusterId(), topicName, rv));
        }

        return Result.buildSuc(operationResultList);
    }

    /**************************************************** private method ****************************************************/

    private void saveLogs(JobLogDO switchLogTemplate, String content) {
        jobLogService.addLogAndIgnoreException(switchLogTemplate.setAndCopyNew(new Date(), content));
    }

    private HaSwitchTopic prepareSwitching(ClusterDO oldActiveClusterPhyDO,
                                           List<HaASRelationDO> doList,
                                           List<KafkaUserAndClientDTO> clientDTOList,
                                           boolean focus,
                                           JobLogDO switchLogTemplate) {
        HaSwitchTopic haSwitchTopic = new HaSwitchTopic(true);

        boolean allSuccess = true; // 所有都成功

        // 存在prepare状态的，则就需要进行预处理操作
        boolean needDOIt = doList.stream().filter(elem -> elem.getStatus().equals(HaStatusEnum.SWITCHING_PREPARE_CODE)).count() > 0;
        if (!needDOIt) {
            // 不需要做
            return haSwitchTopic;
        }

        // 暂停kafkaUser HA
        for (KafkaUserAndClientDTO dto: clientDTOList) {
            Result<Void> rv = haKafkaUserService.setNoneHAInKafka(oldActiveClusterPhyDO.getZookeeper(), HAUtils.mergeKafkaUserAndClient(dto.getKafkaUser(), dto.getClientId()));
            if (rv.failed() && !focus) {
                haSwitchTopic.setFinished(false);

                this.saveLogs(switchLogTemplate, String.format("%s:\t失败，1分钟后再进行重试", HaStatusEnum.SWITCHING_PREPARE.getMsg(oldActiveClusterPhyDO.getClusterName())));
                return haSwitchTopic;
            } else if (rv.failed() && focus) {
                allSuccess = false;
            }
        }

        // 修改Topic主备状态
        doList.forEach(elem -> {
            elem.setStatus(HaStatusEnum.SWITCHING_WAITING_IN_SYNC_CODE);
            haASRelationService.updateRelationStatus(elem.getId(), HaStatusEnum.SWITCHING_WAITING_IN_SYNC_CODE);
        });

        this.saveLogs(switchLogTemplate, String.format("%s:\t%s", HaStatusEnum.SWITCHING_PREPARE.getMsg(oldActiveClusterPhyDO.getClusterName()), allSuccess? "成功": "存在失败，但进行强制执行，跳过该操作"));

        haSwitchTopic.setFinished(true);
        return haSwitchTopic;
    }

    /**
     * 等待主备Topic同步
     */
    private HaSwitchTopic checkTopicInSync(ClusterDO newActiveClusterPhyDO, ClusterDO newStandbyClusterPhyDO, HaASRelationDO relationDO, boolean focus, JobLogDO switchLogTemplate) {
        HaSwitchTopic haSwitchTopic = new HaSwitchTopic(true);
        if (!relationDO.getStatus().equals(HaStatusEnum.SWITCHING_WAITING_IN_SYNC_CODE)) {
            // 状态错误，直接略过
            haSwitchTopic.setFinished(true);
            return haSwitchTopic;
        }

        if (focus) {
            // 无需等待inSync

            // 修改Topic主备状态
            relationDO.setStatus(HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE);
            haASRelationService.updateRelationStatus(relationDO.getId(), HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE);

            haSwitchTopic.setFinished(true);
            this.saveLogs(switchLogTemplate, String.format(
                    "%s:\tTopic:[%s] 强制切换，跳过等待主备同步完成，直接进入下一步",
                    HaStatusEnum.SWITCHING_WAITING_IN_SYNC.getMsg(newActiveClusterPhyDO.getClusterName()),
                    relationDO.getActiveResName()
            ));
            return haSwitchTopic;
        }

        Result<Long> lagResult = haTopicService.getStandbyTopicFetchLag(newStandbyClusterPhyDO.getId(), relationDO.getStandbyResName());
        if (lagResult.failed()) {
            // 获取Lag信息失败
            this.saveLogs(switchLogTemplate, String.format(
                    "%s:\tTopic:[%s] 获取同步的Lag信息失败，1分钟后再检查是否主备同步完成",
                    HaStatusEnum.SWITCHING_WAITING_IN_SYNC.getMsg(newActiveClusterPhyDO.getClusterName()),
                    relationDO.getActiveResName()
            ));
            haSwitchTopic.setFinished(false);
            return haSwitchTopic;
        }

        if (lagResult.getData().longValue() > 0) {
            this.saveLogs(switchLogTemplate, String.format(
                    "%s:\tTopic:[%s] 还存在 %d 条数据未同步完成，1分钟后再检查是否主备同步完成",
                    HaStatusEnum.SWITCHING_WAITING_IN_SYNC.getMsg(newActiveClusterPhyDO.getClusterName()),
                    relationDO.getActiveResName(),
                    lagResult.getData()
            ));

            haSwitchTopic.setFinished(false);
            return haSwitchTopic;
        }

        // 修改Topic主备状态
        relationDO.setStatus(HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE);
        haASRelationService.updateRelationStatus(relationDO.getId(), HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE);

        haSwitchTopic.setFinished(true);
        this.saveLogs(switchLogTemplate, String.format(
                "%s:\tTopic:[%s] 主备同步完成",
                HaStatusEnum.SWITCHING_WAITING_IN_SYNC.getMsg(newActiveClusterPhyDO.getClusterName()),
                relationDO.getActiveResName()
        ));
        return haSwitchTopic;
    }

    /**
     * 备Topic删除拉取主Topic数据的配置
     */
    private HaSwitchTopic oldStandbyTopicDelFetchConfig(ClusterDO newActiveClusterPhyDO, ClusterDO newStandbyClusterPhyDO, HaASRelationDO relationDO, boolean focus, JobLogDO switchLogTemplate, String operator) {
        HaSwitchTopic haSwitchTopic = new HaSwitchTopic(true);
        if (!relationDO.getStatus().equals(HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH_CODE)) {
            // 状态不对
            haSwitchTopic.setFinished(true);
            return haSwitchTopic;
        }

        Result<Void> rv = haTopicService.stopHAInKafka(
                newActiveClusterPhyDO, relationDO.getStandbyResName(), // 旧的备
                operator
        );
        if (rv.failed() && !focus) {
            this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 失败，1分钟后再进行重试", HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH.getMsg(newActiveClusterPhyDO.getClusterName()), relationDO.getActiveResName()));
            haSwitchTopic.setFinished(false);
            return haSwitchTopic;
        } else if (rv.failed() && focus) {
            this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 失败，但进行强制执行，跳过该操作", HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH.getMsg(newActiveClusterPhyDO.getClusterName()), relationDO.getActiveResName()));
        } else {
            this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 成功", HaStatusEnum.SWITCHING_CLOSE_OLD_STANDBY_TOPIC_FETCH.getMsg(newActiveClusterPhyDO.getClusterName()), relationDO.getActiveResName()));
        }

        // 修改Topic主备状态
        relationDO.setStatus(HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH_CODE);
        haASRelationService.updateRelationStatus(relationDO.getId(), HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH_CODE);

        haSwitchTopic.setFinished(true);
        return haSwitchTopic;
    }

    /**
     * 新的备Topic，创建拉取新主Topic数据的配置
     */
    private HaSwitchTopic newStandbyTopicAddFetchConfig(ClusterDO newActiveClusterPhyDO,
                                                        ClusterDO newStandbyClusterPhyDO,
                                                        List<HaASRelationDO> doList,
                                                        boolean focus,
                                                        JobLogDO switchLogTemplate,
                                                        String operator) {
        boolean forceAndFailed = false;
        for (HaASRelationDO relationDO: doList) {
            if (!relationDO.getStatus().equals(HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH_CODE)) {
                // 状态不对
                continue;
            }

            Result<Void> rv = null;
            if (!forceAndFailed) {
                // 非 强制切换并且失败了
                rv = haTopicService.activeHAInKafka(
                        newActiveClusterPhyDO, relationDO.getStandbyResName(),
                        newStandbyClusterPhyDO, relationDO.getStandbyResName(),
                        operator
                );
            }

            if (forceAndFailed) {
                // 强制切换并且失败了，记录该日志
                this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 失败，但因为是强制执行且强制执行时依旧出现操作失败，因此直接跳过该操作", HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH.getMsg(newStandbyClusterPhyDO.getClusterName()), relationDO.getActiveResName()));

            } else if (rv.failed() && !focus) {
                // 如果失败了，并且非强制切换，则直接返回
                this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 失败，1分钟后再进行重试", HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH.getMsg(newStandbyClusterPhyDO.getClusterName()), relationDO.getActiveResName()));

                return new HaSwitchTopic(false);
            } else if (rv.failed() && focus) {
                // 如果失败了，但是是强制切换，则记录日志并继续
                this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 失败，但因为是强制执行，因此跳过该操作", HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH.getMsg(newStandbyClusterPhyDO.getClusterName()), relationDO.getActiveResName()));

                forceAndFailed = true;
            } else {
                // 记录成功日志
                this.saveLogs(switchLogTemplate, String.format("%s:\tTopic:[%s] 成功", HaStatusEnum.SWITCHING_OPEN_NEW_STANDBY_TOPIC_FETCH.getMsg(newStandbyClusterPhyDO.getClusterName()), relationDO.getActiveResName()));
            }

            // 修改Topic主备状态
            relationDO.setStatus(HaStatusEnum.SWITCHING_CLOSEOUT_CODE);
            haASRelationService.updateRelationStatus(relationDO.getId(), HaStatusEnum.SWITCHING_CLOSEOUT_CODE);
        }

        return new HaSwitchTopic(true);
    }

    /**
     * 切换收尾
     * 1、原先的主集群-修改user的active集群，指向新的主集群
     * 2、原先的备集群-修改user的active集群，指向新的主集群
     * 3、网关-修改user的active集群，指向新的主集群
     */
    private HaSwitchTopic closeoutSwitching(ClusterDO newActiveClusterPhyDO,
                                            ClusterDO newStandbyClusterPhyDO,
                                            String gatewayZK,
                                            List<HaASRelationDO> doList,
                                            List<KafkaUserAndClientDTO> kafkaUserAndClientDTOList,
                                            boolean focus,
                                            JobLogDO switchLogTemplate) {
        HaSwitchTopic haSwitchTopic = new HaSwitchTopic(true);

        boolean needDOIt = doList.stream().filter(elem -> elem.getStatus().equals(HaStatusEnum.SWITCHING_CLOSEOUT_CODE)).count() > 0;
        if (!needDOIt) {
            // 不需要做任何事情
            return haSwitchTopic;
        }

        boolean allSuccess = true;
        boolean forceAndNewStandbyFailed = false; // 强制切换，但是新的备依旧操作失败

        for (KafkaUserAndClientDTO dto: kafkaUserAndClientDTOList) {
            String zkNodeName = HAUtils.mergeKafkaUserAndClient(dto.getKafkaUser(), dto.getClientId());

            // 操作新的主集群
            Result<Void> rv = haKafkaUserService.activeHAInKafka(newActiveClusterPhyDO.getZookeeper(), newActiveClusterPhyDO.getId(), zkNodeName);
            if (rv.failed() && !focus) {
                haSwitchTopic.setFinished(false);
                this.saveLogs(switchLogTemplate, String.format("%s:\t失败，1分钟后再进行重试", HaStatusEnum.SWITCHING_CLOSEOUT.getMsg(newActiveClusterPhyDO.getClusterName())));
                return haSwitchTopic;
            } else if (rv.failed() && focus) {
                allSuccess = false;
            }

            // 操作新的备集群，如果出现错误，则下次就不再进行操作ZK。新的备的Topic不是那么重要，因此这里允许出现跳过
            rv = null;
            if (!forceAndNewStandbyFailed) {
                // 如果对备集群的操作过程中，出现了失败，则直接跳过
                rv = haKafkaUserService.activeHAInKafka(newStandbyClusterPhyDO.getZookeeper(), newActiveClusterPhyDO.getId(), zkNodeName);
            }

            if (rv != null && rv.failed() && !focus) {
                haSwitchTopic.setFinished(false);
                this.saveLogs(switchLogTemplate, String.format("%s:\t失败，1分钟后再进行重试", HaStatusEnum.SWITCHING_CLOSEOUT.getMsg(newActiveClusterPhyDO.getClusterName())));
                return haSwitchTopic;
            } else if (rv != null && rv.failed() && focus) {
                allSuccess = false;
                forceAndNewStandbyFailed = true;
            }

            // 操作网关
            rv = haKafkaUserService.activeHAInKafka(gatewayZK, newActiveClusterPhyDO.getId(), zkNodeName);
            if (rv.failed() && !focus) {
                haSwitchTopic.setFinished(false);
                this.saveLogs(switchLogTemplate, String.format("%s:\t失败，1分钟后再进行重试", HaStatusEnum.SWITCHING_CLOSEOUT.getMsg(newActiveClusterPhyDO.getClusterName())));
                return haSwitchTopic;
            } else if (rv.failed() && focus) {
                allSuccess = false;
            }
        }

        // 修改Topic主备信息
        doList.forEach(elem -> {
            HaASRelationDO newHaASRelationDO = new HaASRelationDO(
                    newActiveClusterPhyDO.getId(), elem.getActiveResName(),
                    newStandbyClusterPhyDO.getId(), elem.getStandbyResName(),
                    HaResTypeEnum.TOPIC.getCode(),
                    HaStatusEnum.STABLE_CODE
            );
            newHaASRelationDO.setId(elem.getId());

            haASRelationService.updateById(newHaASRelationDO);
        });

        this.saveLogs(switchLogTemplate, String.format("%s:\t%s", HaStatusEnum.SWITCHING_CLOSEOUT.getMsg(newActiveClusterPhyDO.getClusterName()), allSuccess? "成功": "存在失败，但进行强制执行，跳过该操作"));
        return haSwitchTopic;
    }

    /**
     * 检查参数，并获取主备关系信息
     */
    private Result<List<HaASRelationDO>> checkParamAndGetASRelation(Long activeClusterPhyId, Long standbyClusterPhyId, List<String> switchTopicNameList) {
        List<HaASRelationDO> doList = new ArrayList<>();
        for (String topicName: switchTopicNameList) {
            Result<HaASRelationDO> doResult = this.checkParamAndGetASRelation(activeClusterPhyId, standbyClusterPhyId, topicName);
            if (doResult.failed()) {
                return Result.buildFromIgnoreData(doResult);
            }

            doList.add(doResult.getData());
        }

        return Result.buildSuc(doList);
    }

    /**
     * 检查参数，并获取主备关系信息
     */
    private Result<HaASRelationDO> checkParamAndGetASRelation(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName) {
        // newActiveTopic必须存在，新的备Topic可以不存在
        if (!PhysicalClusterMetadataManager.isTopicExist(activeClusterPhyId, topicName)) {
            return Result.buildFromRSAndMsg(
                    ResultStatus.RESOURCE_NOT_EXIST,
                    String.format("新的主集群ID:[%d]-Topic:[%s] 不存在", activeClusterPhyId, topicName)
            );
        }

        // 查询主备关系是否存在
        HaASRelationDO relationDO = haASRelationService.getSpecifiedHAFromDB(
                standbyClusterPhyId,
                topicName,
                activeClusterPhyId,
                topicName,
                HaResTypeEnum.TOPIC
        );
        if (relationDO == null) {
            // 查询切换后的关系是否存在，如果已经存在，则后续会重新建立一遍
            relationDO = haASRelationService.getSpecifiedHAFromDB(
                    activeClusterPhyId,
                    topicName,
                    standbyClusterPhyId,
                    topicName,
                    HaResTypeEnum.TOPIC
            );
        }

        if (relationDO == null) {
            // 主备关系不存在
            return Result.buildFromRSAndMsg(
                    ResultStatus.RESOURCE_NOT_EXIST,
                    String.format("主集群ID:[%d]-Topic:[%s], 备集群ID:[%d] Topic:[%s] 的主备关系不存在，因此无法切换", activeClusterPhyId, topicName, standbyClusterPhyId, topicName)
            );
        }

        return Result.buildSuc(relationDO);
    }

    private void updateHAClient(Long newActiveClusterPhyId,
                                Long newStandbyClusterPhyId,
                                List<KafkaUserAndClientDTO> kafkaUserAndClientIdList) {
        if (ValidateUtils.isEmptyList(kafkaUserAndClientIdList)) {
            return;
        }

        List<HaASRelationDO> doList = haASRelationService.listAllHAFromDB(newActiveClusterPhyId, HaResTypeEnum.KAFKA_USER_AND_CLIENT);

        Map<String, HaASRelationDO> resNameMap = new HashMap<>();
        doList.forEach(elem -> resNameMap.put(elem.getActiveResName(), elem));

        for (KafkaUserAndClientDTO dto: kafkaUserAndClientIdList) {
            if (ValidateUtils.isBlank(dto.getClientId())) {
                continue;
            }
            String resName = HAUtils.mergeKafkaUserAndClient(dto.getKafkaUser(), dto.getClientId());

            HaASRelationDO newDO = new HaASRelationDO(
                    newActiveClusterPhyId,
                    resName,
                    newStandbyClusterPhyId,
                    resName,
                    HaResTypeEnum.KAFKA_USER_AND_CLIENT.getCode(),
                    HaStatusEnum.STABLE_CODE
            );

            HaASRelationDO oldDO = resNameMap.remove(resName);
            if (oldDO != null) {
                newDO.setId(oldDO.getId());
                haASRelationService.updateById(newDO);
            } else {
                try {
                    haASRelationService.addHAToDB(newDO);
                } catch (DuplicateKeyException dke) {
                    // ignore
                }
            }
        }
    }
}
