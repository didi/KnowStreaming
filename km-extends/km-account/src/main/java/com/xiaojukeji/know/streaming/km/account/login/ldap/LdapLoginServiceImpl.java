package com.xiaojukeji.know.streaming.km.account.login.ldap;

import com.didiglobal.logi.security.common.Result;
import com.didiglobal.logi.security.common.dto.account.AccountLoginDTO;
import com.didiglobal.logi.security.common.dto.user.UserDTO;
import com.didiglobal.logi.security.common.entity.user.User;
import com.didiglobal.logi.security.common.enums.ResultCode;
import com.didiglobal.logi.security.common.vo.user.UserBriefVO;
import com.didiglobal.logi.security.exception.LogiSecurityException;
import com.didiglobal.logi.security.extend.LoginExtend;
import com.didiglobal.logi.security.service.UserService;
import com.didiglobal.logi.security.util.AESUtils;
import com.didiglobal.logi.security.util.CopyBeanUtil;
import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.account.KmAccountConfig;
import com.xiaojukeji.know.streaming.km.account.common.bizenum.LoginServiceNameEnum;
import com.xiaojukeji.know.streaming.km.account.common.ldap.LdapPrincipal;
import com.xiaojukeji.know.streaming.km.account.login.ldap.remote.LdapAuthentication;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.didiglobal.logi.security.util.HttpRequestUtil.*;
import static com.didiglobal.logi.security.util.HttpRequestUtil.COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC;

/**
 * @author Hu.Yue
 * @date 2021/8/4
 */
@Service(LoginServiceNameEnum.LDAP_LOGIN_NAME)
public class LdapLoginServiceImpl implements LoginExtend {
    private static final Logger LOGGER  = LoggerFactory.getLogger(LdapLoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private KmAccountConfig kmAccountConfig;

    @Autowired
    private LdapAuthentication ldapAuthentication;

    @Override
    public UserBriefVO verifyLogin(AccountLoginDTO loginDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws LogiSecurityException {
        String decodePasswd = AESUtils.decrypt(loginDTO.getPw());

        // 去LDAP验证账密
        LdapPrincipal ldapAttrsInfo = ldapAuthentication.authenticate(loginDTO.getUserName(), decodePasswd);
        if(ldapAttrsInfo == null) {
            // 用户不存在，正常来说上如果有问题，上一步会直接抛出异常
            throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
        }

        // 如果用户不存在，则进行插入
        User user = userService.getUserByUserName(ldapAttrsInfo.getSAMAccountName());
        if(ValidateUtils.isNull(user) && kmAccountConfig.getAuthUserRegistration() != null && kmAccountConfig.getAuthUserRegistration()) {
            // 自动注册用户
            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(ldapAttrsInfo.getSAMAccountName());
            userDTO.setPw(decodePasswd);
            userDTO.setRealName(ldapAttrsInfo.getDisplayName());
            userDTO.setPhone("");
            userDTO.setEmail(ldapAttrsInfo.getMail());
            userDTO.setRoleIds(CommonUtils.string2IntList(kmAccountConfig.getAuthUserRegistrationRole()));
            userService.addUser(userDTO, ldapAttrsInfo.getSAMAccountName());

            // user赋值
            user = userService.getUserByUserName(ldapAttrsInfo.getSAMAccountName());
        } else if (ValidateUtils.isNull(user)) {
            // user为空，且不自动注册用户时，赋值默认id给临时用户
            user = new User();
            user.setId(Constant.INVALID_CODE);
        }

        // 记录登录状态
        initLoginContext(request, response, loginDTO.getUserName(), user.getId());
        return CopyBeanUtil.copy(user, UserBriefVO.class);
    }

    @Override
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response){
        // 清理session
        request.getSession().invalidate();
        response.setStatus(REDIRECT_CODE);

        // 清理cookies
        for (Cookie cookie: request.getCookies()) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return Result.buildSucc(Boolean.TRUE);
    }

    @Override
    public boolean interceptorCheck(HttpServletRequest request, HttpServletResponse response, String requestMappingValue, List<String> whiteMappingValues) throws IOException {
        if (StringUtils.isEmpty(requestMappingValue)) {
            LOGGER.error("method=interceptorCheck||msg=uri illegal||uri={}", request.getRequestURI());
            return Boolean.FALSE;
        }

        // 白名单接口
        for(String mapping : whiteMappingValues){
            if (requestMappingValue.contains(mapping)){
                return Boolean.TRUE;
            }
        }

        // 检查是否已经登录
        String userName = HttpRequestUtil.getOperator(request);
        if(StringUtils.isEmpty(userName)) {
            // 未登录
            logout(request, response);
            return Boolean.FALSE;
        }

        // 检查用户是否存在
        User user = userService.getUserByUserName(userName);
        if(user == null) {
            throw new LogiSecurityException(ResultCode.USER_NOT_EXISTS);
        }

        initLoginContext(request, response, userName, user.getId());

        return Boolean.TRUE;
    }

    private void initLoginContext(HttpServletRequest request, HttpServletResponse response, String userName, Integer userId) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval( COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC );
        session.setAttribute(USER, userName);
        session.setAttribute(USER_ID, userId);

        Cookie cookieUserName = new Cookie(USER, userName);
        cookieUserName.setMaxAge(COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC);
        cookieUserName.setPath("/");

        Cookie cookieUserId = new Cookie(USER_ID, userId.toString());
        cookieUserId.setMaxAge(COOKIE_OR_SESSION_MAX_AGE_UNIT_SEC);
        cookieUserId.setPath("/");

        response.addCookie(cookieUserName);
        response.addCookie(cookieUserId);
    }
}
