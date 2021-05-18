package com.xiaojukeji.kafka.manager.common.constant;

/**
 * 登录常量
 * @author zengqiao
 * @date 20/5/8
 */
public class LoginConstant {
    public static final String SESSION_USERNAME_KEY = "username";

    public static final String COOKIE_CHINESE_USERNAME_KEY = "chineseName";

    public static final Integer COOKIE_OR_SESSION_MAX_AGE_UNIT_MS = 24 * 60 * 60 * 1000;

    private LoginConstant() {
    }
}