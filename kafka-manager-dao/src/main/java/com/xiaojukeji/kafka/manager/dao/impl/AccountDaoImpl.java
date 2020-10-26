package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
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
        return sqlSession.insert("AccountDao.insert", accountDO);
    }

    @Override
    public int deleteByName(String username) {
        return sqlSession.delete("AccountDao.deleteByName", username);
    }

    @Override
    public int updateByName(AccountDO accountDO) {
        return sqlSession.insert("AccountDao.updateByName", accountDO);
    }

    @Override
    public List<AccountDO> list() {
        return sqlSession.selectList("AccountDao.list");
    }

    @Override
    public AccountDO getByName(String username) {
        return sqlSession.selectOne("AccountDao.getByName", username);
    }

    @Override
    public List<AccountDO> searchByNamePrefix(String prefix) {
        return sqlSession.selectList("AccountDao.searchByNamePrefix", prefix);
    }
}