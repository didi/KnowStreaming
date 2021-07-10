package com.xiaojukeji.kafka.manager.notify.notifyer;

import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zengqiao
 * @date 20/9/25
 */
@Service("notifyService")
public class KafkaNotifierService extends AbstractNotifyService {
    @Value("${notify.kafka.cluster-id:}")
    private Long clusterId;

    @Value("${notify.kafka.topic-name:}")
    private String topicName;

    @Override
    public boolean sendMsg(String username, String content) {
        KafkaClientPool.produceData2Kafka(clusterId, topicName, content);
        return true;
    }
}