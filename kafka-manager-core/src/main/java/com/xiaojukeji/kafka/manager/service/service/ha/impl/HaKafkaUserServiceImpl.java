package com.xiaojukeji.kafka.manager.service.service.ha.impl;

import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.service.service.ha.HaKafkaUserService;
import com.xiaojukeji.kafka.manager.service.utils.HaKafkaUserCommands;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;

@Service
public class HaKafkaUserServiceImpl implements HaKafkaUserService {

    @Override
    public Result<Void> setNoneHAInKafka(String zookeeper,  String kafkaUser) {
        Properties props = new Properties();
        props.put(KafkaConstant.DIDI_HA_ACTIVE_CLUSTER, KafkaConstant.NONE);

        return HaKafkaUserCommands.modifyHaUserConfig(zookeeper, kafkaUser, props)?
                Result.buildSuc(): // 修改成功
                Result.buildFrom(ResultStatus.ZOOKEEPER_OPERATE_FAILED); // 修改失败
    }

    @Override
    public Result<Void> stopHAInKafka(String zookeeper, String kafkaUser) {
        return HaKafkaUserCommands.deleteHaUserConfig(zookeeper, kafkaUser, Arrays.asList(KafkaConstant.DIDI_HA_ACTIVE_CLUSTER))?
                Result.buildSuc(): // 修改成功
                Result.buildFrom(ResultStatus.ZOOKEEPER_OPERATE_FAILED); // 修改失败
    }

    @Override
    public Result<Void> activeHAInKafka(String zookeeper, Long activeClusterPhyId, String kafkaUser) {
        Properties props = new Properties();
        props.put(KafkaConstant.DIDI_HA_ACTIVE_CLUSTER, String.valueOf(activeClusterPhyId));

        return HaKafkaUserCommands.modifyHaUserConfig(zookeeper, kafkaUser, props)?
                Result.buildSuc(): // 修改成功
                Result.buildFrom(ResultStatus.ZOOKEEPER_OPERATE_FAILED); // 修改失败
    }
}
