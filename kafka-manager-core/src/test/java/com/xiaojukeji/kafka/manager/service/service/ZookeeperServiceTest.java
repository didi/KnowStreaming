package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;
import com.xiaojukeji.kafka.manager.service.config.BaseTest;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author xuguang
 * @Date 2021/12/9
 */
public class ZookeeperServiceTest extends BaseTest {

    @Autowired
    private ZookeeperService zookeeperService;

    @Value("${test.ZK.address}")
    private String ZOOKEEPER_ADDRESS;


    @DataProvider(name = "extendsAndCandidatesZnodeExist")
    public static Object[][] extendsAndCandidatesZnodeExist() {
        // zk中 config下extends节点是否存在，extends节点下candidates节点是否存在
            return new Object[][] {{false, false}, {false, true}, {true, false}, {true, true}};
    }

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(description = "开启JMX参数测试")
    public void openTopicJmxTest() {
        // 开启JMX参数有误
        openTopicJmx2ParamIllegalTest();
        // 开启JMX, 无topic
        openTopicJmx2TopicNotExistTest();
        // 开启JMX成功
        openTopicJmx2SuccessTest();
    }

    private void openTopicJmx2ParamIllegalTest() {
        Result result1 = zookeeperService.openTopicJmx(null, "xgTest", null);
        Assert.assertEquals(result1.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());

        Result result2 = zookeeperService.openTopicJmx(1L, null, null);
        Assert.assertEquals(result2.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void openTopicJmx2TopicNotExistTest() {
        Result result1 = zookeeperService.openTopicJmx(1L, "xgTestxxx",
                new TopicJmxSwitch(true, true, true));
        Assert.assertEquals(result1.getCode(), ResultStatus.TOPIC_NOT_EXIST.getCode());
    }

    private void openTopicJmx2SuccessTest() {
        Result result1 = zookeeperService.openTopicJmx(1L, "xgTest",
                new TopicJmxSwitch(true, true, true));
        Assert.assertEquals(result1.getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取优先被选举为controller的broker")
    public void getControllerPreferredCandidatesTest() throws ConfigException {
        // 获取优先被选举为controller的broker时参数错误
        getControllerPreferredCandidates2ParamIllegalTest();
        // 获取优先被选举为controller的broker时参数错误
        getControllerPreferredCandidates2ZookeeperConnectFailedTest();
        // 获取优先被选举为controller的broker时, zk路径不存在
        getControllerPreferredCandidates2NoZkRootTest();
        // 获取优先被选举为controller的broker时，broker为空
        getControllerPreferredCandidates2BrokerEmptyTest();
        // 获取优先被选举为controller的broker成功
        getControllerPreferredCandidates2SuccessTest();
    }

    private void getControllerPreferredCandidates2ParamIllegalTest() {
        Result<List<Integer>> brokerIds = zookeeperService.getControllerPreferredCandidates(null);
        Assert.assertEquals(brokerIds.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void getControllerPreferredCandidates2ZookeeperConnectFailedTest() {
        Result<List<Integer>> brokerIds = zookeeperService.getControllerPreferredCandidates(100L);
        Assert.assertEquals(brokerIds.getCode(), ResultStatus.ZOOKEEPER_CONNECT_FAILED.getCode());
    }

    private void getControllerPreferredCandidates2NoZkRootTest() {
        Result<List<Integer>> brokerIds = zookeeperService.getControllerPreferredCandidates(1L);
        Assert.assertEquals(brokerIds.getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertTrue(brokerIds.getData().isEmpty());
    }

    private void getControllerPreferredCandidates2BrokerEmptyTest() throws ConfigException {
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");

        Result<List<Integer>> brokerIds = zookeeperService.getControllerPreferredCandidates(1L);
        Assert.assertEquals(brokerIds.getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertTrue(brokerIds.getData().isEmpty());

        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES);
        zkConfig.delete(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE);
        zkConfig.close();
    }

    private void getControllerPreferredCandidates2SuccessTest() throws ConfigException {
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES + "/1", "");

        Result<List<Integer>> brokerIds = zookeeperService.getControllerPreferredCandidates(1L);
        Assert.assertEquals(brokerIds.getCode(), ResultStatus.SUCCESS.getCode());
        Assert.assertFalse(brokerIds.getData().isEmpty());
        Assert.assertEquals(brokerIds.getData().get(0), Integer.valueOf(1));

        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES + "/1");
        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES);
        zkConfig.delete(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE);
        zkConfig.close();
    }

    @Test(dataProvider = "extendsAndCandidatesZnodeExist", description = "增加优先被选举为controller的broker")
    public void addControllerPreferredCandidateTest(boolean extendsExist, boolean candidatesExist) throws ConfigException {
        // 增加优先被选举为controller的broker时参数错误
        addControllerPreferredCandidate2ParamIllegalTest();
        // 增加优先被选举为controller的broker时，zk无法连接
        addControllerPreferredCandidate2zkConnectFailedTest();
        // 增加优先被选举为controller的broker时，节点已经存在
        addControllerPreferredCandidate2zkExistTest();
        // 增加优先被选举为controller的broker成功，四种情况
        addControllerPreferredCandidate2SuccessTest(extendsExist, candidatesExist);
    }

    private void addControllerPreferredCandidate2ParamIllegalTest() {
        Result result = zookeeperService.addControllerPreferredCandidate(null, 100);
        Assert.assertEquals(result.getCode(),ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void addControllerPreferredCandidate2zkConnectFailedTest() {
        Result result = zookeeperService.addControllerPreferredCandidate(100L, 100);
        Assert.assertEquals(result.getCode(),ResultStatus.ZOOKEEPER_CONNECT_FAILED.getCode());
    }

    private void addControllerPreferredCandidate2zkExistTest() throws ConfigException {
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES + "/1", "");

        Result result = zookeeperService.addControllerPreferredCandidate(1L, 1);
        Assert.assertEquals(result.getCode(),ResultStatus.SUCCESS.getCode());

        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES + "/1");
        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES);
        zkConfig.delete(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE);
        zkConfig.close();
    }

    private void addControllerPreferredCandidate2SuccessTest(boolean extendsExist, boolean candidatesExist) throws ConfigException {
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        if (extendsExist) {
            zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
        }
        if (extendsExist && candidatesExist) {
            zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");
        }

        Result result = zookeeperService.addControllerPreferredCandidate(1L, 1);
        Assert.assertEquals(result.getCode(),ResultStatus.SUCCESS.getCode());

        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES + "/1");
        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES);
        zkConfig.delete(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE);
        zkConfig.close();
    }

    @Test(description = "减少优先被选举为controller的broker")
    public void deleteControllerPreferredCandidate() throws ConfigException {
        // 减少优先被选举为controller的broker时参数错误
        deleteControllerPreferredCandidate2ParamIllegalTest();
        // 减少优先被选举为controller的broker时，zk无法连接
        deleteControllerPreferredCandidate2zkConnectFailedTest();
        // 减少优先被选举为controller的broker时，节点已经存在
        addControllerPreferredCandidate2zkNodeNotExistTest();
        // 减少优先被选举为controller的broker成功
        addControllerPreferredCandidate2SuccessTest();
    }

    private void deleteControllerPreferredCandidate2ParamIllegalTest() {
        Result result = zookeeperService.deleteControllerPreferredCandidate(null, 100);
        Assert.assertEquals(result.getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void deleteControllerPreferredCandidate2zkConnectFailedTest() {
        Result result = zookeeperService.addControllerPreferredCandidate(100L, 100);
        Assert.assertEquals(result.getCode(), ResultStatus.ZOOKEEPER_CONNECT_FAILED.getCode());
    }

    private void addControllerPreferredCandidate2zkNodeNotExistTest() throws ConfigException {
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");

        Result result = zookeeperService.deleteControllerPreferredCandidate(1L, 1);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());

        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES);
        zkConfig.delete(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE);
        zkConfig.close();
    }

    private void addControllerPreferredCandidate2SuccessTest() throws ConfigException {
        ZkConfigImpl zkConfig = new ZkConfigImpl(ZOOKEEPER_ADDRESS);
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");
        zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES + "/1", "");

        Result result = zookeeperService.deleteControllerPreferredCandidate(1L, 1);
        Assert.assertEquals(result.getCode(), ResultStatus.SUCCESS.getCode());

        zkConfig.delete(ZkPathUtil.D_CONTROLLER_CANDIDATES);
        zkConfig.delete(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE);
        zkConfig.close();
    }
}
