package com.xiaojukeji.know.streaming.km.license.bean;

import lombok.Data;

/**
 * @author didi
 */
@Data
public class LicenseInfo<T> {
    /**
     *
     */
    private int status;

    /**
     * license 过期时间，单位秒
     */
    private Long expiredDate;

    /**
     *
     */
    private String app;

    /**
     *
     */
    private String type;

    /**
     *
     */
    private T info;
}
