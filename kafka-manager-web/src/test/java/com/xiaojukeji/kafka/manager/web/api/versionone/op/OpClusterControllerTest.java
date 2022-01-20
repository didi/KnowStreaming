package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.ControllerPreferredCandidateDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.ClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterDetailVO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.Constant;
import com.xiaojukeji.kafka.manager.web.config.HttpUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @BeforeMethod
    public void addNewCluster() {
        String url = Constant.BASE_URL + "/api/v1/op/clusters";
        // 接入成功
        addnewCluster1Test(url);
    }

    @AfterMethod
    public void deleteCluster() {
        String url = Constant.BASE_URL + "/api/v1/op/clusters";
        // 删除集群成功
        deleteCluster1Test(url);
    }

    private Long getPhysicalClusterId() {
        String url = Constant.BASE_URL + "/api/v1/rd/clusters/basic-info?need-detail=true";

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        String s = JSON.toJSONString(result.getBody().getData());
        List<ClusterDetailVO> clusterDetailVOS = JSON.parseArray(s, ClusterDetailVO.class);
        for (ClusterDetailVO clusterDetailVO : clusterDetailVOS) {
            if (clusterDetailVO.getClusterName().equals(Constant.CLUSTER_NAME)) {
                return clusterDetailVO.getClusterId();
            }
        }
        return null;
    }

    private ClusterDTO getClusterDTO() {
        ClusterDTO clusterDTO = new ClusterDTO();
        Long physicalClusterId = getPhysicalClusterId();
        clusterDTO.setClusterId(physicalClusterId);
        clusterDTO.setClusterName(Constant.CLUSTER_NAME);
        clusterDTO.setZookeeper(Constant.ZK_ADDRESS);
        clusterDTO.setBootstrapServers(Constant.BOOTSTRAP_SERVERS);
        clusterDTO.setIdc(Constant.IDC);
        return clusterDTO;
    }

    @Test(description = "测试接入集群")
    public void addNewClusterTest() {
        String url = Constant.BASE_URL + "/api/v1/op/clusters";

        // 参数无效
        addnewCluster2Test(url);
        // 无效的zk地址
        addnewCluster3Test(url);
        // 重复创建
        addnewCluster4Test(url);
    }

    private void addnewCluster1Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addnewCluster2Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setZookeeper(null);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void addnewCluster3Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        clusterDTO.setZookeeper(Constant.INVALID);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClusterDTO> httpEntity =
                new HttpEntity<>(clusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.ZOOKEEPER_CONNECT_FAILED.getCode());
    }

    private void addnewCluster4Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        String url = Constant.BASE_URL + "/api/v1/op/clusters";
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
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        clusterDTO.setClusterId(Constant.INVALID_CLUSTER_ID_IN_MYSQL);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        clusterDTO.setZookeeper(Constant.INVALID);
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        String url = Constant.BASE_URL + "/api/v1/op/clusters/{clusterId}/monitor";
        // 监控关闭成功
        clusterMonitor1Test(url);
        // 监控开启成功
        clusterMonitor2Test(url);
        // 无效的集群
        clusterMonitor3Test(url);
    }

    private void clusterMonitor1Test(String url) {
        url = url + "?status=0";
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", Constant.INVALID_ID);

        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.PUT, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.CLUSTER_NOT_EXIST.getCode());
    }

    @Test(description = "测试增加Controller优先候选的Broker")
    public void addControllerPreferredCandidatesTest() {
        String url = Constant.BASE_URL + "/api/v1/op/cluster-controller/preferred-candidates";
        // 增加成功
        addControllerPreferredCandidates1Test(url);
        // broker不存在
        addControllerPreferredCandidates2Test(url);
        // 参数错误
        addControllerPreferredCandidates3Test(url);
    }

    private void addControllerPreferredCandidates1Test(String url) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(getPhysicalClusterId());
        dto.setBrokerIdList(Arrays.asList(Constant.ALIVE_BROKER_ID));

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void addControllerPreferredCandidates2Test(String url) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(getPhysicalClusterId());
        dto.setBrokerIdList(Arrays.asList(-1));

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.BROKER_NOT_EXIST.getCode());
    }

    private void addControllerPreferredCandidates3Test(String url) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(getPhysicalClusterId());

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        String url = Constant.BASE_URL + "/api/v1/op/cluster-controller/preferred-candidates";
        // 删除成功
        deleteControllerPreferredCandidates1Test(url);
        // 参数错误
        deleteControllerPreferredCandidates2Test(url);
    }

    private void deleteControllerPreferredCandidates1Test(String url) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(getPhysicalClusterId());
        dto.setBrokerIdList(Arrays.asList(Constant.ALIVE_BROKER_ID));

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ControllerPreferredCandidateDTO> httpEntity =
                new HttpEntity<>(dto, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteControllerPreferredCandidates2Test(String url) {
        ControllerPreferredCandidateDTO dto = new ControllerPreferredCandidateDTO();
        dto.setClusterId(getPhysicalClusterId());

        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
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
        String url = Constant.BASE_URL + "/api/v1/op/clusters";
        // 集群不存在
        deleteCluster2Test(url);
    }

    private void deleteCluster1Test(String url) {
        ClusterDTO clusterDTO = getClusterDTO();
        url = url + "?clusterId=" + clusterDTO.getClusterId();
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteCluster2Test(String url) {
        url = url + "?clusterId=" + Constant.INVALID_CLUSTER_ID_IN_MYSQL;
        HttpHeaders httpHeaders = HttpUtils.getHttpHeaders();
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.MYSQL_ERROR.getCode());
    }

}
