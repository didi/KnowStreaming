package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.gateway.KafkaAclVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.gateway.KafkaUserVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/3
 */
public class GatewayModelConverter {
    public static List<KafkaAclVO> convert2KafkaAclVOList(List<KafkaAclDO> doList) {
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }

        List<KafkaAclVO> voList = new ArrayList<>();
        for (KafkaAclDO kafkaAclDO: doList) {
            KafkaAclVO vo = new KafkaAclVO();
            vo.setTopicName(kafkaAclDO.getTopicName());
            vo.setTimestamp(kafkaAclDO.getCreateTime().getTime());
            vo.setAccess(kafkaAclDO.getAccess());
            vo.setUsername(kafkaAclDO.getAppId());
            vo.setOperation(kafkaAclDO.getOperation());
            voList.add(vo);
        }
        return voList;
    }

    public static List<KafkaUserVO> convert2KafkaUserVOList(List<KafkaUserDO> doList) {
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }

        List<KafkaUserVO> voList = new ArrayList<>();
        for (KafkaUserDO kafkaUserDO: doList) {
            KafkaUserVO vo = new KafkaUserVO();
            vo.setUsername(kafkaUserDO.getAppId());
            vo.setPassword(kafkaUserDO.getPassword());
            vo.setTimestamp(kafkaUserDO.getCreateTime().getTime());
            vo.setUserType(kafkaUserDO.getUserType());
            vo.setOperation(kafkaUserDO.getOperation());
            voList.add(vo);
        }
        return voList;
    }
}