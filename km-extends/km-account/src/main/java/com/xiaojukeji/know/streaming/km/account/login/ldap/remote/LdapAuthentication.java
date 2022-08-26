package com.xiaojukeji.know.streaming.km.account.login.ldap.remote;

import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.xiaojukeji.know.streaming.km.account.KmAccountConfig;
import com.xiaojukeji.know.streaming.km.account.common.ldap.LdapPrincipal;
import com.xiaojukeji.know.streaming.km.account.common.ldap.exception.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * @author Hu.Yue
 * @date 2021/8/4
 **/
@Component
public class LdapAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapAuthentication.class);

    @Autowired
    private KmAccountConfig kmAccountConfig;

    /**
     * LDAP账密验证
     */
    public LdapPrincipal authenticate(String userName, String password) throws LogiSecurityException, LdapException {
        // 获取ldap-context
        LdapContext ctx = getLdapContext();

        // 获取用户信息
        LdapPrincipal ldapAttrsInfo = getLdapAttrsInfo(userName, ctx);

        // 校验密码
        try {
            // 校验密码
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, ldapAttrsInfo.getUserDN());
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(null);

            // 返回用户信息
            return ldapAttrsInfo;
        } catch (Exception e) {
            LOGGER.error("method=authenticate||userName={}||errMsg={}", userName, e);

            // 密码错误
            throw new LogiSecurityException(ResultCode.USER_CREDENTIALS_ERROR);
        } finally {
            this.closeLdapContext(ctx);
        }
    }

    /**************************************************** private method ****************************************************/

    private LdapContext getLdapContext() throws LdapException {
        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, kmAccountConfig.getLdapFactory());
        env.put(Context.PROVIDER_URL, kmAccountConfig.getLdapUrl() + kmAccountConfig.getLdapBaseDN());
        env.put(Context.SECURITY_AUTHENTICATION, kmAccountConfig.getSecurityAuthentication());

        // 此处若不指定用户名和密码, 则自动转换为匿名登录
        env.put(Context.SECURITY_PRINCIPAL, kmAccountConfig.getSecurityPrincipal());
        env.put(Context.SECURITY_CREDENTIALS, kmAccountConfig.getSecurityCredentials());
        try {
           return new InitialLdapContext(env, null);
        } catch (Exception e) {
            LOGGER.error("method=getLdapContext||errMsg=exception", e);

            throw new LdapException("调用Ldap服务异常", e);
        }
    }

    /**
     * 获取用户信息
     */
    private LdapPrincipal getLdapAttrsInfo(String userName, LdapContext ctx) {
        //存储更多的LDAP元信息
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // 查找
            NamingEnumeration<SearchResult> en = ctx.search(
                    "",
                    "(&(objectClass=*)(" + kmAccountConfig.getLdapFilter() + "=" + userName + "))",
                    constraints
            );
            if (en == null || !en.hasMoreElements()) {
                // 用户不存在
                throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
            }

            // maybe more than one element
            while (en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;

                    // 携带LDAP更多元信息以填充用户元信息
                    LdapPrincipal ldapPrincipal = new LdapPrincipal();
                    ldapPrincipal.setUserDN(si.getName() + "," + kmAccountConfig.getLdapBaseDN());
                    ldapPrincipal.setSAMAccountName(this.keyValueSplit(si.getAttributes().get("samaccountname").toString()));
                    ldapPrincipal.setDepartment(this.keyValueSplit(si.getAttributes().get("department").toString()));
                    ldapPrincipal.setCompany(this.keyValueSplit(si.getAttributes().get("company").toString()));
                    ldapPrincipal.setDisplayName(this.keyValueSplit(si.getAttributes().get("displayname").toString()));
                    ldapPrincipal.setMail(this.keyValueSplit(si.getAttributes().get("mail").toString()));
                    return ldapPrincipal;
                }
            }

            // 用户不存在
            throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
        } catch (Exception e) {
            LOGGER.error("method=getLdapAttrsInfo||userName={}||errMsg=exception", userName, e);

            throw new LdapException("调用Ldap服务异常", e);
        }
    }

    private void closeLdapContext(LdapContext ctx) {
        if (ctx == null) {
            return;
        }

        try {
            ctx.close();
        } catch (Exception e) {
            LOGGER.error("method=closeLdapContext||errMsg=exception", e);
        }
    }

    public String keyValueSplit(String keyValue){
        return keyValue.split(":\\s+")[1];
    }
}
