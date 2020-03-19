package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.AccountDO;
import com.xiaojukeji.kafka.manager.common.utils.EncryptUtil;
import com.xiaojukeji.kafka.manager.dao.AccountDao;
import com.xiaojukeji.kafka.manager.service.service.LoginService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-04-26
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {
    /**
     * <userName, <expire timestamp, role>>
     */
    private static final Map<String, HttpSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Autowired
    private AccountDao accountDao;

    @Override
    public Result<AccountRoleEnum> login(HttpServletRequest request, String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new Result<>(StatusCode.PARAM_ERROR, "参数错误");
        }
        AccountDO accountDO = accountDao.getByName(username);
        if (accountDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "用户/密码不存在");
        }

        if (!accountDO.getPassword().equals(EncryptUtil.md5(password))) {
            return new Result<>(StatusCode.PARAM_ERROR, "用户/密码错误");
        }
        AccountRoleEnum userRoleEnum = AccountRoleEnum.getUserRoleEnum(accountDO.getRole());
        if (userRoleEnum == null) {
            return new Result<>(StatusCode.OPERATION_ERROR, "用户无权限");
        }

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(24 * 60 * 60);
        session.setAttribute("role", userRoleEnum);
        session.setAttribute("username", username);
        SESSION_MAP.put(username, session);
        return new Result<>(userRoleEnum);
    }

    @Override
    public Result logoff(HttpServletRequest request, String username) {
        if (StringUtils.isEmpty(username)) {
            return new Result(StatusCode.PARAM_ERROR, "param error");
        }
        try {
            request.getSession().invalidate();
            removeSession(username);
        } catch (Exception e) {
        }
        return new Result();
    }

    @Override
    public Result addNewAccount(AccountDO accountDO) {
        AccountDO oldAccountDO = accountDao.getByName(accountDO.getUsername());
        if (oldAccountDO != null) {
            return new Result(StatusCode.PARAM_ERROR, "account already exist");
        }
        accountDO.setPassword(EncryptUtil.md5(accountDO.getPassword()));
        accountDO.setStatus(0);
        int status = accountDao.addNewAccount(accountDO);
        if (status > 0) {
            return new Result();
        }
        return new Result(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
    }

    @Override
    public boolean deleteByName(String userName) {
        removeSession(userName);
        return accountDao.deleteByName(userName) > 0;
    }

    @Override
    public Result updateAccount(AccountDO accountDO, String oldPassword) {
        AccountDO oldAccountDO = accountDao.getByName(accountDO.getUsername());
        if (oldAccountDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "account not exist");
        }
        if (!StringUtils.isEmpty(oldPassword)) {
            String md5Sum = EncryptUtil.md5(oldPassword);
            if (oldAccountDO.getPassword().equals(md5Sum)) {
                accountDO.setPassword(md5Sum);
            } else {
                return new Result(StatusCode.OPERATION_ERROR, "old password is wrong");
            }
        } else {
            accountDO.setPassword(StringUtils.isEmpty(accountDO.getPassword())? oldAccountDO.getPassword(): EncryptUtil.md5(accountDO.getPassword()));
        }
        accountDO.setRole(accountDO.getRole() == null? oldAccountDO.getRole(): accountDO.getRole());
        accountDO.setStatus(accountDO.getStatus() == null? oldAccountDO.getStatus(): accountDO.getStatus());
        int status = accountDao.updateAccount(accountDO);
        if (status > 0) {
            removeSession(accountDO.getUsername());
            return new Result();
        }
        return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
    }

    private void removeSession(String username) {
        if (StringUtils.isEmpty(username)) {
            return;
        }
        HttpSession httpSession = SESSION_MAP.get(username);
        SESSION_MAP.remove(username);
        if (httpSession != null) {
            httpSession.invalidate();
        }
    }

    @Override
    public List<AccountDO> list() {
        return accountDao.list();
    }

    @Override
    public boolean isLogin(String userName) {
        if (!StringUtils.isEmpty(userName) && SESSION_MAP.containsKey(userName)) {
            return true;
        }
        return false;
    }
}
