package com.xiaojukeji.know.streaming.km.account;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Data
@Service
public class KmAccountConfig {
    /**************************************************** Ldap 登录相关配置 ****************************************************/

    @Value(value = "${account.ldap.url:}")
    private String ldapUrl;

    @Value(value = "${account.ldap.basedn:}")
    private String ldapBaseDN;

    @Value(value = "${account.ldap.factory:}")
    private String ldapFactory;

    @Value(value = "${account.ldap.filter:}")
    private String ldapFilter;

    @Value(value = "${account.ldap.security.authentication:}")
    private String securityAuthentication;

    @Value(value = "${account.ldap.security.principal:}")
    private String securityPrincipal;

    @Value(value = "${account.ldap.security.credentials:}")
    private String securityCredentials;



    /**************************************************** Ldap 用户注册到KM相关 ****************************************************/

    @Value(value = "${account.ldap.auth-user-registration-role:1677}")
    private String authUserRegistrationRole; // ldap自动注册的默认角色。请注意：它通常来说都是低权限角色

    @Value(value = "${account.ldap.auth-user-registration:false}")
    private Boolean authUserRegistration; // ldap自动注册是否开启

    private KmAccountConfig() {
    }
}

