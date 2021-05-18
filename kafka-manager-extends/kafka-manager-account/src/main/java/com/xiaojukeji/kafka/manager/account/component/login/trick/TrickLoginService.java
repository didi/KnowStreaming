package com.xiaojukeji.kafka.manager.account.component.login.trick;

import com.xiaojukeji.kafka.manager.common.constant.TrickLoginConstant;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


/**
 * @author zengqiao
 * @date 21/5/18
 */
@Service
public class TrickLoginService {
    private final static Logger LOGGER = LoggerFactory.getLogger(TrickLoginService.class);

    @Autowired
    private ConfigService configService;

    /**
     * 是否开启trick的方式登录
     */
    public boolean isTrickLoginOn(HttpServletRequest request) {
        return TrickLoginConstant.TRICK_LOGIN_SWITCH_ON.equals(request.getHeader(TrickLoginConstant.TRICK_LOGIN_SWITCH));
    }

    /**
     * 开启trick方式登录后，当前用户是否可以登录
     */
    public String checkTrickLogin(HttpServletRequest request) {
        String trickLoginUser = request.getHeader(TrickLoginConstant.TRICK_LOGIN_USER);
        LOGGER.info("class=TrickLoginService||method=checkTrickLogin||user={}||uri={}||msg=try trick login", trickLoginUser, request.getRequestURI());
        if (!checkTrickLogin(trickLoginUser)) {
            LOGGER.warn("class=TrickLoginService||method=checkTrickLogin||user={}||uri={}||msg=trick login failed", trickLoginUser, request.getRequestURI());
            return null;
        }
        return trickLoginUser;
    }

    private boolean checkTrickLogin(String trickLoginUser) {
        return Optional.ofNullable(configService.getArrayByKey(TrickLoginConstant.TRICK_LOGIN_LEGAL_USER_CONFIG_KEY, String.class))
                .filter(names -> names.contains(trickLoginUser))
                .isPresent();
    }
}
