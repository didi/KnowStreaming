package com.xiaojukeji.kafka.manager.account.component.sso;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.account.component.AbstractSingleSignOn;
import com.xiaojukeji.kafka.manager.common.constant.LoginConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.common.utils.EncryptUtil;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zengqiao
 * @date 20/8/20
 */
@Service("singleSignOn")
public class BaseSessionSignOn extends AbstractSingleSignOn {
    @Autowired
    private AccountService accountService;

    @Override
    public Result<String> loginAndGetLdap(HttpServletRequest request, HttpServletResponse response, LoginDTO dto) {
        if (ValidateUtils.isBlank(dto.getUsername()) || ValidateUtils.isNull(dto.getPassword())) {
            return null;
        }
        Result<AccountDO> accountResult = accountService.getAccountDO(dto.getUsername());
        if (ValidateUtils.isNull(accountResult) || accountResult.failed()) {
            return new Result<>(accountResult.getCode(), accountResult.getMessage());
        }
        if (ValidateUtils.isNull(accountResult.getData())) {
            return Result.buildFailure("username illegal");
        }
        if (!accountResult.getData().getPassword().equals(EncryptUtil.md5(dto.getPassword()))) {
            return Result.buildFailure("password illegal");
        }
        return Result.buildSuc(accountResult.getData().getUsername());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Boolean needJump2LoginPage) {
        request.getSession().invalidate();
        if (needJump2LoginPage) {
            response.setStatus(AbstractSingleSignOn.REDIRECT_CODE);
            response.addHeader(AbstractSingleSignOn.HEADER_REDIRECT_KEY, "");
        }
    }

    @Override
    public String checkLoginAndGetLdap(HttpServletRequest request) {
        Object username = request.getSession().getAttribute(LoginConstant.SESSION_USERNAME_KEY);
        if (ValidateUtils.isNull(username)) {
            return null;
        }
        return (String) username;
    }

    @Override
    public void setRedirectToLoginPage(HttpServletResponse response) {
        response.setStatus(AbstractSingleSignOn.REDIRECT_CODE);
        response.addHeader(AbstractSingleSignOn.HEADER_REDIRECT_KEY, "");
    }
}