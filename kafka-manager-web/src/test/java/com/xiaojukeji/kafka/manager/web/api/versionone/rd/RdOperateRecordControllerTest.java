package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.OperateRecordDTO;
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
 * @Date 2022/2/21
 */
public class RdOperateRecordControllerTest extends BaseTest {

    @BeforeClass
    public void init() {
        super.init();
    }

    @Test(description = "测试查询操作记录")
    public void operateRecordTest() {
        OperateRecordDTO operateRecordDTO = new OperateRecordDTO();
        operateRecordDTO.setOperateId(0);
        operateRecordDTO.setModuleId(0);
        operateRecordDTO.setStartTime(0L);
        operateRecordDTO.setEndTime(System.currentTimeMillis());

        String url = baseUrl + "/api/v1/rd/operate-record";
        HttpEntity<OperateRecordDTO> httpEntity = new HttpEntity<>(operateRecordDTO, httpHeaders);
        ResponseEntity<Result> result = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class);
        Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals(result.getBody().getCode(), ResultStatus.SUCCESS.getCode());
    }
}
