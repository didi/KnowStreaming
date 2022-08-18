package com.xiaojukeji.know.streaming.km.common.bean.vo.kafkacontroller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zengqiao
 * @date 2022/02/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "KafkaController信息")
public class KafkaControllerVO implements Serializable {
    @ApiModelProperty(value = "节点ID", example = "1")
    private Integer brokerId;

    @ApiModelProperty(value = "节点地址", example = "127.0.0.1")
    private String brokerHost;

    @ApiModelProperty(value = "节点地址", example = "WXH")
    private String brokerRack;

    @ApiModelProperty(value = "变更时间", example = "1234567890123")
    private Long timestamp;
}