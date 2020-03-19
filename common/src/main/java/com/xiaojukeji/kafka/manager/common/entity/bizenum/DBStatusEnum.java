package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * DBStatus状态含义
 * @author zengqiao_cn@163.com
 * @date 19/4/15
 */
public enum DBStatusEnum {
    /**
     * 逻辑删除
     */
    DELETED(-1),

    /**
     * 普通
     */
    NORMAL(0),

    /**
     * 已完成并通过
     */
    PASSED(1);

    private Integer status;

    DBStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public static DBStatusEnum getDBStatusEnum(Integer status) {
        for (DBStatusEnum elem: DBStatusEnum.values()) {
            if (elem.getStatus().equals(status)) {
                return elem;
            }
        }
        return null;
    }
}