package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.dao.gateway.KafkaUserDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Repository("kafkaUserDao")
public class KafkaUserDaoImpl implements KafkaUserDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public int insert(KafkaUserDO kafkaUserDO) {
        return sqlSession.insert("KafkaUserDao.insert", kafkaUserDO);
    }

    @Override
    public List<KafkaUserDO> getKafkaUsers(Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("KafkaUserDao.getKafkaUsers", params);
    }

    @Override
    public List<KafkaUserDO> listAll() {
        return sqlSession.selectList("KafkaUserDao.listAll");
    }
}