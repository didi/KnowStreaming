package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbExecDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.reassign.ReassignCmbTaskDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTaskVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbTopicProcessVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignCmbVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassigncmbMerticsVO;

import java.util.List;

public interface ReassignCmbService {
  List<ReassignCmbVO> getReassignTasksByCondition(ReassignCmbDTO dto);

  Result createReassignTask(ReassignCmbTaskDTO dto);

  Result<ReassigncmbMerticsVO> getReassignTopicMetrics(Long clusterId, String topicName);

  Result modifyReassignTask(ReassignCmbTaskDTO dto);

  Result executeReassignTask(ReassignCmbExecDTO dto);

  Result<ReassignCmbTaskVO> getReassignTasksByTaskId(Long taskId);

  Result<List<ReassignCmbTopicProcessVO>> getReassignTopicProcess(Long clusterId, String topicName);

  long getTotal();
}
