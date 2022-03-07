package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.dao.TopicExpiredDao;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author wyc
 * @date 2021/12/20
 */
public class TopicExpiredServiceTest extends BaseTest {

    /*
    该topic在region_1上，region_1使用了1,2broker，该topic3个分区，2个副本
     */
    @Value("${test.topic.name4}")
    private String REAL_TOPIC1_IN_ZK;

    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;


    @Autowired
    private TopicExpiredDao topicExpiredDao;

    @Autowired
    private TopicExpiredService topicExpiredService;


    private TopicExpiredDO getTopicExpiredDO() {
        TopicExpiredDO topicExpiredDO = new TopicExpiredDO();
        topicExpiredDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicExpiredDO.setExpiredDay(30);
        topicExpiredDO.setTopicName(REAL_TOPIC1_IN_ZK);
        topicExpiredDO.setStatus(0);

        return topicExpiredDO;
    }

    @Test
    public void retainExpiredTopicTest() {
        // 参数非法测试
        Assert.assertEquals(topicExpiredService.retainExpiredTopic(1L, "topic_a", -1), ResultStatus.PARAM_ILLEGAL);

        // Topic不存在测试
        Assert.assertEquals(topicExpiredService.retainExpiredTopic(1L, "topicNotExist", 40), ResultStatus.TOPIC_NOT_EXIST);

        // 成功测试
        // 过期Topic插入到topic_expired表中时，会先检查这个Topic是否在这个物理集群中，所以测试基于集群中建立了"topic_a"的topic
        topicExpiredDao.replace(getTopicExpiredDO());
        Assert.assertEquals(topicExpiredService.retainExpiredTopic(1L, getTopicExpiredDO().getTopicName(), 40), ResultStatus.SUCCESS);

    }


    @Test
    public void deleteByNameTest() {
        // 删除失败
        Assert.assertEquals(topicExpiredService.deleteByTopicName(1L, "notExistTopic"), 0);

        // 删除成功
        // 先在topic_expired表中插入数据,可以插入不存在的topic，因为这个删除只是从数据库中删除，删除的时候并没有检验topic是否存在于集群
        // 根据返回值判断是否删除成功
        TopicExpiredDO topicExpiredDO = getTopicExpiredDO();
        topicExpiredDO.setTopicName("test-topic");
        topicExpiredDao.replace(topicExpiredDO);
        Assert.assertEquals(topicExpiredService.deleteByTopicName(getTopicExpiredDO().getClusterId(), "test-topic"), 1);
    }
}
