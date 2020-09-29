package com.xiaojukeji.kafka.manager.notify.common;

import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;

import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/27
 */
public class NotifyConstant {

    public static final List<Account> accountList = Arrays.asList(
            new Account("xuzhengxi", "徐正熙", "", AccountRoleEnum.OP)
    );
}