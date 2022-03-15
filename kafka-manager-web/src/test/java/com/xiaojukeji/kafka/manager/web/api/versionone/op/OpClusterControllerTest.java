package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.ControllerPreferredCandidateDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.ClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterDetailVO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuguang
 * @Date 2022/1/13
 */
public class OpClusterControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        String url = baseUrl + "/api/v1/op/clusters";
        // 接入成功
        addNewCluter1Test(url);
    }

    @AfterClass
    public void deleteCluster() {
        String url = baseUrl + "/api/v1/op/clusters";
        // 删除集群成功
        deleteCluster1Test(url);
    }

    private Long getPhysicalClusterId() {
        String url = baseUrl + "/api/v1/rd/clusters/basic-info?need-detail=true";
        String clusterName = configMap.get(ConfigConstant.CLUSTER_NAME);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        String s = JSON.toJSONString(result.getBody().getData());
        List<ClusterDetailVO> clusterDetailVOS = JSON.parseArray(s, ClusterDetailVO.class);
        for (ClusterDetailVO clusterDetailVO : clusterDetailVOS) {
            if (clusterDetailVO.getClusterName().equals(clusterName)) {
                return clusterDetailVO.getClusterId();
            }
        }
        return null;
    }

    private ClusterDTO getClusterDTO() {
        ClusterDTO clusterDTO = new ClusterDTO();
        Long physicalClusterId = getPhysicalClusterId();
        clusterDTO.setClusterId(physicalClusterId);
        clusterDTO.setClusterName(configMap.get(ConfigConstant.CLUSTER_NAME));
        clusterDTO.setZookeeper(configMap.get(ConfigConstant.ZOOKEEPER_ADDRESS));
        clusterDTO.setBootstrapServers(configMap.get(ConfigConstant.BOOTSTRAP_ADDRESS));
        clusterDTO.setIdc(ConfigConstant.IDC);
        return clusterDTO;
    }

    @Test(description = "测试接入集群")
    public void addNewClusterTest() {
        String url = baseUrl + "/api/v1/op/clusters";

        // 参数无效
        addNewCluster2Test(url);
        // 无效的zk地址
        addNewCluster3Test(url);
        // 重复创建
        addNewCluster4Test(url);
    }

    private void addNewCluter1Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addNewCluster2Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setZookeeper(null);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void addNewCluster3Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setZookeeper(ConfigConstant.INVALID_STRING);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.ZOOKEEPER_CONNECT_FAILED.getCode());
    }

    private void addNewCluster4Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.RESOURCE_ALREADY_EXISTED.getCode());
    }

    @Test(description = "测试修改物理集群")
    public void modifyClusterTest() {
        String url =  baseUrl + "/api/v1/op/clusters";
        // 修改成功
        modifyCluster1Test(url);
        // 参数错误
        modifyCluster2Test(url);
        // 集群不存在
        modifyCluster3Test(url);
        // 不能修改zk地址
        modifyCluster4Test(url);
    }

    private void modifyCluster1Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void modifyCluster2Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setClusterId(null);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void modifyCluster3Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setClusterId(ConfigConstant.INVALID_CLUSTER_ID);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    private void modifyCluster4Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setZookeeper(ConfigConstant.INVALID_STRING);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CHANGE_ZOOKEEPER_FORBIDDEN.getCode());
    }

    @Test(description = "测试开启｜关闭集群监控")
    public void clusterMonitorTest() {
        String url = baseUrl + "/api/v1/op/clusters/{clusterId}/monitor";
        // 监控关闭成功
        clusterMonitor1Test(url);
        // 监控开启成功
        clusterMonitor2Test(url);
        // 无效的集群
        clusterMonitor3Test(url);
    }

    private void clusterMonitor1Test(String url) {
        url = url + "?status=0";
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", getPhysicalClusterId());

        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void clusterMonitor2Test(String url) {
        url = url + "?status=1";
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", getPhysicalClusterId());

        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void clusterMonitor3Test(String url) {
        url = url + "?status=1";
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", ConfigConstant.INVALID_CLUSTER_ID);

        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    @Test(description = "测试增加Controller优先候选的Broker")
    public void addControllerPreferredCandidatesTest() {
        String url = baseUrl + "/api/v1/op/cluster-controller/preferred-candidates";
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        // 增加成功
        addControllerPreferredCandidates1Test(url, physicalClusterId);
        // broker不存在
        addControllerPreferredCandidates2Test(url, physicalClusterId);
        // 参数错误
        addControllerPreferredCandidates3Test(url, physicalClusterId);
    }

    private void addControllerPreferredCandidates1Test(String url, Long physicalClusterId) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(physicalClusterId);
        String aliveBrokerId = configMap.get(ConfigConstant.ALIVE_BROKER_ID);
        dto.setBrokerIdList(Arrays.asList(Integer.parseInt(aliveBrokerId)));

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addControllerPreferredCandidates2Test(String url, Long physicalClusterId) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(physicalClusterId);
        dto.setBrokerIdList(Arrays.asList(ConfigConstant.INVALID_ID));

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

    private void addControllerPreferredCandidates3Test(String url, Long physicalClusterId) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(physicalClusterId);

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }


    @Test(description = "测试删除Controller优先候选的Broker")
    public void deleteControllerPreferredCandidatesTest() {
        String url = baseUrl + "/api/v1/op/cluster-controller/preferred-candidates";
        Long physicalClusterId = Long.parseLong(configMap.get(ConfigConstant.PHYSICAL_CLUSTER_ID));
        // 删除成功
        deleteControllerPreferredCandidates1Test(url, physicalClusterId);
        // 参数错误
        deleteControllerPreferredCandidates2Test(url, physicalClusterId);
    }

    private void deleteControllerPreferredCandidates1Test(String url, Long physicalClusterId) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(physicalClusterId);
        String aliveBrokerId = configMap.get(ConfigConstant.ALIVE_BROKER_ID);
        dto.setBrokerIdList(Arrays.asList(Integer.parseInt(aliveBrokerId)));

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteControllerPreferredCandidates2Test(String url, Long physicalClusterId) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(physicalClusterId);

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    @Test(description = "测试删除物理集群")
    public void deleteClusterTest() {
        String url = baseUrl + "/api/v1/op/clusters";
        // 集群不存在
        deleteCluster2Test(url);
    }

    private void deleteCluster1Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        url = url + "?clusterId=" + clusterDTO.getClusterId();
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteCluster2Test(String url) {
        url = url + "?clusterId=" + ConfigConstant.INVALID_CLUSTER_ID;
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

}
