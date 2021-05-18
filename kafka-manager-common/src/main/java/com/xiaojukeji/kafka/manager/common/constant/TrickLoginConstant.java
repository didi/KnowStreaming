package com.xiaojukeji.kafka.manager.common.constant;

public class TrickLoginConstant {
    /**
     * HTTP Header key
     */
    public static final String TRICK_LOGIN_SWITCH = "Trick-Login-Switch";

    public static final String TRICK_LOGIN_USER = "Trick-Login-User";

    /**
     * 配置允许 trick 登录用户名单
     */
    public static final String TRICK_LOGIN_LEGAL_USER_CONFIG_KEY = "SECURITY.TRICK_USERS";

    /**
     * 开关状态值
     */
    public static final String TRICK_LOGIN_SWITCH_ON = "on";
    public static final String TRICK_LOGIN_SWITCH_OFF = "off";

    private TrickLoginConstant() {
    }
}
