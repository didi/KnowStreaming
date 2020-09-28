package com.xiaojukeji.kafka.manager.common.entity.vo.gateway;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/3
 */
public class KafkaSecurityVO {
    List<Object> rows;

    public List<Object> getRows() {
        return rows;
    }

    public void setRows(List<Object> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "KafkaSecurityVO{" +
                "rows=" + rows +
                '}';
    }
}