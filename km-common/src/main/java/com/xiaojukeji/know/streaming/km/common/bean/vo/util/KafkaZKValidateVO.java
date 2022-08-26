package com.xiaojukeji.know.streaming.km.common.bean.vo.util;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "Kafka-ZK检查结果")
public class KafkaZKValidateVO {
    @ApiModelProperty(value = "jmx端口", example = "8099")
    private Integer jmxPort;

    @ApiModelProperty(value = "集群版本", example = "2.5.1")
    private String kafkaVersion;

    @ApiModelProperty(value = "错误信息")
    private List<Result<Void>> errList;
}
