package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.account.LoginService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登陆
 * @author huangyiminghappy@163.com
 * @date 19/4/29
 */
@Api(tags = "SSO-Login相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_SSO_PREFIX)
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "登陆", notes = "")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Result<AccountVO> login(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestBody LoginDTO dto){
        Account account = loginService.login(request, response, dto);
        if (ValidateUtils.isNull(account)) {
            return Result.buildFrom(ResultStatus.LOGIN_FAILED);
        }
        AccountVO vo = new AccountVO();
        vo.setUsername(account.getUsername());
        vo.setChineseName(account.getChineseName());
        vo.setDepartment(account.getDepartment());
        vo.setRole(account.getAccountRoleEnum().getRole());
        return new Result<>(vo);
    }

    @ApiOperation(value = "登出", notes = "")
    @RequestMapping(value = "logout", method = RequestMethod.DELETE)
    @ResponseBody
    public Result logoff(HttpServletRequest request, HttpServletResponse response) {
        loginService.logout(request, response, true);
        return new Result();
    }

    @Deprecated
    @ApiOperation(value = "登录检查", notes = "检查SSO返回的Code")
    @RequestMapping(value = "xiaojukeji/login-check", method = RequestMethod.POST)
    @ResponseBody
    public Result<AccountVO> checkCodeAndGetStaffInfo(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      @RequestBody LoginDTO dto) {
        Result<AccountVO> ra = login(request, response, dto);
        if (!Constant.SUCCESS.equals(ra.getCode())) {
            LOGGER.info("user login failed, req:{} result:{}.", dto, ra);
        } else {
            LOGGER.info("user login success, req:{} result:{}.", dto, ra);
        }
        return ra;
    }

    @Deprecated
    @ApiOperation(value = "登出", notes = "")
    @RequestMapping(value = "xiaojukeji/logout", method = RequestMethod.DELETE)
    @ResponseBody
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        return logoff(request, response);
    }
}
