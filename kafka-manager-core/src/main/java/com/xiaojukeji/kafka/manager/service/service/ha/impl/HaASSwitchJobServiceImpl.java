package com.xiaojukeji.kafka.manager.service.service.ha.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.job.HaJobStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.*;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.KafkaUserAndClientDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchJobDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchSubJobDO;
import com.xiaojukeji.kafka.manager.common.utils.ConvertUtil;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.ha.HaASSwitchJobDao;
import com.xiaojukeji.kafka.manager.dao.ha.HaASSwitchSubJobDao;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASSwitchJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HaASSwitchJobServiceImpl implements HaASSwitchJobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaASSwitchJobServiceImpl.class);

    @Autowired
    private HaASSwitchJobDao haASSwitchJobDao;

    @Autowired
    private HaASSwitchSubJobDao haASSwitchSubJobDao;

    @Override
    @Transactional
    public Result<Long> createJob(Long activeClusterPhyId,
                                  Long standbyClusterPhyId,
                                  List<String> topicNameList,
                                  List<KafkaUserAndClientDTO> kafkaUserAndClientList,
                                  String operator) {
        try {
            // 父任务
            HaASSwitchJobDO jobDO = new HaASSwitchJobDO(
                    activeClusterPhyId,
                    standbyClusterPhyId,
                    ValidateUtils.isEmptyList(kafkaUserAndClientList)? 0: 1,
                    kafkaUserAndClientList,
                    HaJobStatusEnum.RUNNING.getStatus(),
                    operator
            );

            haASSwitchJobDao.insert(jobDO);

            // 子任务
            for (String topicName: topicNameList) {
                haASSwitchSubJobDao.insert(new HaASSwitchSubJobDO(
                        jobDO.getId(),
                        activeClusterPhyId,
                        topicName,
                        standbyClusterPhyId,
                        topicName,
                        HaResTypeEnum.TOPIC.getCode(),
                        HaJobStatusEnum.RUNNING.getStatus(),
                        ""
                ));
            }

            return Result.buildSuc(jobDO.getId());
        } catch (Exception e) {
            LOGGER.error(
                    "method=createJob||activeClusterPhyId={}||standbyClusterPhyId={}||topicNameList={}||operator={}||errMsg=exception",
                    activeClusterPhyId, standbyClusterPhyId, ConvertUtil.obj2Json(topicNameList), operator, e
            );

            // 如果这一步出错了，则对上一步进行手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_ERROR, e.getMessage());
        }
    }

    @Override
    public int updateJobStatus(Long jobId, Integer jobStatus) {
        HaASSwitchJobDO jobDO = new HaASSwitchJobDO();
        jobDO.setId(jobId);
        jobDO.setJobStatus(jobStatus);
        return haASSwitchJobDao.updateById(jobDO);
    }

    @Override
    public int updateSubJobStatus(Long subJobId, Integer jobStatus) {
        HaASSwitchSubJobDO subJobDO = new HaASSwitchSubJobDO();
        subJobDO.setId(subJobId);
        subJobDO.setJobStatus(jobStatus);
        return haASSwitchSubJobDao.updateById(subJobDO);
    }

    @Override
    public int updateSubJobExtendData(Long subJobId, HaSubJobExtendData extendData) {
        HaASSwitchSubJobDO subJobDO = new HaASSwitchSubJobDO();
        subJobDO.setId(subJobId);
        subJobDO.setExtendData(ConvertUtil.obj2Json(extendData));
        return haASSwitchSubJobDao.updateById(subJobDO);
    }

    @Override
    public Result<List<HaJobDetail>> jobDetail(Long jobId) {
        LambdaQueryWrapper<HaASSwitchSubJobDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASSwitchSubJobDO::getJobId, jobId);

        List<HaASSwitchSubJobDO> doList = haASSwitchSubJobDao.selectList(lambdaQueryWrapper);
        if (ValidateUtils.isEmptyList(doList)) {
            return Result.buildFromRSAndMsg(ResultStatus.RESOURCE_NOT_EXIST, String.format("jobId:[%d] 不存在", jobId));
        }

        List<HaJobDetail> detailList = new ArrayList<>();
        doList.stream().forEach(elem -> {
            HaJobDetail detail = new HaJobDetail();
            detail.setTopicName(elem.getActiveResName());
            detail.setActiveClusterPhyId(elem.getActiveClusterPhyId());
            detail.setStandbyClusterPhyId(elem.getStandbyClusterPhyId());
            detail.setStatus(elem.getJobStatus());

            // Lag信息
            HaSubJobExtendData extendData = ConvertUtil.str2ObjByJson(elem.getExtendData(), HaSubJobExtendData.class);
            detail.setSumLag(extendData != null? extendData.getSumLag(): null);

            detailList.add(detail);
        });

        return Result.buildSuc(detailList);
    }

    @Override
    public List<Long> listRunningJobs(Long ignoreAfterTime) {
        return new ArrayList<>(new HashSet<>(
                this.listAfterTimeRunningJobs(ignoreAfterTime).values()
        ));
    }

    @Override
    public Map<Long, HaASSwitchJobDO> listClusterLatestJobs() {
        List<HaASSwitchJobDO> doList = haASSwitchJobDao.listAllLatest();

        Map<Long, HaASSwitchJobDO> doMap = new HashMap<>();
        for (HaASSwitchJobDO jobDO: doList) {
            HaASSwitchJobDO inMapJobDO = doMap.get(jobDO.getActiveClusterPhyId());
            if (inMapJobDO == null || inMapJobDO.getId() <= jobDO.getId()) {
                doMap.put(jobDO.getActiveClusterPhyId(), jobDO);
            }

            inMapJobDO = doMap.get(jobDO.getStandbyClusterPhyId());
            if (inMapJobDO == null || inMapJobDO.getId() <= jobDO.getId()) {
                doMap.put(jobDO.getStandbyClusterPhyId(), jobDO);
            }
        }

        return doMap;
    }

    @Override
    public HaASSwitchJobDO getJobById(Long jobId) {
        return haASSwitchJobDao.selectById(jobId);
    }

    @Override
    public List<HaASSwitchSubJobDO> listSubJobsById(Long jobId) {
        LambdaQueryWrapper<HaASSwitchSubJobDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HaASSwitchSubJobDO::getJobId, jobId);
        return haASSwitchSubJobDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<HaASSwitchSubJobDO> listAll(Boolean isAsc) {
        LambdaQueryWrapper<HaASSwitchSubJobDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderBy(isAsc != null, isAsc, HaASSwitchSubJobDO::getId);
        return haASSwitchSubJobDao.selectList(lambdaQueryWrapper);
    }

    /**************************************************** private method ****************************************************/

    private Map<Long, Long> listAfterTimeRunningJobs(Long ignoreAfterTime) {
        LambdaQueryWrapper<HaASSwitchJobDO> jobLambdaQueryWrapper = new LambdaQueryWrapper<>();
        jobLambdaQueryWrapper.eq(HaASSwitchJobDO::getJobStatus, HaJobStatusEnum.RUNNING.getStatus());
        List<HaASSwitchJobDO> jobDOList = haASSwitchJobDao.selectList(jobLambdaQueryWrapper);
        if (jobDOList == null) {
            return new HashMap<>();
        }

        // 获取指定时间之前的任务
        jobDOList = jobDOList.stream().filter(job -> job.getCreateTime().getTime() <= ignoreAfterTime).collect(Collectors.toList());

        Map<Long, Long> clusterPhyIdAndJobIdMap = new HashMap<>();
        jobDOList.forEach(elem -> {
            clusterPhyIdAndJobIdMap.put(elem.getActiveClusterPhyId(), elem.getId());
            clusterPhyIdAndJobIdMap.put(elem.getStandbyClusterPhyId(), elem.getId());
        });
        return clusterPhyIdAndJobIdMap;
    }
}
