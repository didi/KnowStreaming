package com.xiaojukeji.know.streaming.km.common.bean.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author d06679
 * @date 2019/3/22
 */
@Data
public class BaseEntity implements Serializable {
    protected Date createTime;

    protected Date updateTime;
}
