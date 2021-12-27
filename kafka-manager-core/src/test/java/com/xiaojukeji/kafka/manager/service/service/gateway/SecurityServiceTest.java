package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import com.xiaojukeji.kafka.manager.dao.gateway.KafkaAclDao;
import com.xiaojukeji.kafka.manager.dao.gateway.KafkaUserDao;
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
public class SecurityServiceTest extends BaseTest {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private KafkaUserDao kafkaUserDao;
    
    @Autowired
    private KafkaAclDao kafkaAclDao;

    @DataProvider(name = "provideKafkaUserDO")
    public static Object[][] provideKafkaUserDO() {
        KafkaUserDO kafkaUserDO = new KafkaUserDO();
        kafkaUserDO.setAppId("AppIdModuleTest");
        kafkaUserDO.setPassword("AppIdTest");
        kafkaUserDO.setUserType(1);
        kafkaUserDO.setOperation(0);
        return new Object[][] {{kafkaUserDO}};
    }

    @DataProvider(name = "provideKafkaAclDO")
    public static Object[][] provideKafkaAclDO() {
        KafkaAclDO kafkaAclDO = new KafkaAclDO();
        kafkaAclDO.setAppId("AppIdModuleTest");
        kafkaAclDO.setClusterId(1L);
        kafkaAclDO.setTopicName("topicModuleTest");
        kafkaAclDO.setAccess(3);
        kafkaAclDO.setOperation(0);
        return new Object[][] {{kafkaAclDO}};
    }

    @Test(dataProvider = "provideKafkaUserDO")
    public void getKafkaUsersTest(KafkaUserDO kafkaUserDO) {
        kafkaUserDao.insert(kafkaUserDO);

        long now = System.currentTimeMillis();
        List<KafkaUserDO> kafkaUsers = securityService.getKafkaUsers(0L, now);
        Assert.assertFalse(kafkaUsers.isEmpty());
        Assert.assertTrue(kafkaUsers.stream()
                .allMatch( kafkaUser -> kafkaUser.getCreateTime().after(new Date(0L)) &&
                        kafkaUser.getCreateTime().before( new Date(now))));
    }

    @Test(dataProvider = "provideKafkaAclDO")
    public void getKafkaAclsTest(KafkaAclDO kafkaAclDO) {
        kafkaAclDao.insert(kafkaAclDO);

        long now = System.currentTimeMillis();
        List<KafkaAclDO> kafkaAcls = securityService.getKafkaAcls(kafkaAclDO.getClusterId(), 0L, now);
        Assert.assertFalse(kafkaAcls.isEmpty());
        Assert.assertTrue(kafkaAcls.stream()
                .allMatch(kafkaUser -> kafkaUser.getCreateTime().after(new Date(0L)) &&
                        kafkaUser.getCreateTime().before( new Date(now)) &&
                        kafkaUser.getClusterId().equals(kafkaAclDO.getClusterId())));
    }
}
