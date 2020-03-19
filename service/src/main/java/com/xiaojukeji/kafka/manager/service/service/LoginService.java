package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.AccountRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.AccountDO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-04-26
 */
public interface LoginService {
    /**
     * 登陆
     * @param request request
     * @param username username
     * @param password password
     * @return Result
     */
    Result<AccountRoleEnum> login(HttpServletRequest request, String username, String password);

    /**
     * 登出
     * @param request request
     * @param username username
     * @return Result
     */
    Result logoff(HttpServletRequest request, String username);

    /**
     * 添加新账号
     * @param accountDO accountDO
     * @return Result
     */
    Result addNewAccount(AccountDO accountDO);

    /**
     * 删除用户
     * @param username username
     * @return boolean
     */
    boolean deleteByName(String username);

    /**
     * 更新账户
     * @param accountDO accountDO
     * @param oldPassword 老密码
     * @return Result
     */
    Result updateAccount(AccountDO accountDO, String oldPassword);

    /**
     * 用户列表
     * @return List<AccountDO>
     */
    List<AccountDO> list();

    /**
     * 是否登陆了
     * @param username username
     * @return boolean
     */
    boolean isLogin(String username);
}
