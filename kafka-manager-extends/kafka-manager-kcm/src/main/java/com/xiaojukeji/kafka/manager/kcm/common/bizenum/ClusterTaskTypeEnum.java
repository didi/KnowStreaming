package com.xiaojukeji.kafka.manager.kcm.common.bizenum;

import com.xiaojukeji.kafka.manager.kcm.common.entry.ClusterTaskConstant;

/**
 * 集群任务类型
 * @author zengqiao
 * @date 20/5/20
 */
public enum ClusterTaskTypeEnum {
    ROLE_UPGRADE(
            ClusterTaskConstant.UPGRADE,
            ClusterTaskConstant.CLUSTER_ROLE_UPGRADE,
            "集群升级(按角色)",
            ClusterTaskConstant.CLUSTER_ROLE_BEAN_NAME
    ),

    HOST_UPGRADE(
            ClusterTaskConstant.UPGRADE,
            ClusterTaskConstant.CLUSTER_HOST_UPGRADE,
            "集群升级(按主机)",
            ClusterTaskConstant.CLUSTER_HOST_BEAN_NAME
    ),

    DEPLOY(
            ClusterTaskConstant.DEPLOY,
            ClusterTaskConstant.CLUSTER_HOST_DEPLOY,
            "集群部署",
            ClusterTaskConstant.CLUSTER_HOST_BEAN_NAME
    ),

    EXPAND(
            ClusterTaskConstant.DEPLOY,
            ClusterTaskConstant.CLUSTER_HOST_EXPAND,
            "集群扩容",
            ClusterTaskConstant.CLUSTER_HOST_BEAN_NAME),

    ROLLBACK(
            ClusterTaskConstant.ROLLBACK,
            ClusterTaskConstant.CLUSTER_ROLLBACK,
            "集群回滚",
            ""),
    ;

    private int way;

    private String name;

    private String message;

    private String beanName;

    ClusterTaskTypeEnum(int way, String name, String message, String beanName) {
        this.way = way;
        this.name = name;
        this.message = message;
        this.beanName = beanName;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public String toString() {
        return "ClusterTaskTypeEnum{" +
                "way=" + way +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", beanName='" + beanName + '\'' +
                '}';
    }

    public static ClusterTaskTypeEnum getByName(String name) {
        for (ClusterTaskTypeEnum elem: ClusterTaskTypeEnum.values()) {
            if (elem.getName().equals(name)) {
                return elem;
            }
        }
        return null;
    }
}
