package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.partition.PartitionPO;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Task(name = "SyncPartitionTask",
        description = "Partition信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncPartitionTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog log = LogFactory.getLog(SyncPartitionTask.class);

    @Autowired
    private PartitionService partitionService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        Result<Map<String, List<Partition>>> partitionsResult = partitionService.listPartitionsFromKafka(clusterPhy);
        if (partitionsResult.failed()) {
            return new TaskResult(TaskResult.FAIL_CODE, partitionsResult.getMessage());
        }

        // 获取DB中的partition
        Map<String, List<PartitionPO>> dbPartitionMap = new HashMap<>();
        partitionService.listPartitionPOByCluster(clusterPhy.getId()).forEach(
                elem -> {
                    dbPartitionMap.putIfAbsent(elem.getTopicName(), new ArrayList<>());
                    dbPartitionMap.get(elem.getTopicName()).add(elem);
                }
        );

        for (Map.Entry<String, List<Partition>> entry: partitionsResult.getData().entrySet()) {
            try {
                partitionService.updatePartitions(clusterPhy.getId(), entry.getKey(), entry.getValue(), dbPartitionMap.getOrDefault(entry.getKey(), new ArrayList<>()));
            } catch (Exception e) {
                log.error("method=processSubTask||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhy.getId(), entry.getKey(), e);
            }
        }

        // 删除不存在的Topic的分区
        partitionService.deletePartitionsIfNotIn(clusterPhy.getId(), partitionsResult.getData().keySet());

        return TaskResult.SUCCESS;
    }
}
