package com.xiaojukeji.kafka.manager.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;
import com.xiaojukeji.kafka.manager.dao.ha.JobLogDao;
import com.xiaojukeji.kafka.manager.service.service.JobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JobLogServiceImpl implements JobLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobLogServiceImpl.class);

    @Autowired
    private JobLogDao jobLogDao;

    @Override
    public void addLogAndIgnoreException(JobLogDO jobLogDO) {
        try {
            jobLogDao.insert(jobLogDO);
        } catch (Exception e) {
            LOGGER.error("method=addLogAndIgnoreException||jobLogDO={}||errMsg=exception", jobLogDO);
        }
    }

    @Override
    public List<JobLogDO> listLogs(Integer bizType, String bizKeyword, Long startId) {
        LambdaQueryWrapper<JobLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(JobLogDO::getBizType, bizType);
        lambdaQueryWrapper.eq(JobLogDO::getBizKeyword, bizKeyword);
        if (startId != null) {
            lambdaQueryWrapper.ge(JobLogDO::getId, startId);
        }

        return jobLogDao.selectList(lambdaQueryWrapper);
    }
}
