package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.dao.OperateRecordDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 2020/09/03
 */
@Repository("operateRecordDao")
public class OperateRecordDaoImpl implements OperateRecordDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(OperateRecordDO operateRecordDO) {
        return sqlSession.insert("OperateRecordDao.insert", operateRecordDO);
    }

    @Override
    public List<OperateRecordDO> queryByCondition(Integer moduleId, Integer operateId, String operator, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(5);
        params.put("moduleId", moduleId);
        params.put("operateId", operateId);
        params.put("operator", operator);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("OperateRecordDao.queryByCondition", params);
    }
}
