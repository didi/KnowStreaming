package com.xiaojukeji.kafka.manager.service.service.ha;


import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaJobDetail;
import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaSubJobExtendData;
import com.xiaojukeji.kafka.manager.common.entity.dto.ha.KafkaUserAndClientDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchJobDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASSwitchSubJobDO;

import java.util.List;
import java.util.Map;

public interface HaASSwitchJobService {
    /**
     * 创建任务
     */
    Result<Long> createJob(Long activeClusterPhyId,
                           Long standbyClusterPhyId,
                           List<String> topicNameList,
                           List<KafkaUserAndClientDTO> kafkaUserAndClientList,
                           String operator);

    /**
     * 更新任务状态
     */
    int updateJobStatus(Long jobId, Integer jobStatus);

    /**
     * 更新子任务状态
     */
    int updateSubJobStatus(Long subJobId, Integer jobStatus);

    /**
     * 更新子任务扩展数据
     */
    int updateSubJobExtendData(Long subJobId, HaSubJobExtendData extendData);

    /**
     * 任务详情
     */
    Result<List<HaJobDetail>> jobDetail(Long jobId);

    /**
     * 正在运行中的job
     */
    List<Long> listRunningJobs(Long ignoreAfterTime);

    /**
     * 集群近期的任务ID
     */
    Map<Long/*集群ID*/, HaASSwitchJobDO> listClusterLatestJobs();

    HaASSwitchJobDO getJobById(Long jobId);

    List<HaASSwitchSubJobDO> listSubJobsById(Long jobId);

    /**
     * 获取所有切换任务
     */
    List<HaASSwitchSubJobDO> listAll(Boolean isAsc);
}
