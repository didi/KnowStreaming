package com.xiaojukeji.know.streaming.km.common.constant;

/**
 * 登录常量
 * @author zengqiao
 * @date 20/5/8
 */
public class LoginConstant {
    public static final String SESSION_USERNAME_KEY = "username";

    public static final String COOKIE_CHINESE_USERNAME_KEY = "chineseName";

    public static final Integer COOKIE_OR_SESSION_MAX_AGE_UNIT_MS = 24 * 60 * 60 * 1000;

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

    private LoginConstant() {
    }
}