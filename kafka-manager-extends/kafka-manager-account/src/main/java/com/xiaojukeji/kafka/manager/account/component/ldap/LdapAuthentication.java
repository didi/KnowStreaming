package com.xiaojukeji.kafka.manager.account.component.ldap;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

@Component
public class LdapAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapAuthentication.class);

    @Value(value = "${account.ldap.url:}")
    private String ldapUrl;

    @Value(value = "${account.ldap.basedn:}")
    private String ldapBasedn;

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

    private LdapContext getLdapContext() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
        env.put(Context.PROVIDER_URL, ldapUrl + ldapBasedn);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);

        // 此处若不指定用户名和密码,则自动转换为匿名登录
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
        try {
           return new InitialLdapContext(env, null);
        } catch (AuthenticationException e) {
            LOGGER.warn("class=LdapAuthentication||method=getLdapContext||errMsg={}", e);
        } catch (Exception e) {
            LOGGER.error("class=LdapAuthentication||method=getLdapContext||errMsg={}", e);
        }
        return null;
    }

    private String getUserDN(String account, LdapContext ctx) {
        String userDN = "";
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "(&(objectClass=*)("+ldapFilter+"=" + account + "))";

            NamingEnumeration<SearchResult> en = ctx.search("", filter, constraints);
            if (en == null || !en.hasMoreElements()) {
                return "";
            }
            // maybe more than one element
            while (en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;
                    userDN += si.getName();
                    userDN += "," + ldapBasedn;
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("class=LdapAuthentication||method=getUserDN||account={}||errMsg={}", account, e);
        }
        return userDN;
    }

    /**
     * LDAP账密验证
     * @param account
     * @param password
     * @return
     */
    public boolean authenticate(String account, String password) {
        LdapContext ctx = getLdapContext();
        if (ValidateUtils.isNull(ctx)) {
            return false;
        }

        try {
            String userDN = getUserDN(account, ctx);
            if(ValidateUtils.isBlank(userDN)){
                return false;
            }

            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(null);

            return true;
        } catch (AuthenticationException  e) {
            LOGGER.warn("class=LdapAuthentication||method=authenticate||account={}||errMsg={}", account, e);
        } catch (NamingException e) {
            LOGGER.warn("class=LdapAuthentication||method=authenticate||account={}||errMsg={}", account, e);
        } catch (Exception e) {
            LOGGER.error("class=LdapAuthentication||method=authenticate||account={}||errMsg={}", account, e);
        } finally {
            if(ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOGGER.error("class=LdapAuthentication||method=authenticate||account={}||errMsg={}", account, e);
                }
            }
        }
        return false;
    }
}
