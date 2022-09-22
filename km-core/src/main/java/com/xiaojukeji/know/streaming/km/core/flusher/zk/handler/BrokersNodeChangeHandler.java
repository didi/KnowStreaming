package com.xiaojukeji.know.streaming.km.core.flusher.zk.handler;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.BrokerIdsZNode;
import kafka.zookeeper.ZNodeChildChangeHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class BrokersNodeChangeHandler extends AbstractZKHandler implements ZNodeChildChangeHandler {
    private static final ILog log = LogFactory.getLog(BrokersNodeChangeHandler.class);

    private final BrokerService brokerService;

    private static final FutureUtil<Void> futureUtil = FutureUtil.init(
            "BrokerChangeHandlerThread",
            1,
            1,
            Integer.MAX_VALUE
    );

    public BrokersNodeChangeHandler(Long clusterPhyId, KafkaZKDAO kafkaZKDAO, KafkaChangeRecordService kafkaChangeRecordService, BrokerService brokerService) {
        super(clusterPhyId, kafkaZKDAO, kafkaChangeRecordService);
        this.brokerService = brokerService;
    }

    @Override
    public void init() {
        this.handleChildChange();
    }

    @Override
    public String path() {
        return BrokerIdsZNode.path();
    }

    @Override
    public void handleChildChange() {
        long triggerTime = System.currentTimeMillis() / 1000L * 1000L;

        List<Broker> brokerList = new ArrayList<>();
        try {
            for (String brokerId: kafkaZKDAO.getChildren(clusterPhyId, BrokerIdsZNode.path(), true)) {
                brokerList.add(kafkaZKDAO.getBrokerMetadata(clusterPhyId, Integer.valueOf(brokerId)));
            }
        } catch (Exception e) {
            log.error("method=handleChildChange||clusterPhyId={}||errMsg=exception", clusterPhyId, e);

            // 出现异常时，进行重试
            reInitDataIfException();
            return;
        }

        // 记录变更
        futureUtil.submitTask(
                () -> recordChangeToDB(this.clusterPhyId, brokerList, triggerTime)
        );

        // 更新DB
        futureUtil.submitTask(
                () -> updateDBData(this.clusterPhyId, brokerList)
        );
    }

    private void recordChangeToDB(Long clusterPhyId, List<Broker> zkBrokerList, long triggerTime) {
        try {
            // DB中记录的
            Map<Integer, Broker> dbBrokerMap = brokerService.listAllBrokersFromDB(clusterPhyId).stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));

            for (Broker zkBroker: zkBrokerList) {
                Broker dbBroker = dbBrokerMap.remove(zkBroker.getBrokerId());
                if (dbBroker == null) {
                    // 这是新增的Broker
                    kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                            clusterPhyId,
                            ModuleEnum.KAFKA_BROKER,
                            String.valueOf(zkBroker.getBrokerId()),
                            OperationEnum.ADD,
                            new Date(zkBroker.getStartTimestamp())
                    ));

                    continue;
                }

                if (!zkBroker.getStartTimestamp().equals(dbBroker.getStartTimestamp())) {
                    // 这是修改的Broker
                    kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                            clusterPhyId,
                            ModuleEnum.KAFKA_BROKER,
                            String.valueOf(zkBroker.getBrokerId()),
                            OperationEnum.EDIT,
                            new Date(zkBroker.getStartTimestamp())
                    ));
                }
            }

            // 这是下线的Broker
            for (Broker dbBroker: dbBrokerMap.values()) {
                if (!dbBroker.alive()) {
                    // broker本身就未存活，则直接跳过
                    continue;
                }

                kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                        clusterPhyId,
                        ModuleEnum.KAFKA_BROKER,
                        String.valueOf(dbBroker.getBrokerId()),
                        OperationEnum.DELETE,
                        new Date(triggerTime),
                        new Date(dbBroker.getStartTimestamp()) // 用下线Broker的启动时间，避免重复记录删除动作
                ));
            }
        } catch (Exception e) {
            log.error("method=recordChangeToDB||clusterPhyId={}||errMsg=exception", clusterPhyId, e);
        }
    }

    private void updateDBData(Long clusterPhyId, List<Broker> zkBrokerList) {
        try {
            brokerService.updateAliveBrokers(clusterPhyId, zkBrokerList);
        } catch (Exception e) {
            log.error("method=updateDBData||clusterPhyId={}||errMsg=exception", clusterPhyId, e);
        }
    }
}
