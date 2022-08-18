package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkacontroller.KafkaControllerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "集群Broker状态信息")
public class ClusterBrokersStateVO implements Serializable {
    @ApiModelProperty(value = "Broker数", example = "100")
    private Integer brokerCount;

    @ApiModelProperty(value = "Broker版本信息", example = "[\"2.5.1\", \"3.1.0\"]")
    private List<String> brokerVersionList;

    @ApiModelProperty(value = "KafkaController信息")
    private KafkaControllerVO kafkaController;

    @ApiModelProperty(value = "KafkaController正常")
    private Boolean kafkaControllerAlive;

    @ApiModelProperty(value = "配置一致", example = "true")
    private Boolean configSimilar;
}
