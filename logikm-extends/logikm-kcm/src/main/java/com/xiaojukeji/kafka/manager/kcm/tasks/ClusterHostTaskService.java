package com.xiaojukeji.kafka.manager.kcm.tasks;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.NetUtils;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ClusterTaskConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterHostTaskDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author zengqiao
 * @date 20/5/20
 */
@Service(ClusterTaskConstant.CLUSTER_HOST_BEAN_NAME)
public class ClusterHostTaskService extends AbstractClusterTaskService {
    @Override
    public Result<CreationTaskData> getOperationHosts(AbstractClusterTaskDTO abstractClusterTaskDTO) {
        ClusterHostTaskDTO clusterHostTaskDTO = (ClusterHostTaskDTO) abstractClusterTaskDTO;

        CreationTaskData dto = new CreationTaskData();
        for (String hostname: clusterHostTaskDTO.getHostList()) {
            if (!NetUtils.hostnameLegal(hostname)) {
                return Result.buildFrom(ResultStatus.CLUSTER_TASK_HOST_LIST_ILLEGAL);
            }
        }
        dto.setHostList(clusterHostTaskDTO.getHostList());
        dto.setPauseList(Arrays.asList(clusterHostTaskDTO.getHostList().get(0)));
        return new Result<>(dto);
    }
}