package com.xiaojukeji.kafka.manager.account.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;
import com.xiaojukeji.kafka.manager.account.component.AbstractEnterpriseStaffService;
import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.common.utils.EncryptUtil;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.AccountDao;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-04-26
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    private static final String ADMIN_ORDER_HANDLER_CONFIG_KEY = "ADMIN_ORDER_HANDLER_CONFIG";

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AbstractEnterpriseStaffService enterpriseStaffService;

    @Autowired
    private OperateRecordService operateRecordService;

    /**
     * 用户组织信息
     * <username, Staff>
     */
    private static final Cache<String, EnterpriseStaff> USERNAME_STAFF_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(600, TimeUnit.MINUTES)
            .expireAfterWrite(600, TimeUnit.MINUTES).build();

    /**
     * 用户角色信息
     */
    private volatile Map<String, AccountRoleEnum> ACCOUNT_ROLE_CACHE = null;

    private volatile List<String> ADMIN_ORDER_HANDLER_CACHE = null;

    @Override
    public ResultStatus createAccount(AccountDO accountDO) {
        try {
            accountDO.setPassword(EncryptUtil.md5(accountDO.getPassword()));
            if (accountDao.addNewAccount(accountDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.info("create account failed, account already existed, accountDO:{}.", accountDO, e);
            return ResultStatus.RESOURCE_ALREADY_EXISTED;
        } catch (Exception e) {
            LOGGER.error("create account failed, operate mysql failed, accountDO:{}.", accountDO, e);
        }
        LOGGER.warn("class=AccountServiceImpl||method=createAccount||accountDO={}||msg=add account fail，{}!", accountDO,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus deleteByName(String username, String operator) {
        try {
            if (accountDao.deleteByName(username) > 0) {
                Map<String, String> content = new HashMap<>();
                content.put("username", username);
                operateRecordService.insert(operator, ModuleEnum.AUTHORITY, username, OperateEnum.DELETE, content);
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("delete account failed, username:{}.", username, e);
        }
        LOGGER.warn("class=AccountServiceImpl||method=deleteByName||username={}||msg=delete account fail,{}!", username,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus updateAccount(AccountDO accountDO) {
        try {
            AccountDO oldAccountDO = accountDao.getByName(accountDO.getUsername());
            if (ValidateUtils.isNull(oldAccountDO)) {
                return ResultStatus.ACCOUNT_NOT_EXIST;
            }

            if (!ValidateUtils.isBlank(accountDO.getPassword())) {
                accountDO.setPassword(EncryptUtil.md5(accountDO.getPassword()));
            } else {
                accountDO.setPassword(oldAccountDO.getPassword());
            }
            if (accountDao.updateByName(accountDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("update account failed, accountDO:{}.", accountDO, e);
        }
        LOGGER.warn("class=AccountServiceImpl||method=updateAccount||accountDO={}||msg=update account fail,{}!", accountDO,ResultStatus.MYSQL_ERROR.getMessage());
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public Result<AccountDO> getAccountDO(String username) {
        try {
            return Result.buildSuc(accountDao.getByName(username));
        } catch (Exception e) {
            LOGGER.warn("class=AccountServiceImpl||method=getAccountDO||username={}||errMsg={}||msg=get account fail", username, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @Override
    public List<AccountDO> list() {
        return accountDao.list();
    }


    private EnterpriseStaff getStaffData(String username) {
        EnterpriseStaff enterpriseStaff = USERNAME_STAFF_CACHE.getIfPresent(username);
        if (!ValidateUtils.isNull(enterpriseStaff)) {
            return enterpriseStaff;
        }

        enterpriseStaff = enterpriseStaffService.getEnterpriseStaffByName(username);
        if (ValidateUtils.isNull(enterpriseStaff)) {
            return null;
        }
        USERNAME_STAFF_CACHE.put(username, enterpriseStaff);
        return enterpriseStaff;
    }

    @Override
    public AccountRoleEnum getAccountRoleFromCache(String username) {
        if (ValidateUtils.isNull(ACCOUNT_ROLE_CACHE)) {
            flush();
        }
        return ACCOUNT_ROLE_CACHE.getOrDefault(username, AccountRoleEnum.NORMAL);
    }

    private Map<String, AccountRoleEnum> getAdminRoleEnum() {
        Map<String, AccountRoleEnum> ldapMap = new HashMap<>();
        if (ValidateUtils.isNull(ACCOUNT_ROLE_CACHE)) {
            flush();
        }
        for (Map.Entry<String, AccountRoleEnum> entry : ACCOUNT_ROLE_CACHE.entrySet()) {
            if (!AccountRoleEnum.OP.equals(entry.getValue()) &&
                    !AccountRoleEnum.RD.equals(entry.getValue())) {
                continue;
            }
            ldapMap.put(entry.getKey(), entry.getValue());
        }
        return ldapMap;
    }

    @Override
    public Account getAccountFromCache(String username) {
        Account account = new Account();
        account.setUsername(username);
        if (Constant.AUTO_HANDLE_USER_NAME.equals(username)) {
            account.setChineseName(Constant.AUTO_HANDLE_CHINESE_NAME);
            account.setAccountRoleEnum(AccountRoleEnum.OP);
            return account;
        }

        AccountRoleEnum roleEnum = this.getAccountRoleFromCache(username);
        account.setAccountRoleEnum(roleEnum);

        EnterpriseStaff enterpriseStaff = this.getStaffData(username);
        if (ValidateUtils.isNull(enterpriseStaff)) {
            account.setChineseName(username);
            return account;
        }
        account.setDepartment(enterpriseStaff.getDepartment());
        account.setChineseName(enterpriseStaff.getChineseName());
        return account;
    }

    private List<Account> getOPAccountsFromCache() {
        List<Account> accountList = new ArrayList<>();
        for (Map.Entry<String, AccountRoleEnum> entry : getAdminRoleEnum().entrySet()) {
            AccountRoleEnum role = entry.getValue();
            if (!AccountRoleEnum.OP.getRole().equals(role.getRole())) {
                continue;
            }
            Account account = this.getAccountFromCache(entry.getKey());
            if (ValidateUtils.isNull(account)) {
                continue;
            }
            accountList.add(account);
        }
        return accountList;
    }

    private boolean isOp(String username) {
        if (ValidateUtils.isNull(ACCOUNT_ROLE_CACHE)) {
            flush();
        }
        AccountRoleEnum accountRoleEnum = ACCOUNT_ROLE_CACHE.getOrDefault(username, AccountRoleEnum.NORMAL);
        if (accountRoleEnum.equals(AccountRoleEnum.OP)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAdminOrderHandler(String username) {
        if (isOp(username)) {
            return true;
        }
        if (!ValidateUtils.isEmptyList(ADMIN_ORDER_HANDLER_CACHE)
                && ADMIN_ORDER_HANDLER_CACHE.contains(username)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOpOrRd(String username) {
        if (ValidateUtils.isNull(ACCOUNT_ROLE_CACHE)) {
            flush();
        }
        AccountRoleEnum accountRoleEnum = ACCOUNT_ROLE_CACHE.getOrDefault(username, AccountRoleEnum.NORMAL);
        if (accountRoleEnum.equals(AccountRoleEnum.OP) || accountRoleEnum.equals(AccountRoleEnum.RD)) {
            return true;
        }
        return false;
    }

    @Override
    public List<Account> getAdminOrderHandlerFromCache() {
        if (ValidateUtils.isEmptyList(ADMIN_ORDER_HANDLER_CACHE)) {
            return getOPAccountsFromCache();
        }

        List<Account> accountList = new ArrayList<>();
        for (String ldap : ADMIN_ORDER_HANDLER_CACHE) {
            Account account = this.getAccountFromCache(ldap);
            if (ValidateUtils.isNull(account)) {
                continue;
            }
            accountList.add(account);
        }
        return accountList;
    }

    @Override
    public List<EnterpriseStaff> searchAccountByPrefix(String prefix) {
        return enterpriseStaffService.searchEnterpriseStaffByKeyWord(prefix);
    }

    @Scheduled(cron ="0/5 * * * * ?")
    public void flush() {
        try {
            ADMIN_ORDER_HANDLER_CACHE =
                    configService.getArrayByKey(ADMIN_ORDER_HANDLER_CONFIG_KEY, String.class);
        } catch (Exception e) {
            LOGGER.error("flush handler account failed.", e);
        }

        try {
            List<AccountDO> doList = accountDao.list();
            if (ValidateUtils.isNull(doList)) {
                doList = new ArrayList<>();
            }

            Map<String, AccountRoleEnum> tempMap = new ConcurrentHashMap<>(doList.size());
            for (AccountDO accountDO: doList) {
                tempMap.put(accountDO.getUsername(), AccountRoleEnum.getUserRoleEnum(accountDO.getRole()));
            }
            ACCOUNT_ROLE_CACHE = tempMap;
        } catch (Exception e) {
            LOGGER.error("flush account failed.", e);
        }
    }
}
