package com.xiaojukeji.kafka.manager.service.service;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.CreateTopicConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.CreateTopicElemConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.TopicExpiredConfig;
import com.xiaojukeji.kafka.manager.common.entity.dto.config.ConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyc
 * @date 2021/12/9
 */
public class ConfigServiceTest extends BaseTest {
    @Autowired
    private ConfigService configService;

    @DataProvider(name = "configDTO")
    public Object[][] provideConfigDO() {
        ConfigDTO dto = new ConfigDTO();
        dto.setConfigKey("key1");
        dto.setConfigValue("value1");
        dto.setConfigDescription("test");

        return new Object[][] {{dto}};
    }

    public ConfigDTO getConfigDTO() {
        ConfigDTO dto = new ConfigDTO();
        dto.setConfigKey("key1");
        dto.setConfigValue("value1");
        dto.setConfigDescription("test");

        return dto;
    }

    @Test(dataProvider = "configDTO")
    public void insertTest(ConfigDTO dto) {
        // 插入时，MySQL错误
        insert2MySQLErrorTest(dto);

        // 插入成功测试
        insert2SuccessTest(dto);

        // 插入时，资源已存在测试
        insert2ResourceExistedTest(dto);
    }

    private void insert2SuccessTest(ConfigDTO dto) {
        dto.setConfigKey("key1");
        ResultStatus result = configService.insert(dto);
        Assert.assertEquals(result, ResultStatus.SUCCESS);
    }

    private void insert2ResourceExistedTest(ConfigDTO dto2) {
        ResultStatus result2 = configService.insert(dto2);
        Assert.assertEquals(result2, ResultStatus.RESOURCE_ALREADY_EXISTED);
    }

    private void insert2MySQLErrorTest(ConfigDTO dto) {
        dto.setConfigKey(null);
        ResultStatus result = configService.insert(dto);
        Assert.assertEquals(result, ResultStatus.MYSQL_ERROR);
    }


    @Test
    public void deleteByKeyTest() {
        // deleteByKey, key时null
        deleteByKey2NullTest();

        // deleteByKey, 配置不存在测试
        deleteByKey2ConfigNotExistTest();

        // deleteByKey, 成功测试
        deleteByKey2SuccessTest();
    }

    private void deleteByKey2NullTest() {
        ResultStatus result = configService.deleteByKey(null);
        Assert.assertEquals(result, ResultStatus.PARAM_ILLEGAL);
    }

    private void deleteByKey2SuccessTest() {
        ConfigDTO dto = getConfigDTO();
        ResultStatus insertResult = configService.insert(dto);
        Assert.assertEquals(insertResult, ResultStatus.SUCCESS);

        ResultStatus deleteResult = configService.deleteByKey(dto.getConfigKey());
        Assert.assertEquals(deleteResult, ResultStatus.SUCCESS);
    }

    private void deleteByKey2ConfigNotExistTest() {
        ResultStatus result = configService.deleteByKey("key");
        Assert.assertEquals(result, ResultStatus.CONFIG_NOT_EXIST);
    }

    @Test(dataProvider = "configDTO")
    public void updateByKeyTest(ConfigDTO dto) {
        configService.insert(dto);

        // updateByKey, 成功测试
        updateByKey2SuccessTest(dto);

        // updateByKey, 配置不存在测试
        updateByKey2ConfigNotExistTest(dto);
    }

    private void updateByKey2SuccessTest(ConfigDTO dto) {
        dto.setConfigValue("newValue");
        ResultStatus updateResult = configService.updateByKey(dto);
        Assert.assertEquals(updateResult, ResultStatus.SUCCESS);
    }

    @Test(dataProvider = "configDTO", description = "updateByKey, 配置不存在测试")
    private void updateByKey2ConfigNotExistTest(ConfigDTO dto) {
        dto.setConfigKey("newKey");
        ResultStatus updateResult = configService.updateByKey(dto);
        Assert.assertEquals(updateResult, ResultStatus.CONFIG_NOT_EXIST);
    }


    @Test(dataProvider = "configDTO")
    public void updateByKeyTest2(ConfigDTO dto) {
        configService.insert(dto);

        // updateByKey重载方法，成功测试
        updateByKey2SuccessTest1(dto);

        // updateByKey重载方法，资源不存在测试
        updateByKey2ConfigNotExistTest1(dto);

    }

