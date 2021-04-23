package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.LoginDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.account.LoginService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "登陆", notes = "")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Result<AccountVO> login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginDTO dto){
        Result<Account> accountResult = loginService.login(request, response, dto);
        if (ValidateUtils.isNull(accountResult) || accountResult.failed()) {
            return new Result<>(accountResult.getCode(), accountResult.getMessage());
        }
        AccountVO vo = new AccountVO();
        vo.setUsername(accountResult.getData().getUsername());
        vo.setChineseName(accountResult.getData().getChineseName());
        vo.setDepartment(accountResult.getData().getDepartment());
        vo.setRole(accountResult.getData().getAccountRoleEnum().getRole());
        return new Result<>(vo);
    }

    @ApiOperation(value = "登出", notes = "")
    @RequestMapping(value = "logout", method = RequestMethod.DELETE)
    @ResponseBody
    public Result logoff(HttpServletRequest request, HttpServletResponse response) {
        loginService.logout(request, response, true);
        return new Result();
    }
}
