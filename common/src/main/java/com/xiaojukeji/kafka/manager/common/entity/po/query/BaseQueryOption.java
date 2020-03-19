package com.xiaojukeji.kafka.manager.common.entity.po.query;

/**
 * @author zengqiao
 * @date 19/12/2
 */
public class BaseQueryOption {
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BaseQueryOption{" +
                "id=" + id +
                '}';
    }
}