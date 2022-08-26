package com.xiaojukeji.know.streaming.km.account.common.ldap;

import lombok.Data;

@Data
public class LdapPrincipal {
    private String userDN;

    private String sAMAccountName;

    private String department;

    private String company;

    private String displayName;

    private String mail;
}
