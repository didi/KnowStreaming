package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Task(name = "SyncControllerTask",
        description = "Controller信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncControllerTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog LOGGER = LogFactory.getLog(SyncControllerTask.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private KafkaControllerService kafkaControllerService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        Result<KafkaController> controllerResult = kafkaControllerService.getControllerFromKafka(clusterPhy);
        if (controllerResult.failed()) {
            return new TaskResult(TaskResult.FAIL_CODE, controllerResult.getMessage());
        }

        if (controllerResult.getData() == null) {
            kafkaControllerService.setNoKafkaController(clusterPhy.getId(), System.currentTimeMillis() / 1000L * 1000L);

            return TaskResult.SUCCESS;
        }


        Broker controllerBroker = null;

        Result<List<Broker>> brokerListResult = brokerService.listBrokersFromKafka(clusterPhy);
        if (brokerListResult.failed()) {
            LOGGER.error("method=processClusterTask||clusterPhyId={}||result={}||errMsg=list brokers failed", clusterPhy.getId(), brokerListResult);
        } else {
            for (Broker broker: brokerListResult.getData()) {
                if (broker.getBrokerId().equals(controllerResult.getData().getBrokerId())) {
                    controllerBroker = broker;
                }
            }
        }

        kafkaControllerService.insertAndIgnoreDuplicateException(
                controllerResult.getData(),
                controllerBroker != null? controllerBroker.getHost(): "",
                controllerBroker != null? controllerBroker.getRack(): ""
        );

        return TaskResult.SUCCESS;
    }
}
