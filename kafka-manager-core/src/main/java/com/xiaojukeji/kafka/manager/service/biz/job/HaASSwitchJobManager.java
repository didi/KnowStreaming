package com.xiaojukeji.kafka.manager.service.biz.job;


import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaJobState;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.ASSwitchJobDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.ha.job.HaJobDetailVO;

import java.util.List;


public interface HaASSwitchJobManager {
    /**
     * 创建任务
     */
    Result<Long> createJob(ASSwitchJobDTO dto, String operator);

    /**
     * 执行job
     * @param jobId 任务ID
     * @param focus 强制切换
     * @param firstTriggerExecute 第一次触发执行
     * @return
     */
    Result<Void> executeJob(Long jobId, boolean focus, boolean firstTriggerExecute);

    Result<HaJobState> jobState(Long jobId);

    /**
     * 刷新扩展数据
     */
    void flushExtendData(Long jobId);

    /**
     * 对Job执行操作
     */
    Result<Void> actionJob(Long jobId, ASSwitchJobActionDTO dto);

    Result<List<HaJobDetailVO>> jobDetail(Long jobId);
}
