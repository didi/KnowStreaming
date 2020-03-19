package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.service.service.LoginService;
import com.xiaojukeji.kafka.manager.web.model.LoginModel;
import com.xiaojukeji.kafka.manager.web.vo.AccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 登陆
 * @author huangyiminghappy@163.com
 * @date 19/4/29
 */
@Api(value = "LoginController", description = "Login相关接口")
@Controller
@RequestMapping("")
public class LoginController {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "登陆", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AccountVO.class)
    @RequestMapping(value = "api/v1/login/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result login(HttpServletRequest request, @RequestBody LoginModel loginModel){
        if (loginModel == null || !loginModel.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "参数错误");
        }

        AccountRoleEnum accountRoleEnum = null;
        try {
            Result result = loginService.login(request, loginModel.getUsername(), loginModel.getPassword());
            if (!StatusCode.SUCCESS.equals(result.getCode())) {
                return result;
            }
            accountRoleEnum = (AccountRoleEnum) result.getData();
        } catch (Exception e) {
            logger.error("login@LoginController, login failed, req:{}.", loginModel, e);
            return new Result(StatusCode.PARAM_ERROR, "param error");
        }
        return new Result<>(new AccountVO(loginModel.getUsername(), null, accountRoleEnum.getRole()));
    }

    @ApiOperation(value = "登出", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "api/v1/login/logoff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result logoff(HttpServletRequest request, @RequestParam(value="username") String username) {
        if (StringUtils.isEmpty(username)) {
            return new Result(StatusCode.PARAM_ERROR, "param error");
        }
        return loginService.logoff(request, username);
    }
}
