package com.xiaojukeji.kafka.manager.service.service.gateway.impl;

import com.xiaojukeji.kafka.manager.dao.gateway.KafkaAclDao;
import com.xiaojukeji.kafka.manager.dao.gateway.KafkaUserDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/27
 */
@Service("securityService")
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private KafkaUserDao kafkaUserDao;

    @Autowired
    private KafkaAclDao kafkaAclDao;

    @Override
    public List<KafkaUserDO> getKafkaUsers(Long startTime, Long endTime) {
        return kafkaUserDao.getKafkaUsers(new Date(startTime), new Date(endTime));
    }

    @Override
    public List<KafkaAclDO> getKafkaAcls(Long clusterId, Long startTime, Long endTime) {
        return kafkaAclDao.getKafkaAcls(clusterId, new Date(startTime), new Date(endTime));
    }
}