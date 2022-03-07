package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.RebalanceDTO;
import com.xiaojukeji.kafka.manager.web.config.BaseTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author xuguang
 * @Date 2022/2/23
 */
public class OpLeaderControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    private RebalanceDTO getRebalanceDTO() {
        RebalanceDTO rebalanceDTO = new RebalanceDTO();
        rebalanceDTO.setClusterId(physicalClusterId);
        rebalanceDTO.setDimension(0);
        return rebalanceDTO;
    }

    @Test(description = "测试优先副本选举")
    public void preferredReplicaElectionTest() {
//        String url = baseUrl + "/api/v1/op/leaders/preferred-replica-election";

        String url = baseUrl + "/api/v1/op/utils/rebalance";
        RebalanceDTO rebalanceDTO = getRebalanceDTO();
        HttpEntity<RebalanceDTO> httpEntity = new HttpEntity<>(rebalanceDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }

    @Test(description = "测试获取优先副本选举状态")
    public void getRebalanceStatus() {
        String url = baseUrl + "/api/v1/op/leaders/preferred-replica-election-status?clusterId=" + physicalClusterId;
//        String url = baseUrl + "/api/v1/op/utils/rebalance-status?clusterId=" + physicalClusterId;

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.GET, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }



}
