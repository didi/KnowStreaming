package com.xiaojukeji.know.streaming.km.core.flusher.zk.handler;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.common.enums.KafkaConfigTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.config.ConfigChangeNotificationBaseData;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.config.ConfigChangeNotificationDataV1;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.config.ConfigChangeNotificationDataV2;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.ConfigEntityChangeNotificationZNode;
import kafka.zookeeper.ZNodeChildChangeHandler;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConfigNotificationNodeChangeHandler extends AbstractZKHandler implements ZNodeChildChangeHandler {
    private static final ILog log = LogFactory.getLog(ConfigNotificationNodeChangeHandler.class);

    private static final FutureUtil<Void> futureUtil = FutureUtil.init(
            "ConfigNotificationHandlerThread",
            1,
            1,
            Integer.MAX_VALUE
    );

    private volatile String startConfigChangeNode = "config_change_";

    public ConfigNotificationNodeChangeHandler(Long clusterPhyId, KafkaZKDAO kafkaZKDAO, KafkaChangeRecordService kafkaChangeRecordService) {
        super(clusterPhyId, kafkaZKDAO, kafkaChangeRecordService);
        this.kafkaChangeRecordService = kafkaChangeRecordService;
    }

    @Override
    public void init() {
        this.handleChildChange();
    }

    @Override
    public String path() {
        return ConfigEntityChangeNotificationZNode.path();
    }

    @Override
    public void handleChildChange() {
        List<String> notificationList = new ArrayList<>();
        try {
            notificationList.addAll(kafkaZKDAO.getChildren(clusterPhyId, this.path(), true));
        } catch (Exception e) {
            log.error("method=handleChildChange||clusterPhyId={}||errMsg=exception", clusterPhyId, e);

            // 出现异常时，进行重试
            reInitDataIfException();
            return;
        }

        // 记录变更
        futureUtil.submitTask(
                () -> recordChangeToDB(this.clusterPhyId, notificationList)
        );
    }

    private void recordChangeToDB(Long clusterPhyId, List<String> notificationList) {
        try {
            boolean success = true;
            for (String notification: notificationList) {
                try {
                    if (startConfigChangeNode.compareTo(notification) >= 0) {
                        // 已经处理过的，则不进行处理
                        continue;
                    }

                    this.recordChangeToDB(this.clusterPhyId, notification);

                    if (success) {
                        startConfigChangeNode = notification;
                    }
                } catch (Exception e) {
                    log.error("method=recordChangeToDB||clusterPhyId={}||notification={}||errMsg=exception!", clusterPhyId, notification, e);

                    success = false;
                }
            }
        } catch (Exception e) {
            log.error("method=recordChangeToDB||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);
        }
    }

    private void recordChangeToDB(Long clusterPhyId, String notification) throws Exception {
        String changeZNode = this.path() + "/" + notification;

        Tuple<byte[], Stat> dataTuple = kafkaZKDAO.getDataAndStat(clusterPhyId, changeZNode);
        if (System.currentTimeMillis() - dataTuple.getV2().getCtime() >= (10L * 60L * 1000L)) {
            // 忽略历史的
            return;
        }

        KafkaChangeRecordPO recordPO = null;

        ConfigChangeNotificationBaseData baseData = JSONObject.parseObject(dataTuple.getV1(), ConfigChangeNotificationBaseData.class);
        if (ConfigChangeNotificationDataV1.CHANGE_DATA_VERSION.equals(baseData.getVersion())) {
            ConfigChangeNotificationDataV1 dataV1 = JSONObject.parseObject(dataTuple.getV1(), ConfigChangeNotificationDataV1.class);

            recordPO = new KafkaChangeRecordPO(
                    clusterPhyId,
                    KafkaConfigTypeEnum.getByConfigName(dataV1.getEntityType()),
                    dataV1.getEntityName(),
                    OperationEnum.EDIT,
                    new Date(dataTuple.getV2().getCtime())
            );


        } else if (ConfigChangeNotificationDataV2.CHANGE_DATA_VERSION.equals(baseData.getVersion())) {
            ConfigChangeNotificationDataV2 dataV2 = JSONObject.parseObject(dataTuple.getV1(), ConfigChangeNotificationDataV2.class);

            int idx = dataV2.getEntityPath().indexOf("/");
            if (idx < 0) {
                log.error("method=recordChangeToDB||clusterPhyId={}||notification={}||notifyData={}||errMsg=data illegal.", clusterPhyId, notification, dataV2);
                return;
            }

            recordPO = new KafkaChangeRecordPO(
                    clusterPhyId,
                    KafkaConfigTypeEnum.getByConfigName(dataV2.getEntityPath().substring(0, idx)),
                    dataV2.getEntityPath().substring(idx + 1),
                    OperationEnum.EDIT,
                    new Date(dataTuple.getV2().getCtime())
            );
        }

        kafkaChangeRecordService.insertAndIgnoreDuplicate(recordPO);
    }
}
