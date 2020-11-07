package com.xiaojukeji.kafka.manager.openapi;

import com.xiaojukeji.kafka.manager.common.bizenum.ConsumeHealthEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.openapi.common.dto.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/22
 */
public interface ThirdPartService {
    Result<ConsumeHealthEnum> checkConsumeHealth(Long clusterId,
                                                 String topicName,
                                                 String consumerGroup,
                                                 Long maxDelayTime);

    List<Result> resetOffsets(ClusterDO clusterDO, OffsetResetDTO dto);}