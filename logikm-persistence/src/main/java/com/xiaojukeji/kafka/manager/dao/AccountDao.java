package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/3
 */
public interface AccountDao {
    int addNewAccount(AccountDO userDO);

    int deleteByName(String username);

    int updateByName(AccountDO userDO);

    List<AccountDO> list();

    AccountDO getByName(String username);

    List<AccountDO> searchByNamePrefix(String prefix);
}