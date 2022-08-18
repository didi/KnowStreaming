package com.xiaojukeji.know.streaming.km.common.bean.vo.self;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SelfMetricsVO extends BaseVO {
    @ApiModelProperty(value = "KS-KM的IP", example = "127.0.0.1")
    private String ksIp;

    @ApiModelProperty(value = "KS-KM的唯一Key", example = "know-streaming")
    private String ksClusterKey;

    @ApiModelProperty(value = "KS用户数", example = "100")
    private Integer ksUserCount;

    @ApiModelProperty(value = "KS服务端地址", example = "[123, 456, 789]")
    private List<String> ksServerIps;

    @ApiModelProperty(value = "纳管Kafka集群数", example = "10")
    private Integer kafkaClusterCount;

    @ApiModelProperty(value = "纳管Kafka节点数", example = "100")
    private Integer kafkaBrokerCount;
}

