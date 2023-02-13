package com.xiaojukeji.kafka.manager.common.entity.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zengqiao
 * @date 21/07/19
 */
@Data
public class BaseDO implements Serializable {
    private static final long serialVersionUID = 8782560709154468485L;

    /**
     * 主键ID
     */
    protected Long id;

    /**
     * 创建时间
     */
    protected Date createTime;

    /**
     * 更新时间
     */
    protected Date modifyTime;
}
