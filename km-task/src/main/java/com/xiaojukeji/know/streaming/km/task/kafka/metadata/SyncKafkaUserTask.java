package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkauser.KafkaUser;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.kafkauser.KafkaUserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Task(name = "SyncKafkaUserTask",
        description = "KafkaUser信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncKafkaUserTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog log = LogFactory.getLog(SyncKafkaUserTask.class);

    @Autowired
    private KafkaUserService kafkaUserService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        // 查询KafkaUser数据
        Result<List<KafkaUser>> kafkaUserResult = kafkaUserService.getKafkaUserFromKafka(clusterPhy.getId());
        if (kafkaUserResult.failed()) {
            return TaskResult.FAIL;
        }

        if (!kafkaUserResult.hasData()) {
            return TaskResult.SUCCESS;
        }

        // 更新DB中的数据
        kafkaUserService.batchReplaceKafkaUserInDB(
                clusterPhy.getId(),
                kafkaUserResult.getData().stream().map(elem-> elem.getName()).collect(Collectors.toList())
        );

        return TaskResult.SUCCESS;
    }
}
