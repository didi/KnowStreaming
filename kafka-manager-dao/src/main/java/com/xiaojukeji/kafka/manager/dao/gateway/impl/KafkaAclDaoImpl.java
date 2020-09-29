package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.dao.gateway.KafkaAclDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
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
@Repository("kafkaAclDao")
public class KafkaAclDaoImpl implements KafkaAclDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public List<KafkaAclDO> getKafkaAcls(Long clusterId, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("KafkaAclDao.getKafkaAcls", params);
    }

    @Override
    public int insert(KafkaAclDO kafkaAclDO) {
        return sqlSession.insert("KafkaAclDao.insert", kafkaAclDO);
    }

    @Override
    public List<KafkaAclDO> listAll() {
        return sqlSession.selectList("KafkaAclDao.listAll");
    }
}