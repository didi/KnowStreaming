package com.xiaojukeji.know.streaming.km.core.flusher.zk.handler;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.TopicsZNode;
import kafka.zookeeper.ZNodeChildChangeHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TopicsNodeChangeHandler extends AbstractZKHandler implements ZNodeChildChangeHandler {
    private static final ILog log = LogFactory.getLog(TopicsNodeChangeHandler.class);

    private final TopicService topicService;

    private static final FutureUtil<Void> futureUtil = FutureUtil.init(
            "TopicChangeHandlerThread",
            1,
            1,
            Integer.MAX_VALUE
    );

    public TopicsNodeChangeHandler(Long clusterPhyId, KafkaZKDAO kafkaZKDAO, KafkaChangeRecordService kafkaChangeRecordService, TopicService topicService) {
        super(clusterPhyId, kafkaZKDAO, kafkaChangeRecordService);
        this.topicService = topicService;
    }

    @Override
    public void init() {
        this.handleChildChange();
    }

    @Override
    public String path() {
        return TopicsZNode.path();
    }

    @Override
    public void handleChildChange() {
        long triggerTime = System.currentTimeMillis() / 1000L * 1000L;

        List<Topic> topicList = new ArrayList<>();
        try {
            for (String topicName: kafkaZKDAO.getChildren(clusterPhyId, TopicsZNode.path(), true)) {
                topicList.add(kafkaZKDAO.getTopicMetadata(clusterPhyId, topicName));
            }
        } catch (Exception e) {
            log.error("method=handleChildChange||clusterPhyId={}||errMsg=exception", clusterPhyId, e);

            // 出现异常时，进行重试
            reInitDataIfException();
            return;
        }

        // 记录变更
        futureUtil.submitTask(
                () -> recordChangeToDB(this.clusterPhyId, topicList, triggerTime)
        );

        // 更新DB
        futureUtil.submitTask(
                () -> updateDBData(this.clusterPhyId, topicList)
        );
    }

    private void recordChangeToDB(Long clusterPhyId, List<Topic> zkTopicList, long triggerTime) {
        try {
            // DB中记录的
            Map<String, Topic> dbTopicMap = topicService.listTopicsFromDB(clusterPhyId).stream().collect(Collectors.toMap(Topic::getTopicName, Function.identity()));

            for (Topic zkTopic: zkTopicList) {
                Topic dbTopic = dbTopicMap.remove(zkTopic.getTopicName());
                if (dbTopic == null) {
                    // 这是新增的Topic
                    kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                            clusterPhyId,
                            ModuleEnum.KAFKA_TOPIC,
                            zkTopic.getTopicName(),
                            OperationEnum.ADD,
                            new Date(zkTopic.getCreateTime())
                    ));

                    continue;
                }

                if (!zkTopic.getUpdateTime().equals(dbTopic.getUpdateTime())) {
                    // 这是修改的Topic
                    kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                            clusterPhyId,
                            ModuleEnum.KAFKA_TOPIC,
                            zkTopic.getTopicName(),
                            OperationEnum.EDIT,
                            new Date(zkTopic.getCreateTime())
                    ));
                }
            }

            // 这是删除的Topic
            for (Topic dbTopic: dbTopicMap.values()) {
                kafkaChangeRecordService.insertAndIgnoreDuplicate(new KafkaChangeRecordPO(
                        clusterPhyId,
                        ModuleEnum.KAFKA_TOPIC,
                        dbTopic.getTopicName(),
                        OperationEnum.DELETE,
                        new Date(triggerTime),
                        new Date(dbTopic.getCreateTime()) // 用被删除Topic的创建时间，避免重复记录删除动作
                ));
            }
        } catch (Exception e) {
            log.error("method=recordChangeToDB||clusterPhyId={}||errMsg=exception", clusterPhyId, e);
        }
    }

    private void updateDBData(Long clusterPhyId, List<Topic> topicList) {
        try {
            topicService.batchReplaceMetadata(clusterPhyId, topicList);
        } catch (Exception e) {
            log.error("method=updateDBData||clusterPhyId={}||errMsg=exception", clusterPhyId, e);
        }
    }
}
