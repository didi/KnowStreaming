package com.xiaojukeji.kafka.manager.account;

import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;

import java.util.List;
import java.util.Map;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-04-26
 */
public interface AccountService {
    ResultStatus createAccount(AccountDO accountDO);

    AccountDO getAccountDO(String username);

    ResultStatus deleteByName(String username);

    ResultStatus updateAccount(AccountDO accountDO);

    List<AccountDO> list();

    List<EnterpriseStaff> searchAccountByPrefix(String prefix);

    AccountRoleEnum getAccountRoleFromCache(String username);

    Account getAccountFromCache(String userName);

    boolean isAdminOrderHandler(String username);

    List<Account> getAdminOrderHandlerFromCache();
}
