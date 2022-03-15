package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.RdTopicBasic;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.MineTopicSummary;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicAppData;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicBusinessInfo;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.dao.TopicDao;
import com.xiaojukeji.kafka.manager.dao.TopicExpiredDao;
import com.xiaojukeji.kafka.manager.dao.TopicStatisticsDao;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author wyc
 * @date 2021/12/21
 */
public class TopicManagerServiceTest extends BaseTest {
    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.topic.name4}")
    private String REAL_TOPIC_IN_ZK;

    @Value("${test.admin}")
    private String ADMIN_NAME_IN_MYSQL;

    private final static String KAFKA_MANAGER_APP_NAME = "KM管理员";

    @Value("${test.app.id}")
    private String KAFKA_MANAGER_APP_ID;

    private final static Set<Integer> REAL_BROKER_ID_SET = new HashSet<>();

    @Value("${test.region-name}")
    private String REAL_REGION_IN_CLUSTER;

    @Value("${test.logicalCluster.name}")
    private String REAL_LOGICAL_CLUSTER_NAME;

    // 共享集群
    private final static Integer REAL_LOGICAL_CLUSTER_MODE = 0;

    static {
        REAL_BROKER_ID_SET.add(1);
        REAL_BROKER_ID_SET.add(2);
    }


    @Autowired
    @InjectMocks
    private TopicManagerService topicManagerService;

    @Mock
    private TopicDao topicDao;

    @Mock
    private TopicStatisticsDao topicStatisticsDao;

    @Mock
    private TopicExpiredDao topicExpiredDao;

    @Mock
    private AppService appService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private ThrottleService throttleService;

    @Mock
    private RegionService regionService;

    @Mock
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private TopicDO getTopicDO() {
        TopicDO topicDO = new TopicDO();
        topicDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicDO.setTopicName("test_topic");
        topicDO.setAppId("appId");
        topicDO.setPeakBytesIn(100L);
        return topicDO;
    }

    private TopicDO getTopicDOInCluster() {
        // 这个Topic是通过工单手动在物理集群中插入的
        TopicDO topicDO = new TopicDO();
        topicDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicDO.setTopicName(REAL_TOPIC_IN_ZK);
        topicDO.setAppId(KAFKA_MANAGER_APP_ID);
        topicDO.setPeakBytesIn(100L);
        return topicDO;
    }

    private TopicStatisticsDO getTopicStatisticsDO() {
        // cluster_id, topic_name, offset_sum, max_avg_bytes_in, gmt_day
        TopicStatisticsDO topicStatisticsDO = new TopicStatisticsDO();
        topicStatisticsDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicStatisticsDO.setTopicName(REAL_TOPIC_IN_ZK);
        topicStatisticsDO.setOffsetSum(1L);
        topicStatisticsDO.setMaxAvgBytesIn(1.0);
        topicStatisticsDO.setGmtDay("2020-03-30");
        return topicStatisticsDO;
    }

    private TopicExpiredDO getTopicExpiredDO() {
        TopicExpiredDO topicExpiredDO = new TopicExpiredDO();
        return topicExpiredDO;
    }

    private AuthorityDO getAuthorityDO() {
        AuthorityDO authorityDO = new AuthorityDO();
        authorityDO.setAccess(3);
        authorityDO.setAppId(KAFKA_MANAGER_APP_ID);
        authorityDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        authorityDO.setTopicName(REAL_TOPIC_IN_ZK);
        return authorityDO;
    }

    private TopicThrottledMetrics getTopicThrottledMetrics() {
        TopicThrottledMetrics metrics = new TopicThrottledMetrics();
        metrics.setAppId(KAFKA_MANAGER_APP_ID);
        metrics.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        metrics.setTopicName(REAL_TOPIC_IN_ZK);
        metrics.setClientType(KafkaClientEnum.PRODUCE_CLIENT);
        metrics.setBrokerIdSet(REAL_BROKER_ID_SET);
        return metrics;
    }

    private TopicAppData getTopicAppData() {
        TopicAppData data = new TopicAppData();
        data.setAccess(3);
        data.setAppId(KAFKA_MANAGER_APP_ID);
        data.setAppName(KAFKA_MANAGER_APP_NAME);
        data.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        data.setTopicName(REAL_TOPIC_IN_ZK);
        data.setAppPrincipals(ADMIN_NAME_IN_MYSQL);
        data.setConsumerQuota(15728640L);
        data.setProduceQuota(15728640L);
        return data;
    }

    private ClusterDO getClusterDO() {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(REAL_CLUSTER_ID_IN_MYSQL);
        return clusterDO;
    }

    private RegionDO getRegionDO() {
        RegionDO regionDO = new RegionDO();
        regionDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        regionDO.setBrokerList(ListUtils.intList2String(new ArrayList<>(REAL_BROKER_ID_SET)));
        regionDO.setName(REAL_REGION_IN_CLUSTER);
        return regionDO;
    }

    private RdTopicBasic getRdTopicBasic() {
        RdTopicBasic rdTopicBasic = new RdTopicBasic();
        rdTopicBasic.setAppId(KAFKA_MANAGER_APP_ID);
        rdTopicBasic.setAppName(KAFKA_MANAGER_APP_NAME);
        List<String> regionNameList = new ArrayList<>();
        regionNameList.add(REAL_REGION_IN_CLUSTER);
        rdTopicBasic.setRegionNameList(regionNameList);
        return rdTopicBasic;
    }

    private TopicBusinessInfo getTopicBusinessInfo() {
        TopicBusinessInfo info = new TopicBusinessInfo();
        info.setAppId(KAFKA_MANAGER_APP_ID);
        info.setAppName(KAFKA_MANAGER_APP_NAME);
        info.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        info.setPrincipals(ADMIN_NAME_IN_MYSQL);
        info.setTopicName(REAL_TOPIC_IN_ZK);

        return info;
    }

    private LogicalClusterDO getLogicalClusterDO() {
        LogicalClusterDO logicalClusterDO = new LogicalClusterDO();
        logicalClusterDO.setName(REAL_LOGICAL_CLUSTER_NAME);
        logicalClusterDO.setMode(REAL_LOGICAL_CLUSTER_MODE);
        logicalClusterDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        logicalClusterDO.setAppId(KAFKA_MANAGER_APP_ID);
        return logicalClusterDO;
    }

    @Test
    public void addTopicTest() {
        // addTopic只是向数据库topic表中写数据
        // 成功测试
        Mockito.when(topicDao.insert(Mockito.any())).thenReturn(1);
        Assert.assertEquals(topicManagerService.addTopic(getTopicDO()), 1);

        // 失败测试,再次插入相同的Topic
        Mockito.when(topicDao.insert(Mockito.any())).thenReturn(0);
        Assert.assertEquals(topicManagerService.addTopic(getTopicDO()), 0);
    }



    @Test
    public void deleteByTopicNameTest() {
        // 删除也只是删除topic表中的数据
        // 删除失败测试，删除一个不存在的
        Mockito.when(topicDao.deleteByName(Mockito.anyLong(), Mockito.anyString())).thenReturn(0);
        Assert.assertEquals(topicManagerService.deleteByTopicName(getTopicDO().getClusterId(), "notExistTopic"), 0);

        // 删除成功测试
        Mockito.when(topicDao.deleteByName(Mockito.anyLong(), Mockito.anyString())).thenReturn(1);
        Assert.assertEquals(topicManagerService.deleteByTopicName(getTopicDO().getClusterId(), getTopicDO().getTopicName()), 1);
    }



    @Test(description = "物理集群中不存在该topic时的删除操作")
    public void modifyTopic2TopicNotExist() {
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        Assert.assertEquals(topicManagerService.modifyTopic(getTopicDO().getClusterId(), "notExistTopic", null, null), ResultStatus.TOPIC_NOT_EXIST);
    }

    @Test(description = "modifyTopic, 成功")
    public void modifyTopic2Success() {
        TopicDO topicDO = getTopicDOInCluster();
        // 因为会检查集群中是否存在这个Topic，因此直接用KM创建topic，用这个Topic测试
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDO);
        Mockito.when(topicDao.updateByName(Mockito.any())).thenReturn(1);
        Assert.assertEquals(topicManagerService.modifyTopic(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, "更改过", "operator"), ResultStatus.SUCCESS);
    }



    @Test(description = "modifyTopicByOp, 物理集群中不存在该topic")
    public void modifyTopicByOp2TopicNotExistTest() {
        Assert.assertEquals(topicManagerService.modifyTopicByOp(getTopicDOInCluster().getClusterId(), "notExistTopic", "dkm_admin", null, "admin"), ResultStatus.TOPIC_NOT_EXIST);
    }

    @Test(description = "modifyTopicByOp, 物理集群中不存在传入的appId")
    public void modifyTopicByOp2AppNotExistTest() {
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(null);
        Assert.assertEquals(topicManagerService.modifyTopicByOp(getTopicDOInCluster().getClusterId(), getTopicDOInCluster().getTopicName(), "notExistAppId", null, "admin"), ResultStatus.APP_NOT_EXIST);
    }

    @Test(description = "modifyTopicByOp, 成功测试")
    public void modifyTopicByOp2SuccessTest() {
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(appDO);
        Assert.assertEquals(topicManagerService.modifyTopicByOp(getTopicDOInCluster().getClusterId(), getTopicDOInCluster().getTopicName(), getTopicDOInCluster().getAppId(), "无", "admin"), ResultStatus.SUCCESS);
    }


    private void addAuthority2ClusterNotExistTest() {
        AuthorityDO authorityDO = getAuthorityDO();
        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(appDO);
        Mockito.when(logicalClusterMetadataManager.getPhysicalClusterId(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(topicManagerService.addAuthority(authorityDO), ResultStatus.CLUSTER_NOT_EXIST);
    }



    @Test
    public void listAllTest() {
        // 准备mock返回数据
        List<TopicDO> doList = new ArrayList<>();
        doList.add(getTopicDO());
        topicDao.insert(getTopicDO());
        Mockito.when(topicDao.listAll()).thenReturn(doList);
        List<TopicDO> topicDOS = topicManagerService.listAll();
        Assert.assertFalse(topicDOS.isEmpty());
        Assert.assertTrue(topicDOS.stream().allMatch(topicDO ->
                topicDO.getClusterId().equals(getTopicDO().getClusterId()) &&
                topicDO.getAppId().equals(getTopicDO().getAppId()) &&
                topicDO.getTopicName().equals(getTopicDO().getTopicName())));
    }

    @Test
    public void getByClusterIdFromCacheTest() {
        // 返回空集合测试
        getByClusterIdFromCache2ReturnEmptyListTest();

        // 返回成功测试
        getByClusterIdFromCache2SuccessTest();
    }

    private void getByClusterIdFromCache2ReturnEmptyListTest() {
        Assert.assertTrue(topicManagerService.getByClusterIdFromCache(null).isEmpty());
    }

    private void getByClusterIdFromCache2SuccessTest() {
        List<TopicDO> doList = new ArrayList<>();
        doList.add(getTopicDO());
        Mockito.when(topicDao.getByClusterIdFromCache(Mockito.anyLong())).thenReturn(doList);
        Assert.assertEquals(topicManagerService.getByClusterIdFromCache(REAL_CLUSTER_ID_IN_MYSQL), doList);
    }


    @Test
    public void getByClusterIdTest() {
        // 返回空集合测试
        getByClusterId2ReturnEmptyListTest();

        // 返回成功测试
        getByClusterId2SuccessTest();
    }

    private void getByClusterId2ReturnEmptyListTest() {
        Assert.assertTrue(topicManagerService.getByClusterId(null).isEmpty());
    }

    private void getByClusterId2SuccessTest() {
        List<TopicDO> doList = new ArrayList<>();
        doList.add(getTopicDO());
        Mockito.when(topicDao.getByClusterId(Mockito.anyLong())).thenReturn(doList);
        Assert.assertEquals(topicManagerService.getByClusterId(REAL_CLUSTER_ID_IN_MYSQL), doList);
    }



    @Test
    public void getByTopicNameTest() {
        // 返回null测试
        getByTopicName2ReturnNullTest();

        // 成功测试
        getByTopicName2SuccessTest();
    }

    private void getByTopicName2ReturnNullTest() {
        Assert.assertNull(topicManagerService.getByTopicName(null, REAL_TOPIC_IN_ZK));
        Assert.assertNull(topicManagerService.getByTopicName(REAL_CLUSTER_ID_IN_MYSQL, null));
    }

    private void getByTopicName2SuccessTest() {
        TopicDO topicDOInCluster = getTopicDOInCluster();
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDOInCluster);
        Assert.assertEquals(topicManagerService.getByTopicName(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK), topicDOInCluster);
    }

    @Test
    public void replaceTopicStatisticsTest() {
        // 失败测试
        Mockito.when(topicStatisticsDao.replace(Mockito.any())).thenReturn(0);
        Assert.assertEquals(topicManagerService.replaceTopicStatistics(new TopicStatisticsDO()), 0);

        // 成功测试
        Mockito.when(topicStatisticsDao.replace(Mockito.any())).thenReturn(1);
        Assert.assertEquals(topicManagerService.replaceTopicStatistics(new TopicStatisticsDO()), 1);
    }

    @Test
    public void getTopicMaxAvgBytesInTest() {
        // 返回空Map测试
        getTopicMaxAvgBytesIn2ReturnEmptyMapTest();

        // 返回成功测试
        getTopicMaxAvgBytesIn2SuccessTest();
    }

    private void getTopicMaxAvgBytesIn2ReturnEmptyMapTest() {
        List<TopicStatisticsDO> doList = new ArrayList<>();
        Mockito.when(topicStatisticsDao.getTopicStatisticData(Mockito.anyLong(), Mockito.any(), Mockito.anyDouble())).thenReturn(doList);
        Assert.assertTrue(topicManagerService.getTopicMaxAvgBytesIn(REAL_CLUSTER_ID_IN_MYSQL,1, 1.0).size() == 0);
    }

    private void getTopicMaxAvgBytesIn2SuccessTest() {
        List<TopicStatisticsDO> doList = new ArrayList<>();
        TopicStatisticsDO topicStatisticsDO = getTopicStatisticsDO();
        doList.add(topicStatisticsDO);
        Mockito.when(topicStatisticsDao.getTopicStatisticData(Mockito.anyLong(), Mockito.any(), Mockito.anyDouble())).thenReturn(doList);

        Map<String, List<Double>> expectMap = new HashMap<>();
        List<Double> list = new ArrayList<>();
        list.add(topicStatisticsDO.getMaxAvgBytesIn());
        expectMap.put(topicStatisticsDO.getTopicName(), list);

        Map<String, List<Double>> actualMap = topicManagerService.getTopicMaxAvgBytesIn(REAL_CLUSTER_ID_IN_MYSQL, 1, 1.0);
        Assert.assertTrue(actualMap.keySet().stream().allMatch(key -> actualMap.get(key).equals(expectMap.get(key))));
    }

    @Test
    public void getTopicMaxAvgBytesInTest2() {
        // 返回空测试
        getTopicMaxAvgBytesInTest2NullTest();
        // 成功测试
        getTopicMaxAvgBytesInTest2SuccessTest();
    }

    private void getTopicMaxAvgBytesInTest2NullTest() {
        Mockito.when(topicStatisticsDao.getTopicMaxAvgBytesIn(Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn(null);
        Assert.assertEquals(topicManagerService.getTopicMaxAvgBytesIn(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, new Date(), new Date(), 1), null);
    }

    private void getTopicMaxAvgBytesInTest2SuccessTest() {
        Mockito.when(topicStatisticsDao.getTopicMaxAvgBytesIn(Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyInt())).thenReturn(1.0);
        Assert.assertEquals(topicManagerService.getTopicMaxAvgBytesIn(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, new Date(), new Date(), 1), 1.0);
    }

    @Test
    public void getByTopicAndDayTest() {
        TopicStatisticsDO topicStatisticsDO = getTopicStatisticsDO();
        Mockito.when(topicStatisticsDao.getByTopicAndDay(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(topicStatisticsDO);
        Assert.assertEquals(topicManagerService.getByTopicAndDay(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, "2020-03-30"), topicStatisticsDO);
    }

    @Test
    public void getExpiredTopicsTest() {
        Mockito.when(topicExpiredDao.getExpiredTopics(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Assert.assertTrue(topicManagerService.getExpiredTopics(30).isEmpty());
    }




    private MineTopicSummary getMineTopicSummary() {
        MineTopicSummary summary = new MineTopicSummary();
        summary.setAppName(KAFKA_MANAGER_APP_NAME);
        summary.setAccess(TopicAuthorityEnum.OWNER.getCode());
        summary.setPhysicalClusterId(1L);
        summary.setTopicName(REAL_TOPIC_IN_ZK);
        return summary;
    }

    private TopicDO getRealTopicDO() {
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(KAFKA_MANAGER_APP_ID);
        topicDO.setTopicName(REAL_TOPIC_IN_ZK);
        topicDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        topicDO.setDescription("topic介绍");
        topicDO.setAppId(KAFKA_MANAGER_APP_ID);
        return topicDO;
    }

    private AppDO getAppDO() {
        AppDO appDO = new AppDO();
        appDO.setAppId(KAFKA_MANAGER_APP_ID);
        appDO.setName(KAFKA_MANAGER_APP_NAME);
        appDO.setPrincipals(ADMIN_NAME_IN_MYSQL);
        appDO.setType(1);
        return appDO;
    }

    @Test
    public void getMyTopicsTest() {
        MineTopicSummary mineTopicSummary = getMineTopicSummary();
        List<TopicDO> topicDOList = new ArrayList<>();
        TopicDO realTopicDO = getRealTopicDO();
        topicDOList.add(realTopicDO);

        List<AppDO> appDOList = new ArrayList<>();
        AppDO appDO = getAppDO();
        appDOList.add(appDO);

        Mockito.when(topicDao.listAll()).thenReturn(topicDOList);
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(realTopicDO);
        Mockito.when(appService.getByPrincipal(Mockito.anyString())).thenReturn(appDOList);

        Assert.assertTrue(topicManagerService.getMyTopics(ADMIN_NAME_IN_MYSQL).stream().allMatch(summary ->
                summary.getAccess().equals(mineTopicSummary.getAccess()) &&
                summary.getAppName().equals(mineTopicSummary.getAppName()) &&
                summary.getTopicName().equals(mineTopicSummary.getTopicName())));
    }


    @Test
    public void getTopicsTest() {
        // ClusterDO为空测试
        getTopic2clusterDOListIsEmptyTest();

        // appList为空测试
        getTopic2AppListIsEmptyTest();

        // 成功测试
        getTopic2SuccessTest();
    }


    private TopicDTO getTopicDTO() {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setAppId(KAFKA_MANAGER_APP_ID);
        topicDTO.setAppName(KAFKA_MANAGER_APP_NAME);
        topicDTO.setAppPrincipals(ADMIN_NAME_IN_MYSQL);
        topicDTO.setTopicName(REAL_TOPIC_IN_ZK);
        return topicDTO;
    }


    private void getTopic2clusterDOListIsEmptyTest() {
        Mockito.when(clusterService.listAll()).thenReturn(new ArrayList<>());
        Assert.assertTrue(topicManagerService.getTopics(ADMIN_NAME_IN_MYSQL).isEmpty());
    }

    private void getTopic2AppListIsEmptyTest() {
        List<ClusterDO> clusterDOList = new ArrayList<>();
        clusterDOList.add(new ClusterDO());
        Mockito.when(clusterService.listAll()).thenReturn(clusterDOList);
        Mockito.when(appService.listAll()).thenReturn(new ArrayList<>());
        Assert.assertTrue(topicManagerService.getTopics(ADMIN_NAME_IN_MYSQL).isEmpty());
    }

    private void getTopic2SuccessTest() {
        List<ClusterDO> clusterDOList = new ArrayList<>();
        ClusterDO clusterDO = getClusterDO();
        clusterDOList.add(clusterDO);

        List<AppDO> appDOList = new ArrayList<>();
        AppDO appDO = getAppDO();
        appDOList.add(appDO);

        TopicDTO topicDTO = getTopicDTO();

        LogicalClusterDO logicalClusterDO = getLogicalClusterDO();

        Mockito.when(clusterService.listAll()).thenReturn(clusterDOList);
        Mockito.when(appService.listAll()).thenReturn(appDOList);
        Mockito.when(logicalClusterMetadataManager.getTopicLogicalCluster(Mockito.anyLong(), Mockito.anyString())).thenReturn(logicalClusterDO);
        Assert.assertTrue(topicManagerService.getTopics(ADMIN_NAME_IN_MYSQL).stream().allMatch(dto ->
                dto.getAppId().equals(topicDTO.getAppId()) &&
                dto.getAppName().equals(topicDTO.getAppName()) &&
                dto.getTopicName().equals(topicDTO.getTopicName())));

    }

    @Test
    public void getTopicAuthorizedAppsTest() {
        // Topic不存在测试
        getTopicAuthorizedApps2TopicNotExistTest();

        // 没有权限测试
        getTopicAuthorizedApps2NoAuthorityTest();

        // 成功测试
        getTopicAuthorizedApps2successTest();

    }

    private void getTopicAuthorizedApps2TopicNotExistTest() {
        Assert.assertTrue(topicManagerService.getTopicAuthorizedApps(-1L, "notExistTopic").isEmpty());
    }

    private void getTopicAuthorizedApps2NoAuthorityTest() {
        Mockito.when(authorityService.getAuthorityByTopic(Mockito.anyLong(), Mockito.anyString())).thenReturn(new ArrayList<>());
        Assert.assertTrue(topicManagerService.getTopicAuthorizedApps(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK).isEmpty());
    }

    private void getTopicAuthorizedApps2successTest() {
        AuthorityDO authorityDO = getAuthorityDO();
        List<AuthorityDO> authorityDOList = new ArrayList<>();
        authorityDOList.add(authorityDO);
        Mockito.when(authorityService.getAuthorityByTopic(Mockito.anyLong(), Mockito.anyString())).thenReturn(authorityDOList);

        List<TopicThrottledMetrics> topicThrottledMetricsList = new ArrayList<>();
        TopicThrottledMetrics topicThrottledMetrics = getTopicThrottledMetrics();
        topicThrottledMetricsList.add(topicThrottledMetrics);
        Mockito.when(throttleService.getThrottledTopicsFromJmx(Mockito.anyLong(), Mockito.anySet(), Mockito.anyList())).thenReturn(topicThrottledMetricsList);

        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(appDO);

        TopicAppData topicAppData = getTopicAppData();

        Assert.assertTrue(topicManagerService.getTopicAuthorizedApps(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK).stream().allMatch(data ->
                data.getAccess().equals(topicAppData.getAccess()) &&
                data.getAppId().equals(topicAppData.getAppId()) &&
                data.getTopicName().equals(topicAppData.getTopicName()) &&
                data.getAppName().equals(topicAppData.getAppName()) &&
                data.getAppPrincipals().equals(topicAppData.getAppPrincipals())));
    }


    @Test
    public void getTopicMineAppsTest() {
        // topic不存在测试
        getTopicMineApps2TopicNotExistTest();

        // appDOList为空测试
        getTopicMineApps2AppDOListIsEmptyTest();

        // 成功测试
        getTopicMineApps2Success();
    }

    private void getTopicMineApps2TopicNotExistTest() {
        Assert.assertTrue(topicManagerService.getTopicMineApps(-1L, "notExistTopic", ADMIN_NAME_IN_MYSQL).isEmpty());
    }

    private void getTopicMineApps2AppDOListIsEmptyTest() {
        Mockito.when(appService.getByPrincipal(Mockito.anyString())).thenReturn(new ArrayList<>());
        Assert.assertTrue(topicManagerService.getTopicMineApps(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, ADMIN_NAME_IN_MYSQL).isEmpty());
    }

    private void getTopicMineApps2Success() {
        AppDO appDO = getAppDO();
        List<AppDO> appDOList = new ArrayList<>();
        appDOList.add(appDO);
        Mockito.when(appService.getByPrincipal(Mockito.anyString())).thenReturn(appDOList);

        AuthorityDO authorityDO = getAuthorityDO();
        List<AuthorityDO> authorityDOList = new ArrayList<>();
        authorityDOList.add(authorityDO);
        Mockito.when(authorityService.getAuthorityByTopic(Mockito.anyLong(), Mockito.anyString())).thenReturn(authorityDOList);

        List<TopicThrottledMetrics> topicThrottledMetricsList = new ArrayList<>();
        TopicThrottledMetrics topicThrottledMetrics = getTopicThrottledMetrics();
        topicThrottledMetricsList.add(topicThrottledMetrics);
        Mockito.when(throttleService.getThrottledTopicsFromJmx(Mockito.anyLong(), Mockito.anySet(), Mockito.anyList())).thenReturn(topicThrottledMetricsList);

        System.out.println(topicManagerService.getTopicMineApps(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, ADMIN_NAME_IN_MYSQL));
        TopicAppData topicAppData = getTopicAppData();
        Assert.assertFalse(topicManagerService.getTopicMineApps(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, ADMIN_NAME_IN_MYSQL).isEmpty());
    }


    @Test
    public void getRdTopicBasicTest() {
        // 集群不存在测试
        getRdTopicBasic2ClusterNotExistTest();

        // 成功测试
        getRdTopicBasic2SuccessTest();
    }

    private void getRdTopicBasic2ClusterNotExistTest() {
        Mockito.when(clusterService.getById(Mockito.anyLong())).thenReturn(null);
        Assert.assertEquals(topicManagerService.getRdTopicBasic(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK).toString(), Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST).toString());
    }

    private void getRdTopicBasic2SuccessTest() {
        ClusterDO clusterDO = getClusterDO();
        Mockito.when(clusterService.getById(REAL_CLUSTER_ID_IN_MYSQL)).thenReturn(clusterDO);

        RegionDO regionDO = getRegionDO();
        List<RegionDO> regionDOList = new ArrayList<>();
        regionDOList.add(regionDO);
        Mockito.when(regionService.getRegionListByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(regionDOList);

        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(appDO);

        TopicDO topicDO = getTopicDOInCluster();
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDO);

        RdTopicBasic actualResult = topicManagerService.getRdTopicBasic(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK).getData();
        RdTopicBasic expectedResult = getRdTopicBasic();
        Assert.assertNotNull(actualResult);
        Assert.assertTrue(actualResult.getAppId().equals(expectedResult.getAppId()) &&
                actualResult.getAppName().equals(expectedResult.getAppName()) &&
                actualResult.getRegionNameList().equals(expectedResult.getRegionNameList()));

    }

    @Test
    public void getTopicBusinessInfoTest() {
        // TopicDO为null测试
        getRdTopicBasic2SuccessTest();

        // AppDO为null测试
        getTopicBusinessInfo2AppDOIsNullTest();

        // 成功测试
        getTopicBusinessInfo2SuccessTest();
    }

    private void getTopicBusinessInfo2ReturnNullTest() {
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        Assert.assertNull(topicManagerService.getTopicBusinessInfo(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK));
    }

    private void getTopicBusinessInfo2AppDOIsNullTest() {
        TopicDO topicDO = getTopicDOInCluster();
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDO);
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(null);

        TopicBusinessInfo expected = getTopicBusinessInfo();
        expected.setAppId(null);
        expected.setAppName(null);
        expected.setPrincipals(null);
        Assert.assertEquals(topicManagerService.getTopicBusinessInfo(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK).toString(), expected.toString());
    }

    private void getTopicBusinessInfo2SuccessTest() {
        TopicDO topicDO = getTopicDOInCluster();
        Mockito.when(topicDao.getByTopicName(Mockito.anyLong(), Mockito.anyString())).thenReturn(topicDO);

        AppDO appDO = getAppDO();
        Mockito.when(appService.getByAppId(Mockito.anyString())).thenReturn(appDO);

        TopicBusinessInfo expected = getTopicBusinessInfo();
        Assert.assertEquals(topicManagerService.getTopicBusinessInfo(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK).toString(), expected.toString());
    }

    @Test
    public void getTopicStatisticTest() {
        TopicStatisticsDO topicStatisticsDO = getTopicStatisticsDO();
        List<TopicStatisticsDO> topicStatisticsDOList = new ArrayList<>();
        topicStatisticsDOList.add(topicStatisticsDO);

        Mockito.when(topicStatisticsDao.getTopicStatistic(Mockito.anyLong(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(topicStatisticsDOList);
        Assert.assertTrue(topicManagerService.getTopicStatistic(REAL_CLUSTER_ID_IN_MYSQL, REAL_TOPIC_IN_ZK, new Date(), new Date()).stream().allMatch(statisticsDO ->
                statisticsDO.getClusterId().equals(topicStatisticsDO.getClusterId()) &&
                statisticsDO.getMaxAvgBytesIn().equals(topicStatisticsDO.getMaxAvgBytesIn()) &&
                statisticsDO.getOffsetSum().equals(topicStatisticsDO.getOffsetSum())));
    }

    @Test
    public void addAuthorityTest() {
        // app不存在测试
        addAuthority2AppNotExistTest();
    }

    private void addAuthority2AppNotExistTest() {
        AuthorityDO authorityDO = getAuthorityDO();
        Assert.assertEquals(topicManagerService.addAuthority(authorityDO), ResultStatus.APP_NOT_EXIST);
    }





}
