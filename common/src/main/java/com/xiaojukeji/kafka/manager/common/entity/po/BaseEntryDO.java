package com.xiaojukeji.kafka.manager.common.entity.po;

import java.util.Date;

/**
 * @author zengqiao
 * @date 19/11/25
 */
public abstract class BaseEntryDO {
    protected Long id;

    protected Date gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "BaseEntryDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}