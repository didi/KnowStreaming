package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import org.apache.kafka.common.acl.AclBinding;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Task(name = "SyncKafkaAclTask",
        description = "KafkaAcl信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncKafkaAclTask extends AbstractAsyncMetadataDispatchTask {
    @Autowired
    private KafkaAclService kafkaAclService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        Result<List<AclBinding>> aclBindingListResult = kafkaAclService.getDataFromKafka(clusterPhy);
        if (aclBindingListResult.failed()) {
            return TaskResult.FAIL;
        }

        kafkaAclService.writeToDB(clusterPhy.getId(), aclBindingListResult.getData());

        return TaskResult.SUCCESS;
    }
}
