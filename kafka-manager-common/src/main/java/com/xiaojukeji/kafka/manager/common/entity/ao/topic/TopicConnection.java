package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/20
 */
@Data
public class TopicConnection {
    private Long clusterId;

    private String topicName;

    private String appId;

    private String ip;

    private String hostname;

    private String clientType;

    private String clientVersion;

    private String clientId;

    private Long realConnectTime;

    private Long createTime;
}