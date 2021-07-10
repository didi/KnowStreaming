package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.KafkaFileDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaFileDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/7
 */
@Repository("kafkaFileDao")
public class KafkaFileDaoImpl implements KafkaFileDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(KafkaFileDO kafkaFileDO) {
        return sqlSession.insert("KafkaFileDao.insert", kafkaFileDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("KafkaFileDao.deleteById", id);
    }

    @Override
    public int updateById(KafkaFileDO kafkaFileDO) {
        return sqlSession.update("KafkaFileDao.updateById", kafkaFileDO);
    }

    @Override
    public List<KafkaFileDO> list() {
        return sqlSession.selectList("KafkaFileDao.list");
    }

    @Override
    public KafkaFileDO getById(Long id) {
        return sqlSession.selectOne("KafkaFileDao.getById", id);
    }

    @Override
    public KafkaFileDO getFileByFileName(String fileName) {
        return sqlSession.selectOne("KafkaFileDao.getFileByFileName", fileName);
    }
}
