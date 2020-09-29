package com.xiaojukeji.kafka.manager.kcm.common;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ClusterTaskConstant;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;

/**
 * @author zengqiao
 * @date 20/9/8
 */
public class Converters {
    public static ClusterTaskDO convert2ClusterTaskDO(Long agentId,
                                                      CreationTaskData creationTaskDTO,
                                                      String operator) {
        ClusterTaskDO clusterTaskDO = new ClusterTaskDO();
        clusterTaskDO.setUuid(creationTaskDTO.getUuid());
        clusterTaskDO.setClusterId(creationTaskDTO.getClusterId());
        clusterTaskDO.setTaskType(creationTaskDTO.getTaskType());
        clusterTaskDO.setKafkaPackage(creationTaskDTO.getKafkaPackageName());
        clusterTaskDO.setKafkaPackageMd5(creationTaskDTO.getKafkaPackageMd5());
        clusterTaskDO.setServerProperties(creationTaskDTO.getServerPropertiesName());
        clusterTaskDO.setServerPropertiesMd5(creationTaskDTO.getServerPropertiesMd5());
        clusterTaskDO.setAgentTaskId(agentId);
        clusterTaskDO.setAgentRollbackTaskId(ClusterTaskConstant.INVALID_AGENT_TASK_ID);
        clusterTaskDO.setHostList(ListUtils.strList2String(creationTaskDTO.getHostList()));
        clusterTaskDO.setPauseHostList(ListUtils.strList2String(creationTaskDTO.getPauseList()));
        clusterTaskDO.setRollbackHostList("");
        clusterTaskDO.setRollbackPauseHostList("");
        clusterTaskDO.setOperator(operator);
        return clusterTaskDO;
    }

    public static CreationTaskData convert2CreationTaskData(ClusterTaskDO clusterTaskDO) {
        CreationTaskData creationTaskData = new CreationTaskData();
        creationTaskData.setUuid(clusterTaskDO.getUuid());
        creationTaskData.setClusterId(clusterTaskDO.getClusterId());
        creationTaskData.setHostList(ListUtils.string2StrList(clusterTaskDO.getRollbackHostList()));
        creationTaskData.setPauseList(ListUtils.string2StrList(clusterTaskDO.getRollbackPauseHostList()));
        creationTaskData.setTaskType(ClusterTaskConstant.CLUSTER_ROLLBACK);
        creationTaskData.setKafkaPackageName("");
        creationTaskData.setKafkaPackageMd5("");
        creationTaskData.setKafkaPackageUrl("");
        creationTaskData.setServerPropertiesName("");
        creationTaskData.setServerPropertiesMd5("");
        creationTaskData.setServerPropertiesUrl("");
        return creationTaskData;
    }
}