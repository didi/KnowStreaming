package com.xiaojukeji.kafka.manager.service.biz.job.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.JobLogBizTypEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TaskActionEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.job.HaJobStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.MsgConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.HaSwitchTopic;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaJobDetail;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaJobState;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaSubJobExtendData;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchJobDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchSubJobDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.job.HaJobDetailVO;
import com.xiaojukeji.kafka.manager.common.utils.*;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaAppManager;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaTopicManager;
import com.xiaojukeji.kafka.manager.service.biz.job.HaASSwitchJobManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.JobLogService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASSwitchJobService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaTopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class HaASSwitchJobManagerImpl implements HaASSwitchJobManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaASSwitchJobManagerImpl.class);

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Autowired
    private HaASSwitchJobService haASSwitchJobService;

    @Autowired
    private HaTopicManager haTopicManager;

    @Autowired
    private HaTopicService haTopicService;

    @Autowired
    private HaAppManager haAppManager;

    private static final Long BACK_OFF_TIME = 3000L;

    private static final FutureUtil<Void> asyncExecuteJob = FutureUtil.init(
            "HaASSwitchJobManager",
            10,
            10,
            5000
    );

    @Override
    public Result<Long> createJob(ASSwitchJobDTO dto, String operator) {
        LOGGER.info("method=createJob||activeClusterPhyId={}||switchTopicParam={}||operator={}", dto.getActiveClusterPhyId(), ConvertUtil.obj2Json(dto), operator);

        // 1、检查参数是否合法，并获取需要执行主备切换的Topics
        Result<Set<String>> haTopicSetResult = this.checkParamLegalAndGetNeedSwitchHaTopics(dto);
        if (haTopicSetResult.failed()) {
            // 检查失败，则直接返回
            return Result.buildFromIgnoreData(haTopicSetResult);
        }

        LOGGER.info("method=createJob||activeClusterPhyId={}||switchTopics={}||operator={}", dto.getActiveClusterPhyId(), ConvertUtil.obj2Json(haTopicSetResult.getData()), operator);

//        // 2、查看是否将KafkaUser关联的Topic都涵盖了
//        if (dto.getMustContainAllKafkaUserTopics() != null
//                && dto.getMustContainAllKafkaUserTopics()
//                && (dto.getAll() == null || !dto.getAll())
//                && !haAppManager.isContainAllRelateAppTopics(dto.getActiveClusterPhyId(), dto.getTopicNameList())) {
//            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FORBIDDEN, "存在KafkaUser关联的Topic未选中");
//        }

        // 3、创建任务
        Result<Long> longResult = haASSwitchJobService.createJob(
                dto.getActiveClusterPhyId(),
                dto.getStandbyClusterPhyId(),
                new ArrayList<>(haTopicSetResult.getData()),
                dto.getKafkaUserAndClientIdList(),
                operator
        );
        if (longResult.failed()) {
            // 创建失败
            return longResult;
        }

        LOGGER.info("method=createJob||activeClusterPhyId={}||jobId={}||operator={}||msg=create-job success", dto.getActiveClusterPhyId(), longResult.getData(), operator);

        // 4、为了加快执行效率，这里在创建完成任务之后，会直接异步执行HA切换任务
        asyncExecuteJob.directSubmitTask(
                () -> {
                    BackoffUtils.backoff(BACK_OFF_TIME);

                    this.executeJob(longResult.getData(), false, true);

                    // 更新扩展数据
                    this.flushExtendData(longResult.getData());
                }
        );

        // 5、返回结果
        return longResult;
    }

    @Override
    public Result<Void> executeJob(Long jobId, boolean focus, boolean firstTriggerExecute) {
        LOGGER.info("method=executeJob||jobId={}||msg=execute job start", jobId);

        // 查询job
        HaASSwitchJobDO jobDO = haASSwitchJobService.getJobById(jobId);
        if (jobDO == null) {
            LOGGER.warn("method=executeJob||jobId={}||msg=job not exist", jobId);
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, String.format("jobId:[%d] 不存在", jobId));
        }

        // 检查job状态
        if (!HaJobStatusEnum.isRunning(jobDO.getJobStatus())) {
            LOGGER.warn("method=executeJob||jobId={}||jobStatus={}||msg=job status illegal", jobId, HaJobStatusEnum.valueOfStatus(jobDO.getJobStatus()));
            return this.buildActionForbidden(jobId, jobDO.getJobStatus());
        }

        // 查询子job列表
        List<HaASSwitchSubJobDO> subJobDOList = haASSwitchJobService.listSubJobsById(jobId);
        if (ValidateUtils.isEmptyList(subJobDOList)) {
            // 无子任务，则设置任务状态为成功
            haASSwitchJobService.updateJobStatus(jobId, HaJobStatusEnum.SUCCESS.getStatus());
            return Result.buildSuc();
        }

        Set<Integer> statusSet = new HashSet<>();
        subJobDOList.forEach(elem -> statusSet.add(elem.getJobStatus()));
        if (statusSet.size() == 1 && statusSet.contains(HaJobStatusEnum.SUCCESS.getStatus())) {
            // 无子任务，则设置任务状态为成功
            haASSwitchJobService.updateJobStatus(jobId, HaJobStatusEnum.SUCCESS.getStatus());
            return Result.buildSuc();
        }

        if (firstTriggerExecute) {
            this.saveLogs(jobDO.getId(), "主备切换开始...");
            this.saveLogs(jobDO.getId(), "如果主备集群或网关的ZK存在问题，则可能会出现1分钟左右日志不刷新的情况");
        }

        // 进行主备切换
        Result<HaSwitchTopic> haSwitchTopicResult = haTopicManager.switchHaWithCanRetry(
                jobDO.getActiveClusterPhyId(),
                jobDO.getStandbyClusterPhyId(),
                subJobDOList.stream().map(elem -> elem.getActiveResName()).collect(Collectors.toList()),
                jobDO.getExtendRawData(),
                focus,
                firstTriggerExecute,
                new JobLogDO(JobLogBizTypEnum.HA_SWITCH_JOB_LOG.getCode(), String.valueOf(jobId)),
                jobDO.getOperator()
        );

        if (haSwitchTopicResult.failed()) {
            // 出现错误
            LOGGER.error("method=executeJob||jobId={}||executeResult={}||msg=execute job failed", jobId, haSwitchTopicResult);
            return Result.buildFromIgnoreData(haSwitchTopicResult);
        }


        // 执行结果
        HaSwitchTopic haSwitchTopic = haSwitchTopicResult.getData();
        Long timeoutUnitSec = this.getTimeoutUnitSecConfig(jobDO.getActiveClusterPhyId());

        // 存储日志
        if (haSwitchTopic.isFinished()) {
            this.saveLogs(jobDO.getId(), "主备切换完成.");
        }

        // 更新状态
        for (HaASSwitchSubJobDO subJobDO: subJobDOList) {
            if (haSwitchTopic.isActiveTopicSwitchFinished(subJobDO.getActiveResName()) || haSwitchTopic.isFinished()) {
                // 执行完成
                haASSwitchJobService.updateSubJobStatus(subJobDO.getId(), HaJobStatusEnum.SUCCESS.getStatus());
            } else if (runningInTimeout(subJobDO.getCreateTime().getTime(), timeoutUnitSec)) {
                // 超时运行中
                haASSwitchJobService.updateSubJobStatus(subJobDO.getId(), HaJobStatusEnum.RUNNING_IN_TIMEOUT.getStatus());
            }
        }

        if (haSwitchTopic.isFinished()) {
            // 任务执行完成
            LOGGER.info("method=executeJob||jobId={}||executeResult={}||msg=execute job success", jobId, haSwitchTopicResult);

            // 更新状态
            haASSwitchJobService.updateJobStatus(jobId, HaJobStatusEnum.SUCCESS.getStatus());
        } else {
            LOGGER.info("method=executeJob||jobId={}||executeResult={}||msg=execute job not finished", jobId, haSwitchTopicResult);
        }

        // 返回结果
        return Result.buildSuc();
    }

    @Override
    public Result<HaJobState> jobState(Long jobId) {
        List<HaASSwitchSubJobDO> doList = haASSwitchJobService.listSubJobsById(jobId);
        if (ValidateUtils.isEmptyList(doList)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, String.format("jobId:[%d] 不存在", jobId));
        }

        if (System.currentTimeMillis() - doList.get(0).getCreateTime().getTime() <= (BACK_OFF_TIME.longValue() * 2)) {
            // 进度0
            return Result.buildSuc(new HaJobState(doList.size(), 0));
        }

        // 这里会假设主备Topic的名称是一样的
        Map<String, Integer> progressMap = new HashMap<>();
        haASRelationService.listAllHAFromDB(doList.get(0).getActiveClusterPhyId(), HaResTypeEnum.TOPIC).stream().forEach(
                elem -> progressMap.put(elem.getActiveResName(), elem.getStatus())
        );

        HaJobState haJobState = new HaJobState(
                doList.stream().map(elem -> elem.getJobStatus()).collect(Collectors.toList()),
                0
        );

        // 计算细致的进度信息
        Integer progress = 0;
        for (HaASSwitchSubJobDO elem: doList) {
            if (HaJobStatusEnum.isFinished(elem.getJobStatus())) {
                progress += 100;
                continue;
            }

            progress += HaStatusEnum.calProgress(progressMap.get(elem.getActiveResName()));
        }
        haJobState.setProgress(ConvertUtil.double2Int(progress * 1.0 / doList.size()));

        return Result.buildSuc(haJobState);

    }

    @Override
    public void flushExtendData(Long jobId) {
        // 因为仅仅是刷新扩展数据，因此不会对jobId等进行严格检查

        // 查询子job列表
        List<HaASSwitchSubJobDO> subJobDOList = haASSwitchJobService.listSubJobsById(jobId);
        if (ValidateUtils.isEmptyList(subJobDOList)) {
            // 无任务，直接返回
            return;
        }

        for (HaASSwitchSubJobDO subJobDO: subJobDOList) {
            try {
                this.flushExtendData(subJobDO);
            } catch (Exception e) {
                LOGGER.error("method=flushExtendData||jobId={}||subJobDO={}||errMsg=exception", jobId, subJobDO, e);
            }
        }
    }

    @Override
    public Result<Void> actionJob(Long jobId, ASSwitchJobActionDTO dto) {
        if (!TaskActionEnum.FORCE.getAction().equals(dto.getAction())) {
            // 不存在，或者不支持
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "action不存在");
        }

        // 强制执行，异步执行
        this.saveLogs(jobId, "开始执行强制切换...");
        this.saveLogs(jobId, "强制切换过程中，可能出现日志1分钟不刷新情况");
        this.saveLogs(jobId, "强制切换过程中，因可能与正常切换任务同时执行，因此可能出现日志重复问题");
        asyncExecuteJob.directSubmitTask(
                () -> this.executeJob(jobId, true, false)
        );

        return Result.buildSuc();
    }

    @Override
    public Result<List<HaJobDetailVO>> jobDetail(Long jobId) {
        // 获取详情
        Result<List<HaJobDetail>> haResult = haASSwitchJobService.jobDetail(jobId);
        if (haResult.failed()) {
            return Result.buildFromIgnoreData(haResult);
        }

        List<HaJobDetailVO> voList = ConvertUtil.list2List(haResult.getData(), HaJobDetailVO.class);
        if (voList.isEmpty()) {
            return Result.buildSuc(voList);
        }

        ClusterDO activeClusterDO = clusterService.getById(voList.get(0).getActiveClusterPhyId());
        ClusterDO standbyClusterDO = clusterService.getById(voList.get(0).getStandbyClusterPhyId());

        // 获取超时配置
        Long timeoutUnitSecConfig = this.getTimeoutUnitSecConfig(voList.get(0).getActiveClusterPhyId());
        voList.forEach(elem -> {
            elem.setTimeoutUnitSecConfig(timeoutUnitSecConfig);
            elem.setActiveClusterPhyName(activeClusterDO != null? activeClusterDO.getClusterName(): "");
            elem.setStandbyClusterPhyName(standbyClusterDO != null? standbyClusterDO.getClusterName(): "");
        });

        // 返回结果
        return Result.buildSuc(voList);
    }

    /**************************************************** private method ****************************************************/

    /**
     * 检查参数是否合法并返回需要进行主备切换的Topic
     */
    private Result<Set<String>> checkParamLegalAndGetNeedSwitchHaTopics(ASSwitchJobDTO dto) {
        // 1、检查主集群是否存在
        ClusterDO activeClusterDO = clusterService.getById(dto.getActiveClusterPhyId());
        if (ValidateUtils.isNull(activeClusterDO)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getActiveClusterPhyId()));
        }

        // 2、检查备集群是否存在
        ClusterDO standbyClusterDO = clusterService.getById(dto.getStandbyClusterPhyId());
        if (ValidateUtils.isNull(standbyClusterDO)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getStandbyClusterPhyId()));
        }

        // 3、检查集群是否建立了主备关系
        List<HaASRelationDO> clusterDOList = haASRelationService.listAllHAFromDB(dto.getActiveClusterPhyId(), dto.getStandbyClusterPhyId(), HaResTypeEnum.CLUSTER);
        if (ValidateUtils.isEmptyList(clusterDOList)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, "集群主备关系未建立");
        }

        // 4、获取集群当前已经建立主备关系的Topic列表
        List<HaASRelationDO> topicDOList = haASRelationService.listAllHAFromDB(dto.getActiveClusterPhyId(), dto.getStandbyClusterPhyId(), HaResTypeEnum.TOPIC);

        if (dto.getAll() != null && dto.getAll()) {
            // 5.1、对集群所有已经建立主备关系的Topic，进行主备切换

            // 过滤掉 __打头的Topic
            // 过滤掉 当前主集群已经是切换后的主集群的Topic，即这部分Topic已经是切换后的状态了
            return Result.buildSuc(
                    topicDOList.stream()
                            .filter(elem -> !elem.getActiveResName().startsWith("__"))
                            .filter(elem -> !elem.getActiveClusterPhyId().equals(dto.getActiveClusterPhyId()))
                            .map(elem -> elem.getActiveResName())
                            .collect(Collectors.toSet())
            );
        }

        // 5.2、指定Topic进行主备切换

        // 当前已经有主备关系的Topic
        Set<String> relationTopicNameSet = new HashSet<>();
        topicDOList.forEach(elem -> relationTopicNameSet.add(elem.getActiveResName()));

        // 逐个检查Topic，此时这里不进行过滤，如果进行过滤之后，会导致一些用户提交的信息丢失。
        // 比如提交了10个Topic，我过滤成9个，用户就会比较奇怪。
        // 上一步进行过滤，是减少不必要的Topic的刚扰，PS：也可以考虑增加这些干扰，从而让用户明确知道Topic已进行主备切换
        for (String topicName: dto.getTopicNameList()) {
            if (!relationTopicNameSet.contains(topicName)) {
                return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, String.format("Topic:[%s] 主备关系不存在，需要先建立主备关系", topicName));
            }

            // 检查新的主Topic是否存在，如果不存在则直接返回错误，不检查新的备Topic是否存在
            if (!PhysicalClusterMetadataManager.isTopicExist(dto.getActiveClusterPhyId(), topicName)) {
                return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, MsgConstant.getTopicNotExist(dto.getActiveClusterPhyId(), topicName));
            }
        }

        return Result.buildSuc(
                dto.getTopicNameList().stream().collect(Collectors.toSet())
        );
    }

    private void saveLogs(Long jobId, String content) {
        jobLogService.addLogAndIgnoreException(new JobLogDO(
                JobLogBizTypEnum.HA_SWITCH_JOB_LOG.getCode(),
                String.valueOf(jobId),
                new Date(),
                content
        ));
    }

    private void flushExtendData(HaASSwitchSubJobDO subJobDO) {
        HaSubJobExtendData extendData = new HaSubJobExtendData();
        Result<Long> sumLagResult = haTopicService.getStandbyTopicFetchLag(subJobDO.getActiveClusterPhyId(), subJobDO.getActiveResName());
        if (sumLagResult.failed()) {
            extendData.setSumLag(Constant.INVALID_CODE.longValue());
        } else {
            extendData.setSumLag(sumLagResult.getData());
        }

        haASSwitchJobService.updateSubJobExtendData(subJobDO.getId(), extendData);
    }

    private Result<Void> buildActionForbidden(Long jobId, Integer jobStatus) {
        return Result.buildFromRSAndMsg(
                ResultStatus.OPERATION_FORBIDDEN,
                String.format("jobId:[%d] 当前 status:[%s], 不允许被执行", jobId, HaJobStatusEnum.valueOfStatus(jobStatus))
        );
    }

    private boolean runningInTimeout(Long startTimeUnitMs, Long timeoutUnitSec) {
        if (timeoutUnitSec == null) {
            // 配置为空，则返回未超时
            return false;
        }

        // 开始时间 + 超时时间 > 当前时间，则为超时
        return startTimeUnitMs + timeoutUnitSec * 1000 > System.currentTimeMillis();
    }

    private Long getTimeoutUnitSecConfig(Long activeClusterPhyId) {
        // 获取该集群配置
        Long durationUnitSec = configService.getLongValue(
                ConfigConstant.HA_SWITCH_JOB_TIMEOUT_UNIT_SEC_CONFIG_PREFIX + "_" + activeClusterPhyId,
                null
        );

        if (durationUnitSec == null) {
            // 当前集群配置不存在，则获取默认配置
            durationUnitSec = configService.getLongValue(
                    ConfigConstant.HA_SWITCH_JOB_TIMEOUT_UNIT_SEC_CONFIG_PREFIX + "_" + Constant.INVALID_CODE,
                    null
            );
        }

        return durationUnitSec;
    }
}
