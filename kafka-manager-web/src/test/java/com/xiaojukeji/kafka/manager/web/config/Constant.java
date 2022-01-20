package com.xiaojukeji.kafka.manager.web.config;

/**
 * @author xuguang
 * @Date 2022/1/11
 */
public class Constant {
    public static final String BASE_URL = "http://localhost:8080";

    public static final String TRICK_LOGIN_SWITCH = "Trick-Login-Switch";

    public static final String TRICK_LOGIN_USER = "Trick-Login-User";

    /**
     * on表示开启trick_login
     */
    public static final String ON = "on";

    /**
     * 数据库中实际存在的user
     */
    public static final String USER_ADMIN = "admin";

    /**
     * 数据库中实际存在的APPID
     */
    public static final String APPID_IN_MYSQL = "dkm_admin";

    /**
     * 无效字符串
     */
    public static final String INVALID = "xxxx";

    public static final String INVALID_APPID = INVALID;

    /**
     * 数据库中实际存在的物理集群Id
     */
    public static final Long PHYSICAL_CLUSTER_ID_IN_MYSQL = 1L;

    /**
     * 数据库中实际存在的逻辑集群Id
     */
    public static final Long LOGICAL_CLUSTER_ID_IN_MYSQL = 7L;

    public static final Integer ALIVE_BROKER_ID = 3;

    /**
     * 无效的集群Id
     */
    public static final Long INVALID_CLUSTER_ID_IN_MYSQL = Long.MAX_VALUE;

    public static final Long INVALID_ID = -1L;

    /**
     * 数据库中实际存在的TopicName
     */
    public static final String TOPIC_NAME_IN_MYSQL = "moduleTest";

    public static final String INVALID_TOPIC_NAME = INVALID;

    /**
     * 操作权限
     */
    public static final Integer ACCESS = 100;

    public static final String ZK_ADDRESS = "10.190.46.198:2181,10.190.14.237:2181,10.190.50.65:2181/xg";

    public static final String BOOTSTRAP_SERVERS = "10.190.46.198:9093,10.190.14.237:9093,10.190.50.65:9093";

    public static final String CLUSTER_NAME = "clusterTest";

    public static final String IDC = "China";
}
