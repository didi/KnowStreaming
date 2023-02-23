package com.xiaojukeji.know.streaming.km.license.bean;

import lombok.Data;

/**
 * @author didi
 */
@Data
public class LicenseUsage {
    /**
     * 上报时间戳
     */
    private Long timeStamp;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 业务数据
     */
    private String data;
}
