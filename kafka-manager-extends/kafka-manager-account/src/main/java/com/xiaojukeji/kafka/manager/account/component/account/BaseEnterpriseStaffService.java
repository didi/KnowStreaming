package com.xiaojukeji.kafka.manager.account.component.account;

import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.account.component.AbstractEnterpriseStaffService;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/20
 */
@Service("enterpriseStaffService")
public class BaseEnterpriseStaffService extends AbstractEnterpriseStaffService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEnterpriseStaffService.class);

    @Autowired
    private AccountDao accountDao;

    @Override
    public EnterpriseStaff getEnterpriseStaffByName(String username) {
        try {
            AccountDO accountDO = accountDao.getByName(username);
            if (ValidateUtils.isNull(accountDO)) {
                return null;
            }
            return new EnterpriseStaff(accountDO.getUsername(), accountDO.getUsername(), "");
        } catch (Exception e) {
            LOGGER.error("get enterprise staff info failed, username:{}.", username, e);
        }
        return null;
    }

    @Override
    public List<EnterpriseStaff> searchEnterpriseStaffByKeyWord(String keyWord) {
        try {
            List<AccountDO> doList = null;
            if (ValidateUtils.isBlank(keyWord)) {
                // 当用户没有任何输入的时候, 返回全部的用户
                doList = accountDao.list();
            } else {
                doList = accountDao.searchByNamePrefix(keyWord);
            }

            if (ValidateUtils.isEmptyList(doList)) {
                return new ArrayList<>();
            }
            List<EnterpriseStaff> staffList = new ArrayList<>();
            for (AccountDO accountDO: doList) {
                staffList.add(new EnterpriseStaff(accountDO.getUsername(), accountDO.getUsername(), ""));
            }
            return staffList;
        } catch (Exception e) {
            LOGGER.error("search enterprise staff info failed, prefix:{}.", keyWord, e);
        }
        return new ArrayList<>();
    }
}
