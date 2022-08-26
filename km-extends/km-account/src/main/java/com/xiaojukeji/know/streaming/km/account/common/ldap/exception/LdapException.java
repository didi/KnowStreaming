package com.xiaojukeji.know.streaming.km.account.common.ldap.exception;

import com.didiglobal.logi.security.exception.CodeMsg;

public class LdapException extends RuntimeException {

    public LdapException() {}

    public LdapException(CodeMsg codeMsg) {
        super(codeMsg.getCode() + "-" + codeMsg.getMessage());
    }

    public LdapException(String message, Throwable cause) {
        super(message, cause);
    }
}
