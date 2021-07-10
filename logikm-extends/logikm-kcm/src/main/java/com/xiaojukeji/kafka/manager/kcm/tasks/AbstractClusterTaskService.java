package com.xiaojukeji.kafka.manager.kcm.tasks;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.common.utils.UUIDUtils;

/**
 * @author zengqiao
 * @date 20/5/20
 */
public abstract class AbstractClusterTaskService {

    public Result<CreationTaskData> getCreateTaskParamDTO(AbstractClusterTaskDTO abstractClusterTaskDTO) {
        Result<CreationTaskData> operationHostResult = getOperationHosts(abstractClusterTaskDTO);
        if (!Constant.SUCCESS.equals(operationHostResult.getCode())) {
            return new Result<>(operationHostResult.getCode(), operationHostResult.getMessage());
        }

        CreationTaskData dto = operationHostResult.getData();
        dto.setUuid(UUIDUtils.uuid());
        dto.setClusterId(abstractClusterTaskDTO.getClusterId());
        dto.setTaskType(abstractClusterTaskDTO.getTaskType());
        dto.setKafkaPackageName(abstractClusterTaskDTO.getKafkaPackageName());
        dto.setKafkaPackageMd5(abstractClusterTaskDTO.getKafkaPackageMd5());
        dto.setKafkaPackageUrl(
                abstractClusterTaskDTO.getKafkaFileBaseUrl() + "/" + abstractClusterTaskDTO.getKafkaPackageName()
        );
        dto.setServerPropertiesName(abstractClusterTaskDTO.getServerPropertiesName());
        dto.setServerPropertiesMd5(abstractClusterTaskDTO.getServerPropertiesMd5());
        dto.setServerPropertiesUrl(
                abstractClusterTaskDTO.getKafkaFileBaseUrl() + "/" + abstractClusterTaskDTO.getServerPropertiesName()
        );
        return new Result<>(dto);
    }

    protected abstract Result<CreationTaskData> getOperationHosts(AbstractClusterTaskDTO abstractClusterTaskDTO);
}