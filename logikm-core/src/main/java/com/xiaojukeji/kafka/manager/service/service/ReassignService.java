package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicReassignActionEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.reassign.ReassignStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignExecSubDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import kafka.common.TopicAndPartition;
import kafka.utils.ZkUtils;

import java.util.List;
import java.util.Map;

/**
 * migrate topic service
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
public interface ReassignService {
    ResultStatus createTask(List<ReassignTopicDTO> dtoList, String operator);

    ResultStatus modifyTask(ReassignExecDTO dto, TopicReassignActionEnum actionEnum);

    ResultStatus modifySubTask(ReassignExecSubDTO dto);

    List<ReassignTaskDO> getReassignTaskList();

    List<ReassignTaskDO> getTask(Long taskId);

    Result<List<ReassignStatus>> getReassignStatus(Long taskId);

    Map<TopicAndPartition, TaskStatusReassignEnum> verifyAssignment(String zkAddr, String reassignmentJson);

    Map<TopicAndPartition, TaskStatusReassignEnum> verifyAssignment(ZkUtils zkUtils, String reassignmentJson);


}
