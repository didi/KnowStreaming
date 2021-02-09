package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionAddGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionModifyGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.gateway.KafkaAclVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.gateway.KafkaUserVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.GatewayConfigVO;
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

    public static List<GatewayConfigVO> convert2GatewayConfigVOList(List<GatewayConfigDO> doList) {
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }

        List<GatewayConfigVO> voList = new ArrayList<>();
        for (GatewayConfigDO configDO: doList) {
            GatewayConfigVO vo = new GatewayConfigVO();
            vo.setId(configDO.getId());
            vo.setType(configDO.getType());
            vo.setName(configDO.getName());
            vo.setValue(configDO.getValue());
            vo.setVersion(configDO.getVersion());
            vo.setDescription(configDO.getDescription());
            vo.setCreateTime(configDO.getCreateTime());
            vo.setModifyTime(configDO.getModifyTime());
            voList.add(vo);
        }
        return voList;
    }

    public static GatewayConfigDO convert2GatewayConfigDO(OrderExtensionAddGatewayConfigDTO configDTO) {
        GatewayConfigDO configDO = new GatewayConfigDO();
        configDO.setType(configDTO.getType());
        configDO.setName(configDTO.getName());
        configDO.setValue(configDTO.getValue());
        configDO.setDescription(ValidateUtils.isNull(configDTO.getDescription())? "": configDTO.getDescription());
        return configDO;
    }

    public static GatewayConfigDO convert2GatewayConfigDO(OrderExtensionModifyGatewayConfigDTO configDTO) {
        GatewayConfigDO configDO = new GatewayConfigDO();
        configDO.setId(configDTO.getId());
        configDO.setType(configDTO.getType());
        configDO.setName(configDTO.getName());
        configDO.setValue(configDTO.getValue());
        configDO.setDescription(ValidateUtils.isNull(configDTO.getDescription())? "": configDTO.getDescription());
        return configDO;
    }
}