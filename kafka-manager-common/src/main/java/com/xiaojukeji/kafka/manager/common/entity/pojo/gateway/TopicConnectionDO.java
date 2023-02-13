package com.xiaojukeji.kafka.manager.common.entity.pojo.gateway;

import lombok.Data;

import java.util.Date;

/**
 * Topic连接信息
 * @author zengqiao
 * @date 20/7/6
 */
@Data
public class TopicConnectionDO {
    private Long id;

    private String appId;

    private Long clusterId;

    private String topicName;

    private String type;

    private String ip;

    private String clientVersion;

    private String clientId;

    private Long realConnectTime;

    private Date createTime;

    public String uniqueKey() {
        return appId + clusterId + topicName + type + ip + clientId;
    }
}