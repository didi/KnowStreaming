package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Task(name = "SyncZookeeperTask",
        description = "ZK信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncZookeeperTask extends AbstractAsyncMetadataDispatchTask {
    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        Result<List<ZookeeperInfo>> infoResult = zookeeperService.getDataFromKafka(clusterPhy);
        if (infoResult.failed()) {
            return new TaskResult(TaskResult.FAIL_CODE, infoResult.getMessage());
        }

        zookeeperService.writeToDB(clusterPhy.getId(), infoResult.getData());

        return TaskResult.SUCCESS;
    }
}
