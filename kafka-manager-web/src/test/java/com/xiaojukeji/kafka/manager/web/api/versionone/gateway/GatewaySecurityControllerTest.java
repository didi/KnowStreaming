package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.KafkaAclSearchDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.KafkaUserSearchDTO;
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
 * @Date 2022/2/18
 */
public class GatewaySecurityControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    private KafkaAclSearchDTO getKafkaAclSearchDTO() {
        KafkaAclSearchDTO kafkaAclSearchDTO = new KafkaAclSearchDTO();
        kafkaAclSearchDTO.setClusterId(physicalClusterId);
        kafkaAclSearchDTO.setStart(0L);
        long now = System.currentTimeMillis();
        kafkaAclSearchDTO.setEnd(now);
        return kafkaAclSearchDTO;
    }

    private KafkaUserSearchDTO getKafkaUserSearchDTO() {
        KafkaUserSearchDTO kafkaUserSearchDTO = new KafkaUserSearchDTO();
        kafkaUserSearchDTO.setStart(0L);
        long now = System.currentTimeMillis();
        kafkaUserSearchDTO.setEnd(now);
        return kafkaUserSearchDTO;
    }

    @Test(description = "测试查询Kafka用户权限查询")
    public void getSecurityAcls() {
        String url = baseUrl + "/gateway/api/v1/security/acls";
        HttpEntity<KafkaAclSearchDTO> httpEntity = new HttpEntity<>(getKafkaAclSearchDTO(), httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
    }

    @Test(description = "测试查询Kafka用户查询")
    public void getSecurityUsers() {
        String url = baseUrl + "/gateway/api/v1/security/users";
        HttpEntity<KafkaUserSearchDTO> httpEntity = new HttpEntity<>(getKafkaUserSearchDTO(), httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
    }
}
