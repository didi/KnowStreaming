package com.xiaojukeji.kafka.manager.service.monitor;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmNotifyDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmRuleDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyActionDTO;
import com.xiaojukeji.kafka.manager.service.notify.KafkaNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 告警通知
 * @author zengqiao
 * @date 20/3/18
 */
@Component
public class AlarmNotifyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmNotifyService.class);

    @Value("${kafka-monitor.notify-kafka.cluster-id:}")
    private Long clusterId;

    @Value("${kafka-monitor.notify-kafka.topic-name:}")
    private String topicName;

    @Autowired
    private KafkaNotifier kafkaNotifier;

    public void send(AlarmRuleDTO alarmRuleDTO) {
        if (clusterId == null || StringUtils.isEmpty(topicName)) {
            LOGGER.error("application.yml monitor config illegal");
            return;
        }
        // todo 当前只有Kafka的方式，所以这里没有进行判断
        kafkaNotifier.produce(clusterId, topicName, JSON.toJSONString(convert2AlarmNotifyDTO(alarmRuleDTO)));
    }

    private AlarmNotifyDTO convert2AlarmNotifyDTO(AlarmRuleDTO alarmRuleDTO) {
        AlarmNotifyDTO alarmNotifyDTO = new AlarmNotifyDTO();
        alarmNotifyDTO.setAlarmRuleId(alarmRuleDTO.getId());
        AlarmStrategyActionDTO alarmStrategyActionDTO = alarmRuleDTO.getStrategyActionMap().get("KAFKA");
        if (alarmStrategyActionDTO == null) {
            alarmNotifyDTO.setActionTag("");
        }
        alarmNotifyDTO.setMessage(JSON.toJSONString(alarmRuleDTO));
        return alarmNotifyDTO;
    }
}