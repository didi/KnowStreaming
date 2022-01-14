package com.xiaojukeji.kafka.manager.common.utils.jmx;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class JmxConfig {
    /**
     * 单台最大连接数
     */
    private Integer maxConn;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 开启SSL
     */
    private Boolean openSSL;

    /**
     * 连接重试回退事件
     */
    private Long retryConnectBackoffTimeUnitMs;
}
