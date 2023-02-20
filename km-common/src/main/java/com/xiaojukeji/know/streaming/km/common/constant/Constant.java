package com.xiaojukeji.know.streaming.km.common.constant;

/**
 * @author zengqiao
 * @date 20/2/28
 */
public class Constant {

    private Constant() {}

    public static final String  ONE             = "1";
    public static final String  ZERO            = "0";

    public static final Integer  NUM_ONE             = 1;
    public static final Integer  NUM_ZERO            = 0;

    public static final Integer SUCCESS         = 0;

    public static final String  SYSTEM          = "系统";

    public static final String  COMMA           = ",";

    public static final Integer YES             = 1;
    public static final Integer NO              = 0;

    public static final Integer ALIVE                                   = 1;
    public static final Integer DOWN                                    = 0;

    public static final Integer ONE_HUNDRED    = 100;

    public static final Long B_TO_GB    = 1024L * 1024L * 1024L;

    public static final Long B_TO_MB    = 1024L * 1024L;

    public static final Integer DEFAULT_SESSION_TIMEOUT_UNIT_MS = 15000;
    public static final Integer DEFAULT_REQUEST_TIMEOUT_UNIT_MS = 5000;

    /**
     * 指标相关
     */
    public static final Integer PER_BATCH_MAX_VALUE = 100;

    public static final String DEFAULT_USER_NAME = "know-streaming-app";

    public static final int INVALID_CODE = -1;

    public static final String MYSQL_TABLE_NAME_PREFIX = "ks_km_";
    public static final String MYSQL_KC_TABLE_NAME_PREFIX = "ks_kc_";
    public static final String MYSQL_HA_TABLE_NAME_PREFIX = "ks_ha_";

    public static final String SWAGGER_API_TAG_PREFIX = "KS-KM-";

    public static final String HC_CONFIG_NAME_PREFIX = "HC_";

    /**
     * kafka-user前缀
     */
    public static final String KAFKA_PRINCIPAL_PREFIX = "User:";

    /**
     * 采集指标的花费时间
     */
    public static final String COLLECT_METRICS_COST_TIME_METRICS_NAME = "CollectMetricsCostTimeUnitSec";
    public static final Float COLLECT_METRICS_ERROR_COST_TIME = 100.001F;

    public static final Integer DEFAULT_RETRY_TIME = 3;

    public static final Integer ZK_ALIVE_BUT_4_LETTER_FORBIDDEN         = 11;

    public static final String CONNECTOR_CONFIG_ACTION_RELOAD_NAME      = "config.action.reload";

    public static final String CONNECTOR_CONFIG_ERRORS_TOLERANCE_NAME   = "errors.tolerance";

}