    private void updateByKey2SuccessTest1(ConfigDTO dto) {
        String key = dto.getConfigKey();
        String value = "newValue";
        Assert.assertEquals(configService.updateByKey(key, value), ResultStatus.SUCCESS);
    }

    private void updateByKey2ConfigNotExistTest1(ConfigDTO dto) {
        Assert.assertEquals(configService.updateByKey("key2", "newValue"), ResultStatus.CONFIG_NOT_EXIST);
    }



    @Test(dataProvider = "configDTO")
    public void getByKeyTest(ConfigDTO dto) {
        configService.insert(dto);

        // getByKey, 成功测试
        getByKey2SuccessTest(dto);

        // getByKey, 获取失败测试
        getByKey2NullTest();
    }

    private void getByKey2SuccessTest(ConfigDTO dto) {
        ConfigDO result = configService.getByKey(dto.getConfigKey());
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getConfigKey().equals(dto.getConfigKey()) &&
                result.getConfigValue().equals(dto.getConfigValue()) &&
                result.getConfigDescription().equals(dto.getConfigDescription()));
    }

    private void getByKey2NullTest() {
        Assert.assertNull(configService.getByKey("key2"));
    }



    @Test(dataProvider = "configDTO")
    public void getByKeyTest2(ConfigDTO dto) {
        // 需要用到TopicExpiredConfig类
        TopicExpiredConfig config = getTopicExpiredConfig();
        dto.setConfigValue(JSON.toJSONString(config));
        Assert.assertEquals(configService.insert(dto), ResultStatus.SUCCESS);

        // getByKey, 成功测试
        getByKey2SuccessTest1(dto);

        // getByKey, 返回null测试
        getByKey2NullTest1(dto);
    }

    private TopicExpiredConfig getTopicExpiredConfig() {
        TopicExpiredConfig config = new TopicExpiredConfig();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        config.setIgnoreClusterIdList(list);
        return config;
    }

    private void getByKey2SuccessTest1(ConfigDTO dto) {
        TopicExpiredConfig result = configService.getByKey(dto.getConfigKey(), TopicExpiredConfig.class);
        Assert.assertEquals(result.toString(), getTopicExpiredConfig().toString());
    }

    private void getByKey2NullTest1(ConfigDTO dto) {
        Assert.assertNull(configService.getByKey("key", TopicExpiredConfig.class));
    }




    @Test(dataProvider = "configDTO")
    public void getArrayByKeyTest(ConfigDTO dto) {
        dto.setConfigValue(JSON.toJSONString(getStringArray()));
        Assert.assertEquals(configService.insert(dto), ResultStatus.SUCCESS);

        // getArrayByKey 成功测试
        getArrayByKey2SuccessTest(dto);

        // getArrayByKey 返回null测试
        getArrayByKey2NullTest();
    }

    private List<String> getStringArray() {
        List<String> list = new ArrayList<>();
        list.add("value1");
        list.add("value2");
        list.add("value3");
        return list;
    }

    private void getArrayByKey2SuccessTest(ConfigDTO dto) {
        List<String> result = configService.getArrayByKey(dto.getConfigKey(), String.class);
        Assert.assertEquals(result, getStringArray());
    }


    private void getArrayByKey2NullTest() {
        Assert.assertNull(configService.getArrayByKey(null, String.class));
    }



    @Test(dataProvider = "configDTO", description = "getLongValue, 成功测试")
    public void getLongValue2SuccessTest(ConfigDTO dto) {
        dto.setConfigValue("100");
        Assert.assertEquals(configService.insert(dto), ResultStatus.SUCCESS);
        Assert.assertEquals(configService.getLongValue(dto.getConfigKey(), 0L), Long.valueOf(dto.getConfigValue()));
    }

    @Test(description = "getLongValue, 不存在key，返回默认值测试")
    public void getLongValue2NotExistTest() {
        Assert.assertEquals(configService.getLongValue("key", 100L), Long.valueOf(100L));
    }

    @Test(dataProvider = "configDTO", description = "getLongValue, 存在key但是value是null")
    public void getLongValue2ValueIsNull(ConfigDTO dto) {
        dto.setConfigValue(null);
        Assert.assertEquals(configService.insert(dto), ResultStatus.SUCCESS);

        Assert.assertEquals(configService.getLongValue(dto.getConfigKey(), 100L), Long.valueOf(100L));
    }


    @Test(dataProvider = "configDTO", description = "listAll, 成功测试")
    public void listAll2SuccessTest(ConfigDTO dto) {
        Assert.assertEquals(configService.insert(dto), ResultStatus.SUCCESS);
        List<ConfigDO> result = configService.listAll();
        Assert.assertNotNull(result);

        List<ConfigDTO> list = new ArrayList<>();
        list.add(dto);

        // 判断key字段是否相同
        Assert.assertEquals(result.stream().map(ConfigDO::getConfigKey).collect(Collectors.toList()),
                list.stream().map(ConfigDTO::getConfigKey).collect(Collectors.toList()));

        Assert.assertEquals(result.stream().map(ConfigDO::getConfigValue).collect(Collectors.toList()),
                list.stream().map(ConfigDTO::getConfigValue).collect(Collectors.toList()));

    }


    public CreateTopicConfig getCreateTopicConfig() {
        return new CreateTopicConfig();
    }

    public ConfigDTO getConfigDTO1() {
        ConfigDTO dto = new ConfigDTO();
        dto.setConfigKey(TopicCreationConstant.INNER_CREATE_TOPIC_CONFIG_KEY);
        dto.setConfigValue(JSON.toJSONString(getCreateTopicConfig()));
        dto.setConfigDescription("test");
        return dto;
    }

    @Test(description = "getAutoPassedTopicApplyOrderNumPerTask, config表中不存在INNER_CREATE_TOPIC_CONFIG_KEY" +
            "对应的记录，返回默认值测试")
    public void getAutoPassedTopicApplyOrderNumPerTask2NotExistTest() {
        Assert.assertEquals(configService.getAutoPassedTopicApplyOrderNumPerTask(), TopicCreationConstant.DEFAULT_MAX_PASSED_ORDER_NUM_PER_TASK);
    }

    @Test(description = "getAutoPassedTopicApplyOrderNumPerTask, 查到的记录中，记录的maxPassedOrderNumPerTask属性为null测试")
    public void getAutoPassedTopicApplyOrderNumPerTask2NullTest() {
        configService.insert(getConfigDTO1());
        Assert.assertEquals(configService.getAutoPassedTopicApplyOrderNumPerTask(), TopicCreationConstant.DEFAULT_MAX_PASSED_ORDER_NUM_PER_TASK);
    }


    @Test(description = "getAutoPassedTopicApplyOrderNumPerTask, 查到的记录中，记录的maxPassedOrderNumPerTask" +
            "比TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK大时测试")
    public void getAutoPassedTopicApplyOrderNumPerTask2BiggerMaxTest() {
        ConfigDTO configDTO = getConfigDTO1();
        CreateTopicConfig createTopicConfig = getCreateTopicConfig();
        createTopicConfig.setMaxPassedOrderNumPerTask(TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK + 10);
        configDTO.setConfigValue(JSON.toJSONString(createTopicConfig));

        configService.insert(configDTO);
        Assert.assertEquals(configService.getAutoPassedTopicApplyOrderNumPerTask(), TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK);
    }

    @Test(description = "getAutoPassedTopicApplyOrderNumPerTask, 查到的记录中，记录的maxPassedOrderNumPerTask" +
            "比TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK小时测试")
    public void getAutoPassedTopicApplyOrderNumPerTask2SmallerMaxTest() {
        ConfigDTO configDTO = getConfigDTO1();
        CreateTopicConfig createTopicConfig = getCreateTopicConfig();
        int val = TopicCreationConstant.MAX_PASSED_ORDER_NUM_PER_TASK - 10;
        createTopicConfig.setMaxPassedOrderNumPerTask(val);
        configDTO.setConfigValue(JSON.toJSONString(createTopicConfig));

        configService.insert(configDTO);
        Assert.assertEquals(configService.getAutoPassedTopicApplyOrderNumPerTask(), Integer.valueOf(val));
    }






    public CreateTopicElemConfig getCreateTopicElemConfig(Long clusterId) {
        CreateTopicElemConfig config = new CreateTopicElemConfig();
        config.setClusterId(clusterId);
        config.setBrokerIdList(new ArrayList<>());
        config.setRegionIdList(new ArrayList<>());
        config.setPartitionNum(TopicCreationConstant.DEFAULT_PARTITION_NUM);
        config.setReplicaNum(TopicCreationConstant.DEFAULT_REPLICA);
        config.setRetentionTimeUnitHour(TopicCreationConstant.DEFAULT_RETENTION_TIME_UNIT_HOUR);
        config.setAutoExecMaxPeakBytesInUnitB(TopicCreationConstant.AUTO_EXEC_MAX_BYTES_IN_UNIT_B);
        return config;
    }

    @Test(description = "getCreateTopicConfig, config表中不存在key时测试")
    public void getCreateTopicConfig2NotExistKeyTest() {
        CreateTopicElemConfig createTopicElemConfig = getCreateTopicElemConfig(10L);
        Assert.assertEquals(configService.getCreateTopicConfig(10L, "systemCode").toString(), createTopicElemConfig.toString());
    }

    @Test(description = "getCreateTopicConfig, value中存在和clusterId一致的记录")
    public void getCreateTopicConfig2ExistTest() {
        CreateTopicElemConfig createTopicElemConfig = getCreateTopicElemConfig(10L);
        createTopicElemConfig.setReplicaNum(4);
        List<CreateTopicElemConfig> list = new ArrayList<>();
        list.add(createTopicElemConfig);

        CreateTopicConfig createTopicConfig = getCreateTopicConfig();
        createTopicConfig.setConfigList(list);

        ConfigDTO configDTO = getConfigDTO1();
        configDTO.setConfigValue(JSON.toJSONString(createTopicConfig));
        configService.insert(configDTO);

        Assert.assertEquals(configService.getCreateTopicConfig(10L, "systemCode").toString(), createTopicElemConfig.toString());
    }

    @Test(description = "getCreateTopicConfig, value中不存在和clusterId一致的记录")
    public void getCreateTopicConfig2NotExitConfigEleTest() {
        CreateTopicElemConfig createTopicElemConfig = getCreateTopicElemConfig(11L);
        createTopicElemConfig.setReplicaNum(4);
        List<CreateTopicElemConfig> list = new ArrayList<>();
        list.add(createTopicElemConfig);

        CreateTopicConfig createTopicConfig = getCreateTopicConfig();
        createTopicConfig.setConfigList(list);

        ConfigDTO configDTO = getConfigDTO1();
        configDTO.setConfigValue(JSON.toJSONString(createTopicConfig));
        configService.insert(configDTO);

        Assert.assertEquals(configService.getCreateTopicConfig(10L, "systemCode").toString(), getCreateTopicElemConfig(10L).toString());
    }




    public ConfigDTO getConfigDTO2() {
        ConfigDTO dto = new ConfigDTO();
        dto.setConfigKey(ConfigConstant.KAFKA_CLUSTER_DO_CONFIG_KEY);
        dto.setConfigDescription("test");
        return dto;
    }
    public ConfigDO getConfigDO() {
        return new ConfigDO();
    }

    @Test(description = "getClusterDO, config表中不存在ConfigConstant.KAFKA_CLUSTER_DO_CONFIG_KEY这个key")
    public void getClusterDO2NotExistKeyTest() {
        Assert.assertNull(configService.getClusterDO(10L));
    }

    @Test(description = "getClusterDO, config表中key对应的value没法解析成ConfigDO测试")
    public void getClusterDO2ParseFailTest() {
        ConfigDTO configDTO2 = getConfigDTO2();
        configDTO2.setConfigValue("value");
        configService.insert(configDTO2);

        Assert.assertNull(configService.getClusterDO(10L));
    }
    public List<ClusterDO> getClusterDOList() {
        ClusterDO clusterDO1 = new ClusterDO();
        clusterDO1.setId(10L);
        clusterDO1.setClusterName("test1");
        ClusterDO clusterDO2 = new ClusterDO();
        clusterDO2.setId(20L);
        clusterDO2.setClusterName("test2");
        List<ClusterDO> list = new ArrayList<>();
        list.add(clusterDO1);
        list.add(clusterDO2);
        return list;
    }

    @Test(description = "getClusterDO, 成功查到测试")
    public void getClusterDO2SuccessTest() {
        ConfigDTO configDTO2 = getConfigDTO2();
        List<ClusterDO> clusterDOList = getClusterDOList();
        configDTO2.setConfigValue(JSON.toJSONString(clusterDOList));
        configService.insert(configDTO2);
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setId(20L);
        clusterDO.setClusterName("test2");

        Assert.assertEquals(configService.getClusterDO(20L), clusterDO);
    }


}
