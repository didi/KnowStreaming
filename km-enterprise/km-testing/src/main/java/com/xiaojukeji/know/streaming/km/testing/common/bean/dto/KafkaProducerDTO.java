package com.xiaojukeji.know.streaming.km.testing.common.bean.dto;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseTesting;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.ClusterTopicDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@EnterpriseTesting
@ApiModel(description="Kafka生产者测试")
public class KafkaProducerDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "消息Key", example = "hello know-streaming key")
    private String recordKey;

    @NotNull(message = "recordValue不允许为null")
    @ApiModelProperty(value = "消息Value", example = "hello know-streaming value")
    private String recordValue;

    @ApiModelProperty(value = "recordHeader, key-value结构", example = "{}")
    private Properties recordHeader;

    @Min(value = 1, message = "recordCount不允许为null或者小于0")
    @ApiModelProperty(value = "发送消息条数", example = "6")
    private Integer recordCount;

    @NotNull(message = "clientProperties不允许为null")
    @ApiModelProperty(value = "客户端配置", example = "{}")
    private Properties clientProperties;

    @ApiModelProperty(value = "分区ID列表，为空时表示不进行控制", example = "[1, 2, 3]")
    private List<Integer> partitionIdList;

    @NotNull(message = "recordOperate不允许为空")
    @ApiModelProperty(value = "记录操作，仅记录发起的第一次", example = "false")
    private Boolean recordOperate;
}
