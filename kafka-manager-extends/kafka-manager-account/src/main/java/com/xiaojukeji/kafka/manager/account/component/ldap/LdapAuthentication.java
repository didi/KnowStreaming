package com.xiaojukeji.kafka.manager.account.component.ldap;

import com.xiaojukeji.kafka.manager.common.utils.SplitUtils;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

    private Map<String, Object> getLdapAttrsInfo(String account, LdapContext ctx) {
        //存储更多的LDAP元信息
        Map<String, Object> ldapAttrsInfo = new HashMap<>();
        String userDN = "";
        ldapAttrsInfo.clear();
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "(&(objectClass=*)("+ldapFilter+"=" + account + "))";

            NamingEnumeration<SearchResult> en = ctx.search("", filter, constraints);
            if (en == null || !en.hasMoreElements()) {
                return null;
            }
            // maybe more than one element
            while (en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;
                    userDN += si.getName();
                    userDN += "," + ldapBasedn;
                    //携带LDAP更多元信息以填充用户元信息
                    ldapAttrsInfo.put("userDN", userDN);
                    ldapAttrsInfo.put("sAMAccountName",
                            SplitUtils.keyValueSplit(si.getAttributes().get("samaccountname").toString()));
                    ldapAttrsInfo.put("department",
                            SplitUtils.keyValueSplit(si.getAttributes().get("department").toString()));
                    ldapAttrsInfo.put("company",
                            SplitUtils.keyValueSplit(si.getAttributes().get("company").toString()));
                    ldapAttrsInfo.put("displayName",
                            SplitUtils.keyValueSplit(si.getAttributes().get("displayname").toString()));
                    ldapAttrsInfo.put("mail",
                            SplitUtils.keyValueSplit(si.getAttributes().get("mail").toString()));
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("class=LdapAuthentication||method=getUserDN||account={}||errMsg={}", account, e);
        }
        return ldapAttrsInfo;
    }

    /**
     * LDAP账密验证
     * @param account
     * @param password
     * @return
     */
    public Map<String, Object> authenticate(String account, String password) {
        LdapContext ctx = getLdapContext();
        if (ValidateUtils.isNull(ctx)) {
            return null;
        }

        try {
            Map<String, Object> ldapAttrsInfo = getLdapAttrsInfo(account, ctx);
            if(ValidateUtils.isNull(ldapAttrsInfo)){
                return null;
            }

            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, ldapAttrsInfo.get("userDN").toString());
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(null);

            return ldapAttrsInfo;
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
        return null;
    }
}
