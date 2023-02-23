package com.xiaojukeji.know.streaming.km.testing.common.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.ClusterTopicDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@EnterpriseTesting
@ApiModel(description="Kafka消费者测试")
public class KafkaConsumerDTO extends ClusterTopicDTO {
    @Valid
    @NotNull(message = "startFrom不允许为null")
    @ApiModelProperty(value = "消费起始位置信息")
    private KafkaConsumerStartFromDTO startFrom;

    @Min(value = 1000, message = "maxDurationUnitMs不允许为null，且不能小于1000ms")
    @ApiModelProperty(value = "消费结束信息", example = "10000")
    private Long maxDurationUnitMs;

    @Valid
    @NotNull(message = "filter不允许为null")
    @ApiModelProperty(value = "发送消息条数", example = "6")
    private KafkaConsumerFilterDTO filter;

    @NotNull(message = "clientProperties不允许为null")
    @ApiModelProperty(value = "客户端配置", example = "{}")
    private Properties clientProperties;

    @NotNull(message = "recordOperate不允许为空")
    @ApiModelProperty(value = "记录操作，仅记录发起的第一次", example = "false")
    private Boolean recordOperate;

    @Min(value = 1, message = "maxRecords不允许为null，且不能小于1")
    @ApiModelProperty(value = "消费结束信息", example = "100")
    private Integer maxRecords;
}
