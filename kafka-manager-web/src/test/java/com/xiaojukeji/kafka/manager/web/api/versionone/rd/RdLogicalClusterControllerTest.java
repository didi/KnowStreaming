package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.LogicalClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.RegionDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.RegionVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.LogicalClusterVO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import com.xiaojukeji.kafka.manager.web.config.ConfigConstant;
import com.xiaojukeji.kafka.manager.web.config.CustomDataSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * @Date 2022/2/17
 */
public class RdLogicalClusterControllerTest extends BaseTest {

    private Long regionId;

    private Long logicalClusterId;

    @BeforeClass
    public void init() {
        super.init();

        // 创建region
        createRegionSuccess();
        // 获取regionId
        regionId = getRegionId();
        // 创建逻辑集群
        createLogicalClusterSuccess();
        logicalClusterId = getLogicalClusterId();
    }

    @AfterClass
    public void destroy() {
        deleteLogicalClusterSuccess();
        deleteRegionSuccess();
    }

    @Test(description = "测试增加逻辑集群")
    public void createLogicalClusterTest() {
        String url = baseUrl + "/api/v1/rd/logical-clusters";
        // 参数错误
        createLogicalCluster1(url);
    }

    private void createLogicalCluster1(String url) {
        LogicalClusterDTO logicalClusterDTO = CustomDataSource.getLogicalClusterDTO(configMap);
        logicalClusterDTO.setRegionIdList(null);
        HttpEntity<LogicalClusterDTO> httpEntity = new HttpEntity<>(logicalClusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void createLogicalClusterSuccess() {
        String url = baseUrl + "/api/v1/rd/logical-clusters";
        LogicalClusterDTO logicalClusterDTO = CustomDataSource.getLogicalClusterDTO(configMap);
        logicalClusterDTO.setRegionIdList(Arrays.asList(regionId));
        HttpEntity<LogicalClusterDTO> httpEntity = new HttpEntity<>(logicalClusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void createRegionSuccess() {
        String url = baseUrl + "/api/v1/rd/regions";
        RegionDTO regionDTO = CustomDataSource.getRegionDTO(configMap);
        HttpEntity<RegionDTO> httpEntity = new HttpEntity<>(regionDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private Long getRegionId() {
        String url = baseUrl + "/api/v1/rd/{clusterId}/regions";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        String s = JSON.toJSONString(result.getBody().getData());
        List<RegionVO> regions = JSON.parseArray(s, RegionVO.class);
        for (RegionVO region : regions) {
            if (region.getName().equals(configMap.get(ConfigConstant.REGION_NAME))) {
                return region.getId();
            }
        }
        return null;
    }

    private Long getLogicalClusterId() {
        String url = baseUrl + "/api/v1/rd/{physicalClusterId}/logical-clusters";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        String s = JSON.toJSONString(result.getBody().getData());
        List<LogicalClusterVO> logicalClusters = JSON.parseArray(s, LogicalClusterVO.class);
        for (LogicalClusterVO logicalCLuster : logicalClusters) {
            if (logicalCLuster.getLogicalClusterName().equals(configMap.get(ConfigConstant.LOGICAL_CLUSTER_NAME))) {
                return logicalCLuster.getLogicalClusterId();
            }
        }
        return null;
    }

    @Test(description = "测试修改逻辑集群")
    public void modifyLogicalClusterTest() {
        // 参数失败
        modifyLogicalCluster1();
        // 修改成功
        modifyLogicalCluster2();
    }

    private void modifyLogicalCluster1() {
        String url = baseUrl + "/api/v1/rd/logical-clusters";
        LogicalClusterDTO logicalClusterDTO = CustomDataSource.getLogicalClusterDTO(configMap);
        logicalClusterDTO.setRegionIdList(Arrays.asList(regionId));
        logicalClusterDTO.setId(null);
        HttpEntity<LogicalClusterDTO> httpEntity = new HttpEntity<>(logicalClusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void modifyLogicalCluster2() {
        String url = baseUrl + "/api/v1/rd/logical-clusters";
        LogicalClusterDTO logicalClusterDTO = CustomDataSource.getLogicalClusterDTO(configMap);
        logicalClusterDTO.setRegionIdList(Arrays.asList(regionId));
        logicalClusterDTO.setId(getLogicalClusterId());
        HttpEntity<LogicalClusterDTO> httpEntity = new HttpEntity<>(logicalClusterDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试删除逻辑集群")
    public void deleteLogicalClusterTest() {

    }

    private void deleteLogicalClusterSuccess() {
        String url = baseUrl + "/api/v1/rd/logical-clusters?id=" + logicalClusterId;
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    private void deleteRegionSuccess() {
        Long regionId = getRegionId();
        String url = baseUrl + "/api/v1/rd/regions?id=" + regionId;
        HttpEntity<Long> httpEntity =
                new HttpEntity<>(null, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(
                url, HttpMethod.DELETE, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取逻辑集群列表")
    public void getLogicalClustersTest() {
        String url = baseUrl + "/api/v1/rd/{physicalClusterId}/logical-clusters";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("physicalClusterId", physicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试由逻辑集群Id获取逻辑集群")
    public void getLogicalClusterTest() {
        String url = baseUrl + "/api/v1/rd/logical-clusters?id=" + getLogicalClusterId();
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
