package com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KafkaUserVO extends BaseTimeVO {
    @ApiModelProperty(value = "集群Id", example = "3")
    protected Long clusterId;

    @ApiModelProperty(value = "KafkaUser名称", example = "know")
    protected String name;

    @ApiModelProperty(value = "认证方式", example = "1300")
    protected Integer authType;

    @ApiModelProperty(value = "认证方式", example = "scram")
    protected String authName;
}
