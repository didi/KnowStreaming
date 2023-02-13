package com.xiaojukeji.kafka.manager.service.service;


import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.JobLogDO;

import java.util.List;

/**
 * Job相关的日志
 */
public interface JobLogService {
    void addLogAndIgnoreException(JobLogDO jobLogDO);

    List<JobLogDO> listLogs(Integer bizType, String bizKeyword, Long startId);
}
