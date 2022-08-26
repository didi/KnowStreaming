package com.xiaojukeji.know.streaming.km.core.service.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.util.KafkaValidate;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 22/02/28
 */
public interface ClusterValidateService {
    Result<KafkaValidate> checkKafkaLegal(String bootstrapServers, Properties clientProps, String zookeeper);
}
