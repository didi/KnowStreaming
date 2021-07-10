package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;

import java.util.List;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-04-26
 */
public interface AccountService {
    /**
     * 增加账号
     * @param accountDO 账号信息
     * @return
     */
    ResultStatus createAccount(AccountDO accountDO);

    /**
     * 查询账号信息
     * @param username 用户名
     * @return
     */
    Result<AccountDO> getAccountDO(String username);

    /**
     * 删除用户
     * @param username 用户名
     * @return
     */
    ResultStatus deleteByName(String username, String operator);

    /**
     * 更新账号
     * @param accountDO 账号信息
     * @return
     */
    ResultStatus updateAccount(AccountDO accountDO);

    /**
     * 获取用户列表
     * @return
     */
    List<AccountDO> list();

    /**
     * 依据前缀获取查询用户信息
     * @param prefix
     * @return
     */
    List<EnterpriseStaff> searchAccountByPrefix(String prefix);

    /**
     * 从cache中获取用户角色
     * @param username
     * @return
     */
    AccountRoleEnum getAccountRoleFromCache(String username);

    /**
     * 从cache中获取用户信息
     * @param userName
     * @return
     */
    Account getAccountFromCache(String userName);

    /**
     * 判断当前用户是否是管理员工单的审批人
     * @param username
     * @return
     */
    boolean isAdminOrderHandler(String username);

    /**
     * 是否是运维或者研发角色
     * @param username
     * @return
     */
    boolean isOpOrRd(String username);

    List<Account> getAdminOrderHandlerFromCache();
}
