package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.task.ClusterTaskMetadataVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.task.ClusterTaskStatusVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.task.ClusterTaskSubStatusVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.task.ClusterTaskVO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskStatus;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.ClusterTaskSubStatus;
import com.xiaojukeji.kafka.manager.kcm.common.bizenum.ClusterTaskSubStateEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/21
 */
public class ClusterTaskModelConverter {
    public static List<ClusterTaskVO> convert2ClusterTaskVOList(List<ClusterTaskDO> doList,
                                                                Map<Long, String> clusterNameMap) {
        if (ValidateUtils.isEmptyList(doList)) {
            return new ArrayList<>();
        }
        List<ClusterTaskVO> voList = new ArrayList<>();
        for (ClusterTaskDO clusterTaskDO: doList) {
            ClusterTaskVO vo = new ClusterTaskVO();
            vo.setTaskId(clusterTaskDO.getId());
            vo.setTaskType(clusterTaskDO.getTaskType());
            vo.setClusterId(clusterTaskDO.getClusterId());
            vo.setClusterName(clusterNameMap.getOrDefault(clusterTaskDO.getClusterId(), ""));
            vo.setStatus(clusterTaskDO.getTaskStatus());
            vo.setOperator(clusterTaskDO.getOperator());
            vo.setCreateTime(clusterTaskDO.getCreateTime().getTime());
            voList.add(vo);
        }
        return voList;
    }

    public static ClusterTaskMetadataVO convert2ClusterTaskMetadataVO(ClusterTaskDO clusterTaskDO,
                                                                      ClusterDO clusterDO,
                                                                      KafkaFileDO kafkaFileDO) {
        if (ValidateUtils.isNull(clusterTaskDO)) {
            return null;
        }
        ClusterTaskMetadataVO vo = new ClusterTaskMetadataVO();
        vo.setTaskId(clusterTaskDO.getId());
        vo.setClusterId(clusterTaskDO.getClusterId());
        vo.setClusterName(ValidateUtils.isNull(clusterDO)? "": clusterDO.getClusterName());
        vo.setHostList(ListUtils.string2StrList(clusterTaskDO.getHostList()));
        vo.setPauseHostList(ListUtils.string2StrList(clusterTaskDO.getPauseHostList()));
        vo.setRollbackHostList(ListUtils.string2StrList(clusterTaskDO.getRollbackHostList()));
        vo.setRollbackPauseHostList(ListUtils.string2StrList(clusterTaskDO.getRollbackPauseHostList()));
        vo.setKafkaPackageName(clusterTaskDO.getKafkaPackage());
        vo.setKafkaPackageMd5(clusterTaskDO.getKafkaPackageMd5());
        vo.setServerPropertiesFileId(ValidateUtils.isNull(kafkaFileDO)? null: kafkaFileDO.getId());
        vo.setServerPropertiesName(clusterTaskDO.getServerProperties());
        vo.setServerPropertiesMd5(clusterTaskDO.getServerPropertiesMd5());
        vo.setOperator(clusterTaskDO.getOperator());
        vo.setGmtCreate(clusterTaskDO.getCreateTime().getTime());
        return vo;
    }

    public static ClusterTaskStatusVO convert2ClusterTaskStatusVO(ClusterTaskStatus clusterTaskStatus,
                                                                  Map<String, List<String>> hostRoleMap) {
        if (ValidateUtils.isNull(clusterTaskStatus)) {
            return null;
        }
        ClusterTaskStatusVO clusterTaskStatusVO = new ClusterTaskStatusVO();
        clusterTaskStatusVO.setTaskId(clusterTaskStatus.getTaskId());
        if (ValidateUtils.isNull(clusterTaskStatus.getStatus())) {
            clusterTaskStatusVO.setStatus(null);
        } else {
            clusterTaskStatusVO.setStatus(clusterTaskStatus.getStatus().getCode());
        }
        clusterTaskStatusVO.setRollback(clusterTaskStatus.getRollback());
        clusterTaskStatusVO.setSumCount(clusterTaskStatus.getSubStatusList().size());
        clusterTaskStatusVO.setSuccessCount(0);
        clusterTaskStatusVO.setFailedCount(0);
        clusterTaskStatusVO.setRunningCount(0);
        clusterTaskStatusVO.setWaitingCount(0);
        clusterTaskStatusVO.setSubTaskStatusList(new ArrayList<>());
        for (ClusterTaskSubStatus elem: clusterTaskStatus.getSubStatusList()) {
            ClusterTaskSubStatusVO vo = new ClusterTaskSubStatusVO();
            vo.setHostname(elem.getHostname());
            vo.setStatus(elem.getStatus().getCode());
            vo.setKafkaRoles(
                    ListUtils.strList2String(hostRoleMap.getOrDefault(elem.getHostname(), new ArrayList<>()))
            );
            vo.setGroupId(elem.getGroupNum());

            // 任务状态
            if (ClusterTaskSubStateEnum.WAITING.equals(elem.getStatus())) {
                clusterTaskStatusVO.setWaitingCount(clusterTaskStatusVO.getWaitingCount() + 1);
            } else if (ClusterTaskSubStateEnum.RUNNING.equals(elem.getStatus())
                    || ClusterTaskSubStateEnum.KILLING.equals(elem.getStatus())) {
                clusterTaskStatusVO.setRunningCount(clusterTaskStatusVO.getRunningCount() + 1);
            } else if (ClusterTaskSubStateEnum.FAILED.equals(elem.getStatus())
                    || ClusterTaskSubStateEnum.TIMEOUT.equals(elem.getStatus())
                    || ClusterTaskSubStateEnum.CANCELED.equals(elem.getStatus())
                    || ClusterTaskSubStateEnum.IGNORED.equals(elem.getStatus())
                    || ClusterTaskSubStateEnum.KILL_FAILED.equals(elem.getStatus())) {
                clusterTaskStatusVO.setFailedCount(clusterTaskStatusVO.getFailedCount() + 1);
            } else if (ClusterTaskSubStateEnum.SUCCEED.equals(elem.getStatus())) {
                clusterTaskStatusVO.setSuccessCount(clusterTaskStatusVO.getSuccessCount() + 1);
            }
            clusterTaskStatusVO.getSubTaskStatusList().add(vo);
        }
        return clusterTaskStatusVO;
    }
}