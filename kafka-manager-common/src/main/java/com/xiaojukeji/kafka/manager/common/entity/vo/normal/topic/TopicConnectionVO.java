package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhongyuankai,zengqiao
 * @date 20/4/8
 */
@Data
@ApiModel(value = "Topic连接信息")
public class TopicConnectionVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "topic名称")
    private String topicName;

    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "主机名")
    private String hostname;

    @ApiModelProperty(value = "客户端类型[consume|produce]")
    private String clientType;

    @ApiModelProperty(value = "客户端版本")
    private String clientVersion;

    @ApiModelProperty(value = "客户端ID")
    private String clientId;

    @ApiModelProperty(value = "连接Broker时间")
    private Long realConnectTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;
}
