package com.xiaojukeji.know.streaming.km.account.common.bizenum;

import com.didiglobal.logi.security.extend.LoginExtendBeanTool;
import lombok.Getter;

@Getter
public enum LoginServiceNameEnum {
    DEFAULT(LoginServiceNameEnum.DEFAULT_LOGIN_NAME, "默认"),

    LDAP(LoginServiceNameEnum.LDAP_LOGIN_NAME, "Ldap登录"),

    ;

    /**
     * @see LoginExtendBeanTool.DEFAULT_BEAN_NAME
     */
    public static final String DEFAULT_LOGIN_NAME = "logiSecurityDefaultLoginExtendImpl";

    public static final String LDAP_LOGIN_NAME = "ksLdapLoginService";

    private final String name;

    private final String msg;

    LoginServiceNameEnum(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }
}
