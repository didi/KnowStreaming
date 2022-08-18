package com.xiaojukeji.know.streaming.km.core.service.reassign;

import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignExecutionStrategy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignTask;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

public interface ReassignStrategyService {
    /**
     * 根据均衡策略生成迁移任务详情
     * @param executionStrategy 均衡策略

     * @return
     */
    Result<List<ReassignTask>> generateReassignmentTask(ReassignExecutionStrategy executionStrategy);

}
