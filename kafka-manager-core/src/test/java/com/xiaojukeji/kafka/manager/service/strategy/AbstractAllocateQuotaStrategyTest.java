package com.xiaojukeji.kafka.manager.service.strategy;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2021/12/15
 */
public class AbstractAllocateQuotaStrategyTest extends BaseTest {

    @Autowired
    private AbstractAllocateQuotaStrategy abstractAllocateQuotaStrategy;

    private TopicQuota getTopicQuota() {
        TopicQuota topicQuota = new TopicQuota();
        topicQuota.setTopicName("xxx");
        topicQuota.setConsumeQuota(1000L);
        topicQuota.setProduceQuota(1000L);
        topicQuota.setAppId("xxxAppId");
        topicQuota.setClusterId(1L);
        return topicQuota;
    }

    @Test
    public void getNewTopicQuotaTest() {
        TopicQuota topicQuota = getTopicQuota();
        TopicQuota newTopicQuota = abstractAllocateQuotaStrategy.getNewTopicQuota(topicQuota, 3, 1000L);
        Assert.assertNotNull(newTopicQuota);
        Assert.assertEquals(newTopicQuota.toString(), topicQuota.toString());
    }
}
