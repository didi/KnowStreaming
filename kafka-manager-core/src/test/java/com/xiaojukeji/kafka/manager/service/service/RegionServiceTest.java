package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wyc
 * @date 2021/12/8
 */
public class RegionServiceTest extends  BaseTest{
    @Value("${test.phyCluster.id}")
    private Long REAL_CLUSTER_ID_IN_MYSQL;

    @Value("${test.region-name}")
    private String REAL_REGION_NAME_IN_CLUSTER;

    @Value("${test.topic.name1}")
    private String REAL_TOPIC1_IN_ZK;
    @Autowired
    private RegionService regionService;

    @DataProvider(name = "regionDO")
    public Object[][] provideRegionDO() {
        RegionDO regionDO = new RegionDO();
        regionDO.setStatus(0);
        regionDO.setName("region1");
        // 物理集群id
        regionDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        regionDO.setDescription("test");

        List<Integer> brokerIdList = new ArrayList<>();
        brokerIdList.add(3);
        regionDO.setBrokerList(ListUtils.intList2String(brokerIdList));

        return new Object[][] {{regionDO}};
    }

    private RegionDO getRegionDO() {
        RegionDO regionDO = new RegionDO();
        regionDO.setStatus(0);
        regionDO.setName("region1");
        // 物理集群id
        regionDO.setClusterId(REAL_CLUSTER_ID_IN_MYSQL);
        regionDO.setDescription("test");

        List<Integer> brokerIdList = new ArrayList<>();
        brokerIdList.add(3);
        regionDO.setBrokerList(ListUtils.intList2String(brokerIdList));
        return regionDO;
    }


    @Test(description = "creatRegion, 参数为null测试")
    public void createRegion2ParamIllegalTest() {
        Assert.assertEquals(regionService.createRegion(null), ResultStatus.PARAM_ILLEGAL);
    }

