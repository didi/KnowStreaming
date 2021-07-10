package com.xiaojukeji.kafka.manager.common.constant;

/**
 * @author zengqiao
 * @date 20/2/28
 */
public class Constant {
    public static final Integer SUCCESS = 0;

    public static final Integer MAX_AVG_BYTES_DURATION = 10;

    public static final Integer BATCH_INSERT_SIZE = 50;

    public static final Integer DEFAULT_SESSION_TIMEOUT_UNIT_MS = 30000;

    public static final Integer MAX_TOPIC_OPERATION_SIZE_PER_REQUEST = 10;

    /**
     * 不进行过滤的BrokerId
     */
    public static final Integer NOT_FILTER_BROKER_ID = -1;

    /**
     * 默认最近20分钟的连接信息
     */
    public static final Long TOPIC_CONNECTION_LATEST_TIME_MS = 20 * 60 * 1000L;

    /**
     * 工单相关
     */
    public static final String HANDLE_APP_APPLY_MAX_NUM = "handle_app_apply_order_num";

    public static final Integer HANDLE_APP_APPLY_MAX_NUM_DEFAULT = 10;

    public static final String AUTO_HANDLE_USER_NAME = "auto_handle";

    public static final String AUTO_HANDLE_CHINESE_NAME = "自动审批";

    public static final String UNKNOWN_VERSION = "unknownVersion";

    public static final String UNKNOWN_USER = "UNKNOWN_USER";

    public static final String DEFAULT_USER_NAME = "kafka-admin";

    public static final Integer DEFAULT_MAX_CAL_TOPIC_EXPIRED_DAY = 90;

    public static final Integer INVALID_CODE = -1;

    private Constant() {
    }
}
