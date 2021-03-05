package com.xiaojukeji.kafka.manager.common.utils.ldap;

import org.apache.commons.lang.StringUtils;
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
public class LDAPAuthentication {

    @Value(value = "${ldap.url}")
    private String ldapUrl;

    @Value(value = "${ldap.basedn}")
    private String ldapBasedn;

    @Value(value = "${ldap.factory}")
    private String ldapFactory;

    @Value(value = "${ldap.filter}")
    private String ldapfilter;

    @Value(value = "${ldap.auth-user-registration-role}")
    private String authUserRegistrationRole;

    @Value(value = "${ldap.security.authentication}")
    private String securityAuthentication;

    @Value(value = "${ldap.security.principal}")
    private String securityPrincipal;

    @Value(value = "${ldap.security.credentials}")
    private String securityCredentials;

    private LdapContext getConnect() {
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
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUserDN(String account,LdapContext ctx) {
        String userDN = "";
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "(&(objectClass=*)("+ldapfilter+"=" + account + "))";

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
            e.printStackTrace();
        }

        return userDN;
    }

    /**
     * LDAP账密验证
     * @param account
     * @param password
     * @return
     */
    public boolean authenricate(String account, String password) {
        LdapContext ctx = getConnect();

        boolean valide = false;

        try {
            String userDN = getUserDN(account,ctx);

            if(StringUtils.isEmpty(userDN)){
                return    valide;
            }


            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(null);
            valide = true;
        } catch (AuthenticationException e) {
            System.out.println(e.toString());
        } catch (NamingException e) {
            e.printStackTrace();
        }finally {
            if(ctx!=null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }

        return valide;
    }

}
