package com.xiaojukeji.know.streaming.km.common.bean.vo.ha.mirror;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@ApiModel(description="Topic复制信息")
public class TopicMirrorInfoVO {
    @ApiModelProperty(value="源集群ID", example = "1")
    private Long sourceClusterId;

    @ApiModelProperty(value="源集群名称", example = "know-streaming-1")
    private String sourceClusterName;

    @ApiModelProperty(value="目标集群ID", example = "2")
    private Long destClusterId;

    @ApiModelProperty(value="目标集群名称", example = "know-streaming-2")
    private String destClusterName;

    @ApiModelProperty(value="Topic名称", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value="写入速率(bytes/s)", example = "100")
    private Double bytesIn;

    @ApiModelProperty(value="复制速率(bytes/s)", example = "100")
    private Double replicationBytesIn;

    @ApiModelProperty(value="延迟消息数", example = "100")
    private Long lag;
}