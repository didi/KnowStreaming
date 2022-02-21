package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.RegionDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.RegionVO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RdRegionControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();

        // 创建region
        createRegionSuccess();
    }

    @AfterClass
    public void deleteRegion() {
        // 删除region
        deleteRegionSuccess();
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

    @Test(description = "测试创建region")
    public void createRegion() {
        String url = baseUrl + "/api/v1/op/topics";
        // 参数错误
        createRegion1Test(url);
    }

    private void createRegion1Test(String url) {
        RegionDTO regionDTO = CustomDataSource.getRegionDTO(configMap);
        regionDTO.setClusterId(null);
        HttpEntity<RegionDTO> httpEntity = new HttpEntity<>(regionDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
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

    @Test(description = "测试删除region")
    public void deleteRegionTest() {

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

    @Test(description = "测试修改region")
    public void modifyRegionTest() {
        String url = baseUrl + "/api/v1/rd/regions";
        // 参数错误
        modifyRegion1(url);
        // 修改成功
        modifyRegion2(url);
    }

    private void modifyRegion1(String url) {
        RegionDTO regionDTO = CustomDataSource.getRegionDTO(configMap);
        regionDTO.setId(null);
        HttpEntity<RegionDTO> httpEntity = new HttpEntity<>(regionDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.PARAM_ILLEGAL.getCode());
    }

    private void modifyRegion2(String url) {
        RegionDTO regionDTO = CustomDataSource.getRegionDTO(configMap);
        regionDTO.setId(getRegionId());
        HttpEntity<RegionDTO> httpEntity = new HttpEntity<>(regionDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "获取region列表")
    public void getRegionListTest() {

        String url = baseUrl + "/api/v1/rd/{clusterId}/regions";
        Map<String, Object> urlVariables = new HashMap<>();
        urlVariables.put("clusterId", physicalClusterId);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class, urlVariables);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
