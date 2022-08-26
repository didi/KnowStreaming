package com.xiaojukeji.know.streaming.km.common.bean.po;

public abstract class BaseESPO {
    /**
     * 获取ES文档的主键key
     *
     * @return
     */
    public abstract String getKey();

    /**
     * 获取routing值
     * @return
     */
    public abstract String getRoutingValue();
}