    @Test(description = "createRegion, 成功测试")
    public void createRegion2SuccessTest() {
        RegionDO regionDO = getRegionDO();
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);
    }

    @Test(description = "createRegion, clusterId为空测试")
    public void createRegion2ExistBrokerIdAlreadyInRegionTest1() {
        RegionDO regionDO = getRegionDO();
        regionDO.setClusterId(null);
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.RESOURCE_ALREADY_USED);
    }


    @Test(description = "createRegion, 创建时传入的brokerList中有被使用过的")
    public void createRegion2ExistBrokerIdAlreadyInRegionTest2() {
        RegionDO regionDO = getRegionDO();
        // 真实物理集群和数据库中region使用1,2broker
        // 再创建一个Region, 使用1,3broker
        List<Integer> newBrokerIdList = new ArrayList<>();
        newBrokerIdList.add(1);
        newBrokerIdList.add(3);
        regionDO.setBrokerList(ListUtils.intList2String(newBrokerIdList));
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.RESOURCE_ALREADY_USED);
    }

    @Test(description = "createRegion, 创建时，region使用到的broker挂掉了")
    public void createRegion2BrokerNotExistTest() {
        RegionDO regionDO = getRegionDO();
        // 传入一个不存在的物理集群，检测时，会认为该集群存活的broker个数为0
        regionDO.setClusterId(-1L);
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.BROKER_NOT_EXIST);
    }

    @Test(description = "createRegion, 创建时，regionName重复")
    public void createRegion2ResourceAlreadyExistTest() {
        RegionDO regionDO = getRegionDO();
        // 插入同名Region，注意brokerList需要保持不一样，不然会返回RESOURCE_ALREADY_USED
        regionDO.setName(REAL_REGION_NAME_IN_CLUSTER);
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.RESOURCE_ALREADY_EXISTED);
    }


    @Test
    public void deleteByIdTest() {
        RegionDO regionDO = getRegionDO();
        // 参数非法测试
        deleteById2ParamIllegalTest(regionDO);

        // 资源不存在测试
        deleteById2ResourceNotExistTest(regionDO);

        // 删除成功测试
        deleteById2SuccessTest(regionDO);

    }

    private void deleteById2ParamIllegalTest(RegionDO regionDO) {
        Assert.assertEquals(regionService.deleteById(null), ResultStatus.PARAM_ILLEGAL);
    }

    private void deleteById2ResourceNotExistTest(RegionDO regionDO) {
        Assert.assertEquals(regionService.deleteById(10L), ResultStatus.RESOURCE_NOT_EXIST);
    }

    private void deleteById2SuccessTest(RegionDO regionDO) {
        regionDO.setId(1L);
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);

        // 插入时，xml文件中没用到id，id交给数据库自增，因此需要先查出Region的id，再根据id删除
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO region = regionDOList.get(0);
        Assert.assertEquals(regionService.deleteById(region.getId()), ResultStatus.SUCCESS);
    }


    @Test(description = "updateRegion, 参数非法测试")
    public void updateRegion2ParamIllegalTest1() {
        RegionDO regionDO = getRegionDO();
        Assert.assertEquals(regionService.updateRegion(null), ResultStatus.PARAM_ILLEGAL);
        Assert.assertEquals(regionService.updateRegion(regionDO), ResultStatus.PARAM_ILLEGAL);
    }

    @Test(description = "updateRegion, 资源不存在测试")
    public void updateRegion2ResourceNotExistTest1() {
        RegionDO regionDO = getRegionDO();
        // 不插入Region，直接更新
        regionDO.setId(-1L);
        Assert.assertEquals(regionService.updateRegion(regionDO), ResultStatus.RESOURCE_NOT_EXIST);
    }

    @Test(description = "updateRegion, brokerList未改变，成功测试")
    public void updateRegion2SuccessWithBrokerListNotChangeTest1() {
        RegionDO regionDO = getRegionDO();
        // 先在数据库中创建一个Region
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);

        // 查询出创建的Region,并修改一些参数后，作为新的Region
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO newRegionDO = regionDOList.get(0);
        newRegionDO.setStatus(1);

        Assert.assertEquals(regionService.updateRegion(newRegionDO), ResultStatus.SUCCESS);
    }

    @Test(description = "updateRegion, 传入的broker已经被使用测试")
    public void updateRegion2ResourceAlreadyUsedTest1() {
        RegionDO regionDO = getRegionDO();
        // 先在数据库中创建一个Region
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);

        // 查询出创建的Region,并修改brokerList后，作为新的Region
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO newRegionDO = regionDOList.get(0);

        List<Integer> newBrokerIdList = new ArrayList<>();
        newBrokerIdList.add(1);
        newBrokerIdList.add(3);

        // 更新Region的brokerList
        newRegionDO.setBrokerList(ListUtils.intList2String(newBrokerIdList));
        // 构造情况
        newRegionDO.setClusterId(null);
        Assert.assertEquals(regionService.updateRegion(newRegionDO), ResultStatus.RESOURCE_ALREADY_USED);
    }

    @Test(description = "updateRegion, 更新的broker不存在")
    public void updateRegion2BrokerNotExistTest1() {
        RegionDO regionDO = getRegionDO();
        // 先在数据库中创建一个Region
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);

        // 查询出创建的Region,并修改brokerList后，作为新的Region
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO newRegionDO = regionDOList.get(0);

        // 构造情况
        List<Integer> newBrokerIdList = new ArrayList<>();
        newBrokerIdList.add(4);
        newBrokerIdList.add(5);
        newRegionDO.setBrokerList(ListUtils.intList2String(newBrokerIdList));

        Assert.assertEquals(regionService.updateRegion(newRegionDO), ResultStatus.BROKER_NOT_EXIST);
    }


    @Test(description = "updateRegion, brokeList发生了改变，成功测试")
    public void updateRegion2SuccessWithBrokerListChangeTest1() {
        RegionDO regionDO = getRegionDO();
        // 查询出创建的Region,并修改brokerList后，作为新的Region
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO newRegionDO = regionDOList.get(0);

        // 构造情况
        List<Integer> newBrokerIdList = new ArrayList<>();
        newBrokerIdList.add(1);
        newBrokerIdList.add(3);
        newRegionDO.setBrokerList(ListUtils.intList2String(newBrokerIdList));

        Assert.assertEquals(regionService.updateRegion(newRegionDO), ResultStatus.SUCCESS);
    }

    @Test(description = "updateRegion重载方法，参数非法测试")
    public void updateRegion2ParamIllegalTest2() {
        RegionDO regionDO = getRegionDO();
        Assert.assertEquals(regionService.updateRegion(null, "1,3"), ResultStatus.PARAM_ILLEGAL);
        Assert.assertEquals(regionService.updateRegion(1L, "1, 3"), ResultStatus.PARAM_ILLEGAL);
    }

    @Test(description = "updateRegion重载方法，成功测试")
    public void updateRegion2SuccessTest2() {
        RegionDO regionDO = getRegionDO();
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO region = regionDOList.get(0);
        Assert.assertEquals(regionService.updateRegion(region.getId(), "1,3"), ResultStatus.SUCCESS);
    }


    @Test
    public void updateCapacityByIdTest() {
        RegionDO regionDO = getRegionDO();
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);
        RegionDO region = regionService.getByClusterId(1L).get(0);
        region.setCapacity(1000L);

        // 成功测试
        updateCapacityById2SuccessTest(region);

        // 失败测试
        // 集群中不存在regionId是100的
        region.setId(100L);
        updateCapacityByIdFailureTest(region);
    }

    private void updateCapacityById2SuccessTest(RegionDO regionDO) {
        Assert.assertEquals(regionService.updateCapacityById(regionDO), 1);
    }

    private void updateCapacityByIdFailureTest(RegionDO regionDO) {
        Assert.assertEquals(regionService.updateCapacityById(regionDO), 0);
    }


    @Test
    public void getByIdTest() {
        RegionDO regionDO = getRegionDO();
        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);

        // 获取成功测试
        RegionDO region = regionService.getByClusterId(1L).get(0);
        getById2SuccessTest(region);

        // 获取失败测试
        region.setId(-1L);
        getById2FailureTest(region);

    }

    private void getById2SuccessTest(RegionDO regionDO) {
        Assert.assertEquals(regionService.getById(regionDO.getId()).toString(), regionDO.toString());
    }

    private void getById2FailureTest(RegionDO regionDO) {
        Assert.assertNull(regionService.getById(regionDO.getId()));
    }

    private void getByClusterId2SuccessTest(RegionDO regionDO) {
        Assert.assertNotNull(regionService.getByClusterId(regionDO.getClusterId()));
        Assert.assertTrue(regionService.getByClusterId(regionDO.getClusterId()).stream().allMatch(regionDO1 ->
                regionDO1.getName().equals(regionDO.getName())));
    }

    private void getByClusterId2FailureTest(RegionDO regionDO) {
        Assert.assertTrue(regionService.getByClusterId(-1L).isEmpty());
    }

    @Test(dataProvider = "regionDO")
    public void getRegionNumTest(RegionDO regionDO) {
        // 插入一条数据
        Map<Long, Integer> regionNum = regionService.getRegionNum();
        for(Map.Entry<Long, Integer> entry : regionNum.entrySet()) {
            Assert.assertEquals(entry.getKey(), Long.valueOf(1));
            Assert.assertEquals(entry.getValue(), Integer.valueOf(1));
        }
    }

    @Test(dataProvider = "regionDO")
    public void getFullBrokerIdListTest(RegionDO regionDO) {
        List<Integer> brokerIdList = new ArrayList<>();
        brokerIdList.add(3);

        // regionId是null测试
        getFullBrokerIdList2RegionIdIsNullTest(regionDO, brokerIdList);

        // 数据库中不存在对应的regionId数据
        getFullBrokerIdList2RegionNotExistTest(regionDO, brokerIdList);

        Assert.assertEquals(regionService.createRegion(regionDO), ResultStatus.SUCCESS);
        RegionDO region = regionService.getByClusterId(1L).get(0);
        // 传进来的brokerList是空的
        getFullBrokerIdList2BrokerIdListIsEmpty(regionDO, region, new ArrayList<>());

        // 传进来的brokerList不是空的
        getFullBrokerIdList2Success(regionDO, region, brokerIdList);

    }

    private void getFullBrokerIdList2RegionIdIsNullTest(RegionDO regionDO, List<Integer> brokerIdList) {
        List<Integer> fullBrokerIdList = regionService.getFullBrokerIdList(1L, null, brokerIdList);
        Assert.assertEquals(fullBrokerIdList, brokerIdList);
    }

    private void getFullBrokerIdList2RegionNotExistTest(RegionDO regionDO, List<Integer> brokerIdList) {
        Assert.assertEquals(regionService.getFullBrokerIdList(1L, -1L, brokerIdList), brokerIdList);
    }

    private void getFullBrokerIdList2BrokerIdListIsEmpty(RegionDO regionDO, RegionDO regionInDataBase, List<Integer> brokerIdList) {
        List<Integer> fullBrokerIdList = regionService.getFullBrokerIdList(1L, regionInDataBase.getId(), brokerIdList);
        Assert.assertEquals(fullBrokerIdList, ListUtils.string2IntList(regionInDataBase.getBrokerList()));
    }

    private void getFullBrokerIdList2Success(RegionDO regionDO, RegionDO regionInDataBase, List<Integer> brokerIdList) {
        List<Integer> fullBrokerIdList = regionService.getFullBrokerIdList(1L, regionInDataBase.getId(), brokerIdList);
        List<Integer> allBrokerIdList = ListUtils.string2IntList(regionInDataBase.getBrokerList());
        allBrokerIdList.addAll(brokerIdList);
        Assert.assertEquals(allBrokerIdList, fullBrokerIdList);
    }

    private void convert2BrokerIdRegionMap2RegionListDOIsNull() {
        Assert.assertTrue(regionService.convert2BrokerIdRegionMap(null).isEmpty());
    }

    private void convert2BrokerIdRegionMap2Success(RegionDO regionDO) {
        // 预期结果, key是brokerId, value是Region
        List<RegionDO> regionDOList = regionService.getByClusterId(1L);
        RegionDO region = regionDOList.get(0);
        Map<Integer, RegionDO> brokerIdRegionDOMap = ListUtils.string2IntList(regionDO.getBrokerList()).stream().collect(Collectors.toMap(brokerId -> brokerId, regionDO1 -> region));


        // 实际结果
        Map<Integer, RegionDO> result = regionService.convert2BrokerIdRegionMap(regionDOList);
        Assert.assertEquals(brokerIdRegionDOMap, result);
    }


    @Test(dataProvider = "regionDO")
    public void getIdleRegionBrokerListTest(RegionDO regionDO) {
        // 物理集群id和regionIdList是null测试
        getIdleRegionBrokerList2PhysicalClusterIdIsNullTest();

        // 参数物理集群下的regionDOList为空测试
        getIdleRegionBrokerList2RegionDOListIsEmptyTest();

        // 成功测试
        getIdleRegionBrokerList2SuccessTest(regionDO);
    }

    private void getIdleRegionBrokerList2PhysicalClusterIdIsNullTest() {
        Assert.assertNull(regionService.getIdleRegionBrokerList(null, new ArrayList<>()));
    }

    private void getIdleRegionBrokerList2RegionDOListIsEmptyTest() {
        List<Long> regionIdList = new ArrayList<>();
        regionIdList.add(-1L);
        Assert.assertNull(regionService.getIdleRegionBrokerList(1L, regionIdList));
    }

    private void getIdleRegionBrokerList2SuccessTest(RegionDO regionDO) {
        // 从数据库中查找
        List<Long> regionIdList = regionService.getByClusterId(1L).stream().map(RegionDO::getId).collect(Collectors.toList());
        List<Integer> brokerIdList = regionService.getByClusterId(1L)
                .stream().flatMap(regionDO1 -> ListUtils.string2IntList(regionDO1.getBrokerList()).stream())
                .collect(Collectors.toList());
        Assert.assertEquals(regionService.getIdleRegionBrokerList(1L, regionIdList), brokerIdList);
    }

    @Test
    public void getTopicNameRegionBrokerIdMap2SuccessTest() {
        // 创建逻辑集群，创建Topic，均已在数据库写入
        // 逻辑集群基于物理集群1建立，region的brokerList是1，2
        // Topic基于region建立，也就是使用到broker1和2

        // 这个方法是返回topicName -> topic所使用broker以及这些broker所在region中所有的broker
        Map<String, Set<Integer>> topicNameRegionBrokerIdMap = regionService.getTopicNameRegionBrokerIdMap(1L);
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        Assert.assertEquals(topicNameRegionBrokerIdMap.get(REAL_TOPIC1_IN_ZK), set);
    }

    @Test
    public void getRegionListByTopicNameTest() {
        // 数据库中依然建立了Region, LogicalCluster, Topic
        getRegionListByTopicName2EmptyTest();

        // 返回集合不为空测试
        getRegionListByTopicName2Success();
    }

    private void getRegionListByTopicName2EmptyTest() {
        // 传入一个不存在的topic
        Assert.assertEquals(regionService.getRegionListByTopicName(1L, "notExistTopic"), new ArrayList<>());
    }

    private void getRegionListByTopicName2Success() {
        List<RegionDO> expectedResult = regionService.getByClusterId(1L);
        Assert.assertEquals(regionService.getRegionListByTopicName(1L, REAL_TOPIC1_IN_ZK), expectedResult);
    }
}
