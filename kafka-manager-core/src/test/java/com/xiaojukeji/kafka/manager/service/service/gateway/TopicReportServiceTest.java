package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicReportDO;
import com.xiaojukeji.kafka.manager.dao.gateway.TopicReportDao;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * @author xuguang
 * @Date 2021/12/7
 */
public class TopicReportServiceTest extends BaseTest {

    @Autowired
    private TopicReportService topicReportService;

    @Autowired
    private TopicReportDao topicReportDao;

    @DataProvider(name = "provideTopicReportDO")
    public static Object[][] provideTopicReportDO() {
        TopicReportDO topicReportDO = new TopicReportDO();
        topicReportDO.setId(1L);
        topicReportDO.setClusterId(1L);
        topicReportDO.setTopicName("xgTest");
        topicReportDO.setStartTime(new Date(1638786493173L));
        topicReportDO.setEndTime(new Date(1638786493173L));
        topicReportDO.setModifyTime(new Date(1638786493173L));
        topicReportDO.setCreateTime(new Date(1638786493173L));
        return new Object[][] {{topicReportDO}};
    }

    @Test(dataProvider = "provideTopicReportDO")
    public void getNeedReportTopicTest(TopicReportDO topicReportDO) {
        // 数据库中插入数据
        int replace = topicReportDao.replace(topicReportDO);
        List<TopicReportDO> result = topicReportService.getNeedReportTopic(1L);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).toString(), topicReportDO.toString());
    }
}
