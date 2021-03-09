package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountRoleVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountSummaryVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.api.versionone.gateway.GatewayHeartbeatController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/27
 */
@Api(tags = "Normal-Account相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalAccountController {

    private final static Logger LOGGER = LoggerFactory.getLogger(NormalAccountController.class);

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "账号搜索", notes = "仅支持搜索, 不支持全部展示")
    @RequestMapping(value = "accounts", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AccountSummaryVO>> searchOnJobStaffByKeyWord(@RequestParam("keyWord") String keyWord) {
        List<EnterpriseStaff> staffList = accountService.searchAccountByPrefix(keyWord);
        if (ValidateUtils.isEmptyList(staffList)) {
            LOGGER.info("class=NormalAccountController||method=searchOnJobStaffByKeyWord||keyWord={}||msg=staffList is empty!", keyWord);
            return new Result<>();
        }
        List<AccountSummaryVO> voList = new ArrayList<>();
        for (EnterpriseStaff staff: staffList) {
            AccountSummaryVO vo = new AccountSummaryVO();
            vo.setUsername(staff.getUsername());
            vo.setChineseName(staff.getChineseName());
            vo.setDepartment(staff.getDepartment());
            voList.add(vo);
        }
        return new Result<>(voList);
    }

    @ApiOperation(value = "查询角色", notes = "")
    @RequestMapping(value = "accounts/account", method = RequestMethod.GET)
    @ResponseBody
    public Result<AccountRoleVO> searchAccount() {
        String username = SpringTool.getUserName();
        AccountRoleEnum accountRoleEnum = accountService.getAccountRoleFromCache(username);
        return new Result<>(new AccountRoleVO(username, accountRoleEnum.getRole()));
    }
}