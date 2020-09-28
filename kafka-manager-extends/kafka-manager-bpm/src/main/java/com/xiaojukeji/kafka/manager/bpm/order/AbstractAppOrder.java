package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/20
 */
public abstract class AbstractAppOrder extends AbstractOrder {
    @Autowired
    private AccountService accountService;

    @Override
    public ResultStatus checkAuthority(OrderDO orderDO, String username) {
        if (!accountService.isAdminOrderHandler(username)) {
            return ResultStatus.USER_WITHOUT_AUTHORITY;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public List<Account> getApproverList(String extensions) {
        return accountService.getAdminOrderHandlerFromCache();
    }
}
