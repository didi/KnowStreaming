package com.xiaojukeji.know.streaming.km.common.constant;

/**
 * Api前缀
 * @author zengqiao
 * @date 20/4/16
 */
public class ApiPrefix {
    public static final String API_PREFIX = "/ks-km/api/";

    public static final String API_V1_PREFIX = API_PREFIX + "v1/";

    public static final String API_V3_PREFIX = API_PREFIX + "v3/";

    public static final String API_V3_CONNECT_PREFIX = API_V3_PREFIX + "kafka-connect/";

    public static final String API_V3_MM2_PREFIX = API_V3_PREFIX + "kafka-mm2/";

    public static final String API_V3_HA_MIRROR_PREFIX = API_V3_PREFIX + "ha-mirror/";

    public static final String API_V3_OPEN_PREFIX = API_V3_PREFIX + "open/";

    private ApiPrefix() {
    }
}