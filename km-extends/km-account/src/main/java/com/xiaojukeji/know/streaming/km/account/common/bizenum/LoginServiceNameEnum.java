package com.xiaojukeji.know.streaming.km.account.common.bizenum;

import lombok.Getter;

@Getter
public enum LoginServiceNameEnum {
    DEFAULT(LoginServiceNameEnum.DEFAULT_LOGIN_NAME, "默认"),

    LDAP(LoginServiceNameEnum.LDAP_LOGIN_NAME, "Ldap登录"),

    ;

    public static final String DEFAULT_LOGIN_NAME = "loginService";

    public static final String LDAP_LOGIN_NAME = "ldapLoginService";

    private final String name;

    private final String msg;

    LoginServiceNameEnum(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }
}
