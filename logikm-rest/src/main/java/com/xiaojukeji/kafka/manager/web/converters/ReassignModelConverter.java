package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.entity.ao.reassign.ReassignStatus;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignPartitionStatusVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignTaskVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign.ReassignTopicStatusVO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ReassignmentElemData;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import kafka.common.TopicAndPartition;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/4/16
 */
public class ReassignModelConverter {
    public static List<ReassignTopicStatusVO> convert2ReassignTopicStatusVOList(List<ReassignStatus> dtoList) {
        if (ValidateUtils.isNull(dtoList)) {
            return new ArrayList<>();
        }

        List<ReassignTopicStatusVO> voList = new ArrayList<>();
        for (ReassignStatus elem: dtoList) {
            ReassignTopicStatusVO vo = new ReassignTopicStatusVO();
            vo.setSubTaskId(elem.getSubTaskId());
            vo.setClusterId(elem.getClusterId());
            vo.setClusterName(elem.getClusterName());
            vo.setTopicName(elem.getTopicName());
            vo.setStatus(elem.getStatus());
            vo.setCompletedPartitionNum(0);
            vo.setRealThrottle(elem.getRealThrottle());
            vo.setMaxThrottle(elem.getMaxThrottle());
            vo.setMinThrottle(elem.getMinThrottle());
            vo.setTotalPartitionNum(elem.getReassignList().size());
            vo.setReassignList(new ArrayList<>());

            if (ValidateUtils.isNull(elem.getReassignStatusMap())) {
                elem.setReassignStatusMap(new HashMap<>());
            }
            for (ReassignmentElemData elemData: elem.getReassignList()) {
                ReassignPartitionStatusVO partitionStatusVO = new ReassignPartitionStatusVO();
                partitionStatusVO.setPartitionId(elemData.getPartition());
                partitionStatusVO.setDestReplicaIdList(elemData.getReplicas());
                TaskStatusReassignEnum reassignEnum = elem.getReassignStatusMap().get(
                        new TopicAndPartition(elem.getTopicName(),
                                elemData.getPartition())
                );
                if (!ValidateUtils.isNull(reassignEnum)) {
                    partitionStatusVO.setStatus(reassignEnum.getCode());
                }
                if (!ValidateUtils.isNull(reassignEnum) && TaskStatusReassignEnum.isFinished(reassignEnum.getCode())) {
                    vo.setCompletedPartitionNum(vo.getCompletedPartitionNum() + 1);
                }
                vo.getReassignList().add(partitionStatusVO);
            }
            voList.add(vo);
        }
        return voList;
    }

    public static List<ReassignTaskVO> convert2ReassignTaskVOList(List<ReassignTaskDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }

        // 同一批任务聚合在一起
        Map<Long, List<ReassignTaskDO>> doMap = new TreeMap<>(Collections.reverseOrder());
        for (ReassignTaskDO elem: doList) {
            List<ReassignTaskDO> subDOList = doMap.getOrDefault(elem.getTaskId(), new ArrayList<>());
            subDOList.add(elem);
            doMap.put(elem.getTaskId(), subDOList);
        }

        // 计算这一批任务的状态
        List<ReassignTaskVO> voList = new ArrayList<>();
        for (Long taskId: doMap.keySet()) {
            voList.add(convert2ReassignTaskVO(taskId, doMap.get(taskId)));
        }
        return voList;
    }

    public static ReassignTaskVO convert2ReassignTaskVO(Long taskId, List<ReassignTaskDO> doList) {
        if (ValidateUtils.isEmptyList(doList)) {
            return null;
        }

        ReassignTaskVO vo = new ReassignTaskVO();
        vo.setTaskName(String.format("%s 数据迁移任务", DateUtils.getFormattedDate(taskId)));
        vo.setTaskId(taskId);
        vo.setTotalTopicNum(doList.size());
        vo.setBeginTime(0L);
        vo.setEndTime(0L);

        Integer completedTopicNum = 0;
        Set<Integer> statusSet = new HashSet<>();
        for (ReassignTaskDO elem: doList) {
            vo.setGmtCreate(elem.getGmtCreate().getTime());
            vo.setOperator(elem.getOperator());
            vo.setDescription(elem.getDescription());
            if (TaskStatusReassignEnum.isFinished(elem.getStatus())) {
                completedTopicNum += 1;
                statusSet.add(elem.getStatus());

                // 任务结束时间
                vo.setEndTime(Math.max(elem.getGmtModify().getTime(), vo.getEndTime()));
            } else {
                statusSet.add(elem.getStatus());
            }
            // 任务计划开始时间
            vo.setBeginTime(elem.getBeginTime().getTime());
        }

        // 任务整体状态
        if (statusSet.contains(TaskStatusReassignEnum.RUNNING.getCode())) {
            vo.setStatus(TaskStatusReassignEnum.RUNNING.getCode());
            vo.setEndTime(null);
        } else if (statusSet.contains(TaskStatusReassignEnum.RUNNABLE.getCode())) {
            vo.setStatus(TaskStatusReassignEnum.RUNNABLE.getCode());
            vo.setEndTime(null);
        } else if (statusSet.contains(TaskStatusReassignEnum.NEW.getCode()) && statusSet.size() == 1) {
            // 所有的都是新建状态
            vo.setStatus(TaskStatusReassignEnum.NEW.getCode());
            vo.setEndTime(null);
        } else if (statusSet.contains(TaskStatusReassignEnum.CANCELED.getCode()) && statusSet.size() == 1) {
            // 所有的都是取消状态
            vo.setStatus(TaskStatusReassignEnum.CANCELED.getCode());
        } else if (statusSet.contains(TaskStatusReassignEnum.SUCCEED.getCode()) && statusSet.size() == 1) {
            // 所有的都是成功状态
            vo.setStatus(TaskStatusReassignEnum.SUCCEED.getCode());
        } else if (statusSet.contains(TaskStatusReassignEnum.FAILED.getCode())) {
            // 所有的都是成功状态
            vo.setStatus(TaskStatusReassignEnum.FAILED.getCode());
        } else {
            vo.setStatus(TaskStatusReassignEnum.UNKNOWN.getCode());
            vo.setEndTime(null);
        }

        vo.setCompletedTopicNum(completedTopicNum);
        return vo;
    }
}