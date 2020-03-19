package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.web.converters.AccountConverter;
import com.xiaojukeji.kafka.manager.web.model.AccountModel;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.AccountDO;
import com.xiaojukeji.kafka.manager.service.service.LoginService;
import com.xiaojukeji.kafka.manager.web.vo.AccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/3
 */
@Api(value = "AdminAccountController", description = "Account相关接口")
@RestController
@RequestMapping("api/v1/")
public class AdminAccountController {
    private final static Logger logger = LoggerFactory.getLogger(AdminAccountController.class);

    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "添加账号", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AccountVO.class)
    @RequestMapping(value = "admin/accounts/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<AccountVO> addAccount(@RequestBody AccountModel reqObj) {
        if (reqObj == null || !reqObj.insertLegal()) {
            return new Result<>(StatusCode.PARAM_ERROR, "param error");
        }
        AccountDO accountDO = AccountConverter.convert2AccountDO(reqObj);
        String password = accountDO.getPassword();
        try {
            Result result = loginService.addNewAccount(accountDO);
            if (!StatusCode.SUCCESS.equals(result.getCode())) {
                return result;
            }
        } catch (DuplicateKeyException e) {
            return new Result<>(StatusCode.PARAM_ERROR, "account already exist");
        } catch (Exception e) {
            logger.error("addAccount@AdminAccountController, create failed, req:{}.", reqObj, e);
            return new Result<>(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(new AccountVO(accountDO.getUsername(), password, accountDO.getRole()));
    }

    @ApiOperation(value = "删除账号", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "admin/accounts/account", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result deleteAccount(@RequestParam("username") String username) {
        if (StringUtils.isEmpty(username)) {
            return new Result(StatusCode.PARAM_ERROR, "param error");
        }
        try {
            if (!loginService.deleteByName(username)){
                return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
            }
        } catch (Exception e) {
            logger.error("deleteAccount@AdminAccountController, delete failed, username:{}.", username, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result();
    }

    @ApiOperation(value = "修改账号", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "admin/accounts/account", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result adminUpdateAccount(@RequestBody AccountModel reqObj) {
        if (reqObj == null || !reqObj.modifyLegal()) {
            return new Result(StatusCode.PARAM_ERROR, "参数错误");
        }
        AccountDO accountDO = AccountConverter.convert2AccountDO(reqObj);
        try {
            return loginService.updateAccount(accountDO, null);
        } catch (Exception e) {
            logger.error("updateAccount@AdminAccountController, update failed, req:{}.", reqObj, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }

    @ApiOperation(value = "修改账号", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "accounts/account", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result selfUpdateAccount(@RequestBody AccountModel reqObj) {
        if (reqObj == null || !reqObj.modifyLegal() || StringUtils.isEmpty(reqObj.getPassword()) || StringUtils.isEmpty(reqObj.getOldPassword())) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        AccountDO accountDO = AccountConverter.convert2AccountDO(reqObj);
        try {
            return loginService.updateAccount(accountDO, reqObj.getOldPassword());
        } catch (Exception e) {
            logger.error("updateAccount@AdminAccountController, update failed, req:{}.", reqObj, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }

    @ApiOperation(value = "账号列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AccountVO.class)
    @RequestMapping(value = "admin/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<AccountVO>> listAccounts() {
        try {
            List<AccountDO> accountDOList = loginService.list();
            return new Result<>(AccountConverter.convert2AccountVOList(accountDOList));
        } catch (Exception e) {
            logger.error("listAccounts@AdminAccountController, list failed.", e);
            return new Result<>(StatusCode.MY_SQL_SELECT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }
}