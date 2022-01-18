package com.xiaojukeji.kafka.manager.common.constant;

/**
 * Api前缀
 * @author zengqiao
 * @date 20/4/16
 */
public class ApiPrefix {
    public static final String API_PREFIX = "/api/";
    private static final String API_V1_PREFIX = API_PREFIX + "v1/";

    // login
    public static final String API_V1_SSO_PREFIX = API_V1_PREFIX + "sso/";

    // console
    public static final String API_V1_NORMAL_PREFIX = API_V1_PREFIX + "normal/";
    public static final String API_V1_RD_PREFIX = API_V1_PREFIX + "rd/";
    public static final String API_V1_OP_PREFIX = API_V1_PREFIX + "op/";

    // open
    public static final String API_V1_THIRD_PART_PREFIX = API_V1_PREFIX + "third-part/";

    // gateway
    public static final String GATEWAY_API_V1_PREFIX = "/gateway" + API_V1_PREFIX;

    private ApiPrefix() {
    }
}