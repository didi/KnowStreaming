package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.dao.KafkaBillDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;
import com.xiaojukeji.kafka.manager.service.service.KafkaBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/12
 */
@Service("kafkaBillService")
public class KafkaBillServiceImpl implements KafkaBillService {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaBillServiceImpl.class);

    @Autowired
    private KafkaBillDao kafkaBillDao;

    @Override
    public int replace(KafkaBillDO kafkaBillDO) {
        try {
            return kafkaBillDao.replace(kafkaBillDO);
        } catch (Exception e) {
            LOGGER.error("replace kafka bill failed, kafkaBillDO:{}.", kafkaBillDO, e);
        }
        return 0;
    }

    @Override
    public List<KafkaBillDO> getByTopicName(Long clusterId, String topicName, Date startTime, Date endTime) {
        try {
            return kafkaBillDao.getByTopicName(clusterId, topicName, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get kafka bill list failed, clusterId:{}, topicName:{}.", clusterId, topicName, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<KafkaBillDO> getByPrincipal(String principal, Date startTime, Date endTime) {
        try {
            return kafkaBillDao.getByPrincipal(principal, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get kafka bill list failed, principal:{}.", principal, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<KafkaBillDO> getByTimeBetween(Date startTime, Date endTime) {
        try {
            return kafkaBillDao.getByTimeBetween(startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get kafka bill list failed, startTime:{}, endTime:{}.", startTime, endTime, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<KafkaBillDO> getByGmtDay(String gmtDay) {
        try {
            return kafkaBillDao.getByGmtDay(gmtDay);
        } catch (Exception e) {
            LOGGER.error("get kafka bill list failed, gmtDay:{}.", gmtDay, e);
        }
        return new ArrayList<>();
    }
}