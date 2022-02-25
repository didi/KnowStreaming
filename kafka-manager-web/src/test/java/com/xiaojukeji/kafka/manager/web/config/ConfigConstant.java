package com.xiaojukeji.kafka.manager.web.config;

/**
 * 集成测试配置变量
 * @author xuguang
 * @Date 2022/1/11
 */
public interface ConfigConstant {
    /**
     * 本地配置属性
     */
    String BASE_URL = "base-url";

    String CLUSTER_NAME = "cluster.name";

    String ZOOKEEPER_ADDRESS = "cluster.zookeeper.address";

    String BOOTSTRAP_ADDRESS = "cluster.bootstrap.address";

    String ALIVE_BROKER_ID = "alive-brokerId";

    String PHYSICAL_CLUSTER_ID = "physicalCluster.id.in.mysql";

    String LOGICAL_CLUSTER_ID = "logicalCluster.id.in.mysql";

    String LOGICAL_CLUSTER_NAME = "logicalCluster.name";

    String TOPIC_NAME = "topic.name";

    String APPID = "appId";

    String REGION_NAME = "region.name";

    String GATEWAY_TYPE = "gateway.config.type";

    String GATEWAY_NAME = "gateway.config.name";

    String GATEWAY_VALUE = "gateway.config.value";

    String GATEWAY_DESCRIPTION = "gateway.config.description";

    String ACCOUNT_USERNAME = "account.username";

    String ACCOUNT_ROLE = "account.role";

    String ACCOUNT_PASSWORD = "account.password";


    /**
     * 登陆参数
     */
    String TRICK_LOGIN_SWITCH = "Trick-Login-Switch";

    String TRICK_LOGIN_USER = "Trick-Login-User";

    String OPEN_TRICK_LOGIN = "on";

    /**
     * 管理员用户
     */
    String ADMIN_USER = "admin";

    /**
     * 无效字符串
     */
    String INVALID_STRING = "%_";

    /**
     * 无效集群id
     */
    Long INVALID_CLUSTER_ID = Long.MAX_VALUE;

    /**
     * 无效id
     */
    Integer INVALID_ID = -1;

    /**
     * 数据中心
     */
    String IDC = "cn";

    String CONFIG_KEY = "key";

    String CONFIG_VALUE = "value";

    String KAFKA_MANAGER = "kafka-manager";

    String DESCRIPTION = "integrationTest";

    /**
     * 操作权限
     */
    Integer ACCESS = 100;
}
