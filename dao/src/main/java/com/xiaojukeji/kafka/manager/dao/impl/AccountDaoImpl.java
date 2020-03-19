package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.AccountDO;
import com.xiaojukeji.kafka.manager.dao.AccountDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/3
 */
@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int addNewAccount(AccountDO accountDO) {
        accountDO.setStatus(DBStatusEnum.NORMAL.getStatus());
        return sqlSession.insert("AccountDao.insert", accountDO);
    }

    @Override
    public int deleteByName(String username) {
        return sqlSession.delete("AccountDao.deleteByName", username);
    }

    @Override
    public int updateAccount(AccountDO accountDO) {
        return sqlSession.insert("AccountDao.insert", accountDO);
    }

    @Override
    public List<AccountDO> list() {
        return sqlSession.selectList("AccountDao.list");
    }

    @Override
    public AccountDO getByName(String username) {
        return sqlSession.selectOne("AccountDao.getByName", username);
    }

}