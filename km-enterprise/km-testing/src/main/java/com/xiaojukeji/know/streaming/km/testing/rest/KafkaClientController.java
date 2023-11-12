package com.xiaojukeji.know.streaming.km.testing.rest;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.testing.biz.KafkaClientTestManager;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaConsumerDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaProducerDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestConsumerVO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestProducerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@EnterpriseTesting
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "KafkaClient-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaClientController {
    @Autowired
    private KafkaClientTestManager kafkaClientTestManager;

    @ApiOperation(value = "生产者测试")
    @PostMapping(value = "clients/producer")
    @ResponseBody
    public Result<List<TestProducerVO>> produceTest(@Validated @RequestBody KafkaProducerDTO dto) {
        return kafkaClientTestManager.produceTest(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "消费者测试")
    @PostMapping(value = "clients/consumer")
    @ResponseBody
    public Result<TestConsumerVO> consumeTest(@Validated @RequestBody KafkaConsumerDTO dto) {
        return kafkaClientTestManager.consumeTest(dto, HttpRequestUtil.getOperator());
    }
}
