package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountVO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.AccountConverter;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.AccountDTO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.account.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/3
 */
@Api(tags = "RD-Account相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdAccountController {
    private final static Logger logger = LoggerFactory.getLogger(RdAccountController.class);

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "添加账号", notes = "")
    @RequestMapping(value = "accounts", method = RequestMethod.POST)
    @ResponseBody
    public Result addAccount(@RequestBody AccountDTO dto) {
        if (!dto.legal() || ValidateUtils.isBlank(dto.getPassword())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ResultStatus rs = accountService.createAccount(AccountConverter.convert2AccountDO(dto));
        return Result.buildFrom(rs);
    }

    @ApiOperation(value = "删除账号", notes = "")
    @RequestMapping(value = "accounts", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteAccount(@RequestParam("username") String username) {
        ResultStatus rs = accountService.deleteByName(username, SpringTool.getUserName());
        return Result.buildFrom(rs);
    }

    @ApiOperation(value = "修改账号", notes = "")
    @RequestMapping(value = "accounts", method = RequestMethod.PUT)
    @ResponseBody
    public Result updateAccount(@RequestBody AccountDTO reqObj) {
        if (!reqObj.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        ResultStatus rs = accountService.updateAccount(AccountConverter.convert2AccountDO(reqObj));
        return Result.buildFrom(rs);
    }

    @ApiOperation(value = "账号列表", notes = "")
    @RequestMapping(value = "accounts", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AccountVO>> listAccounts() {
        try {
            List<AccountDO> accountDOList = accountService.list();
            return new Result<>(AccountConverter.convert2AccountVOList(accountDOList));
        } catch (Exception e) {
            logger.error("listAccounts@AdminAccountController, list failed.", e);
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }
}