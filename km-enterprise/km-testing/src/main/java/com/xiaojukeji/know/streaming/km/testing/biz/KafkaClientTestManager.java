package com.xiaojukeji.know.streaming.km.testing.biz;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaConsumerDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.dto.KafkaProducerDTO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestConsumerVO;
import com.xiaojukeji.know.streaming.km.testing.common.bean.vo.TestProducerVO;


import java.util.List;

@EnterpriseTesting
public interface KafkaClientTestManager {
    /**
     * 生产测试
     * @param dto 生产测试参数
     * @param operator 操作人
     * @return
     */
    Result<List<TestProducerVO>> produceTest(KafkaProducerDTO dto, String operator);

    /**
     * 消费测试
     * @param dto 消费测试参数
     * @param operator 操作人
     * @return
     */
    Result<TestConsumerVO> consumeTest(KafkaConsumerDTO dto, String operator);
}
