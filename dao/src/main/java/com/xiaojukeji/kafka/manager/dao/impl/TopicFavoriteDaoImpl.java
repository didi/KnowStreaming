package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.TopicFavoriteDO;
import com.xiaojukeji.kafka.manager.dao.TopicFavoriteDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 19/6/23
 */
@Repository("topicFavoriteDao")
public class TopicFavoriteDaoImpl implements TopicFavoriteDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private KafkaManagerProperties kafkaManagerProperties;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<TopicFavoriteDO> topicFavoriteDOList) {
        if (kafkaManagerProperties.hasPG()) {
            return sqlSession.insert("TopicFavoriteDao.batchAddOnPG", topicFavoriteDOList);
        }
        return sqlSession.insert("TopicFavoriteDao.batchAdd", topicFavoriteDOList);
    }

    @Override
    public Boolean batchDelete(List<Long> idList) {
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                if (idList == null) {
                    return Boolean.TRUE;
                }
                for (Long id: idList) {
                    try {
                        if (deleteById(id) <= 0) {
                            status.setRollbackOnly();
                            return Boolean.FALSE;
                        }
                    } catch (Throwable t) {
                        status.setRollbackOnly();
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }
        });
    }

    private int deleteById(Long id) {
        return sqlSession.delete("TopicFavoriteDao.deleteById", id);
    }

    @Override
    public List<TopicFavoriteDO> getByUserName(String username) {
        return sqlSession.selectList("TopicFavoriteDao.getByUserName", username);
    }

    @Override
    public List<TopicFavoriteDO> getByUserNameAndClusterId(String username, Long clusterId) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("clusterId", clusterId);
        return sqlSession.selectList("TopicFavoriteDao.getByUserNameAndClusterId", params);
    }

}