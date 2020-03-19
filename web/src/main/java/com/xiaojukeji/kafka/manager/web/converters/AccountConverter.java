package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.web.model.AccountModel;
import com.xiaojukeji.kafka.manager.common.entity.po.AccountDO;
import com.xiaojukeji.kafka.manager.web.vo.AccountVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/3
 */
public class AccountConverter {
    public static AccountDO convert2AccountDO(AccountModel accountModel) {
        AccountDO accountDO = new AccountDO();
        accountDO.setUsername(accountModel.getUsername());
        accountDO.setPassword(accountModel.getPassword());
        accountDO.setRole(accountModel.getRole());
        return accountDO;
    }

    public static List<AccountVO> convert2AccountVOList(List<AccountDO> accountDOList) {
        if (accountDOList == null) {
            return new ArrayList<>();
        }
        List<AccountVO> userVOList = new ArrayList<>();
        for (AccountDO accountDO: accountDOList) {
            userVOList.add(new AccountVO(accountDO.getUsername(), null, accountDO.getRole()));
        }
        return userVOList;
    }
}