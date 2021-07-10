package com.xiaojukeji.kafka.manager.kcm.common.entry;

/**
 * 升级部署常量
 * @author zengqiao
 * @date 20/5/20
 */
public class ClusterTaskConstant {
    public static final Long INVALID_AGENT_TASK_ID = -1L;

    public static final String CLUSTER_ROLE_UPGRADE = "role_upgrade";
    public static final String CLUSTER_HOST_UPGRADE = "host_upgrade";
    public static final String CLUSTER_HOST_DEPLOY = "host_deploy";
    public static final String CLUSTER_HOST_EXPAND = "host_expand";
    public static final String CLUSTER_ROLLBACK = "rollback";

    public static final String CLUSTER_ROLE_BEAN_NAME  = "clusterRoleTaskService";
    public static final String CLUSTER_HOST_BEAN_NAME  = "clusterHostTaskService";

    public static final Integer UPGRADE = 0;
    public static final Integer DEPLOY = 1;
    public static final Integer ROLLBACK = 2;
}