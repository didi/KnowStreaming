package com.xiaojukeji.know.streaming.km.account.login.trick.impl;

import com.didiglobal.logi.security.common.vo.config.ConfigVO;
import com.didiglobal.logi.security.service.ConfigService;
import com.xiaojukeji.know.streaming.km.account.common.constant.TrickJumpLoginConstant;
import com.xiaojukeji.know.streaming.km.account.login.trick.TrickJumpLoginService;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author zengqiao
 * @date 21/5/18
 */
@Service
public class TrickJumpLoginServiceImpl implements TrickJumpLoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrickJumpLoginServiceImpl.class);

    @Autowired
    private ConfigService configService;

    @Override
    public boolean isOpenTrickJumpLogin(HttpServletRequest request) {
        return TrickJumpLoginConstant.TRICK_JUMP_LOGIN_SWITCH_ON.equals(request.getHeader(TrickJumpLoginConstant.TRICK_JUMP_LOGIN_SWITCH));
    }

    @Override
    public String checkTrickJumpLogin(HttpServletRequest request) {
        String trickLoginUser = request.getHeader(TrickJumpLoginConstant.TRICK_JUMP_LOGIN_USER);

        LOGGER.info("method=checkTrickJumpLogin||userName={}||uri={}||msg=try trick jump login", trickLoginUser, request.getRequestURI());
        if (!checkTrickJumpLogin(trickLoginUser)) {
            LOGGER.warn("method=checkTrickJumpLogin||userName={}||uri={}||msg=trick login failed", trickLoginUser, request.getRequestURI());
            return null;
        }

        return trickLoginUser;
    }

    private boolean checkTrickJumpLogin(String trickJumpLoginUser) {
        List<ConfigVO> voList = configService.listConfigByGroup(TrickJumpLoginConstant.TRICK_JUMP_LOGIN_LEGAL_USER_CONFIG_GROUP)
                .stream()
                .filter(elem -> elem.getValueName().equals(TrickJumpLoginConstant.TRICK_JUMP_LOGIN_LEGAL_USER_CONFIG_NAME))
                .collect(Collectors.toList());
        if (ValidateUtils.isEmptyList(voList)) {
            // 不存在
            return false;
        }

        for (ConfigVO vo: voList) {
            List<String> canJumpUserNameList = ConvertUtil.str2ObjArrayByJson(vo.getValue(), String.class);
            if (ValidateUtils.isEmptyList(canJumpUserNameList)) {
                continue;
            }

            if (canJumpUserNameList.contains(trickJumpLoginUser)) {
                return true;
            }
        }

        return false;
    }
}
