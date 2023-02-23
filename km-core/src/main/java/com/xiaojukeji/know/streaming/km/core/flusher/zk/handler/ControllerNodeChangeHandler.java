package com.xiaojukeji.know.streaming.km.core.flusher.zk.handler;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.BackoffUtils;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.ControllerZNode;
import kafka.zookeeper.ZNodeChangeHandler;

import java.util.Date;


public class ControllerNodeChangeHandler extends AbstractZKHandler implements ZNodeChangeHandler {
    private static final ILog log = LogFactory.getLog(ControllerNodeChangeHandler.class);

    private static final FutureUtil<Void> futureUtil = FutureUtil.init(
            "ControllerChangeHandlerThread",
            1,
            1,
            Integer.MAX_VALUE
    );

    private final KafkaControllerService kafkaControllerService;

    public ControllerNodeChangeHandler(Long clusterPhyId, KafkaZKDAO kafkaZKDAO, KafkaChangeRecordService kafkaChangeRecordService, KafkaControllerService kafkaControllerService) {
        super(clusterPhyId, kafkaZKDAO, kafkaChangeRecordService);
        this.kafkaControllerService = kafkaControllerService;
    }

    @Override
    public void init() {
        this.handleChange();
    }

    @Override
    public String path() {
        return ControllerZNode.path();
    }

    @Override
    public void handleCreation() {
        this.handleChange();
    }

    @Override
    public void handleDeletion() {
        this.handleChange();
    }

    @Override
    public void handleDataChange() {
        this.handleChange();
    }

    public void handleChange() {
        long triggerTime = System.currentTimeMillis() / 1000L * 1000L;

        // 回退等待3秒, 绝大多数情况下，3秒都可以选出新的controller，如果3秒后还没有，则记录该3秒无controller的情况
        BackoffUtils.backoff(3000);

        // 记录变更
        futureUtil.submitTask(
                () -> recordChangeToDB(this.clusterPhyId, triggerTime)
        );

        // 更新DB
        futureUtil.submitTask(
                () -> updateDBData(this.clusterPhyId, triggerTime)
        );
    }

    private void recordChangeToDB(Long clusterPhyId, long triggerTime) {
        try {
            KafkaController kafkaController = kafkaZKDAO.getKafkaController(clusterPhyId, true);

            // 记录变更
            kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                    clusterPhyId,
                    ModuleEnum.KAFKA_CONTROLLER,
                    String.valueOf(kafkaController != null ? kafkaController.getBrokerId(): Constant.INVALID_CODE),
                    kafkaController != null ? OperationEnum.SWITCH: OperationEnum.DELETE,
                    kafkaController != null ? new Date(kafkaController.getTimestamp()) : new Date(triggerTime)
            ));
        } catch (Exception e) {
            log.error("method=recordChangeToDB||clusterPhyId={}||errMsg=exception", clusterPhyId, e);
        }
    }

    private void updateDBData(Long clusterPhyId, long triggerTime) {
        try {
            KafkaController kafkaController = kafkaZKDAO.getKafkaController(clusterPhyId, true);
            if (kafkaController == null) {
                kafkaControllerService.setNoKafkaController(clusterPhyId, triggerTime);
            } else {
                Broker broker = kafkaZKDAO.getBrokerMetadata(clusterPhyId, kafkaController.getBrokerId());

                kafkaControllerService.insertAndIgnoreDuplicateException(kafkaController, broker != null? broker.getHost(): "", broker != null? broker.getRack(): "");
            }
        } catch (Exception e) {
            log.error("method=updateDBData||clusterPhyId={}||errMsg=exception", clusterPhyId, e);

            // 出现异常时，进行重试
            reInitDataIfException();
        }
    }
}
