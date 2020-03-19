package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.web.model.ClusterModel;
import com.xiaojukeji.kafka.manager.web.vo.cluster.ClusterBasicVO;
import com.xiaojukeji.kafka.manager.web.vo.cluster.ClusterDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.ControllerDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.ControllerData;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.web.vo.KafkaControllerVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * cluster相关转换
 * @author huangyiminghappy@163.com
 * @date 2019/3/15
 */
public class ClusterModelConverter {
    public static List<ClusterBasicVO> convert2ClusterBasicVOList(List<ClusterDO> clusterDOList) {
        if (clusterDOList == null || clusterDOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ClusterBasicVO> clusterBasicVOList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterDOList) {
            clusterBasicVOList.add(convert2ClusterBasicVO(clusterDO));
        }
        return clusterBasicVOList;
    }

    public static ClusterBasicVO convert2ClusterBasicVO(ClusterDO clusterDO) {
        ClusterBasicVO clusterBasicVO = new ClusterBasicVO();
        clusterBasicVO.setClusterId(clusterDO.getId());
        clusterBasicVO.setClusterName(clusterDO.getClusterName());
        clusterBasicVO.setBootstrapServers(clusterDO.getBootstrapServers());
        clusterBasicVO.setKafkaVersion(clusterDO.getKafkaVersion());
        clusterBasicVO.setGmtCreate(clusterDO.getGmtCreate().getTime());
        clusterBasicVO.setGmtModify(clusterDO.getGmtModify().getTime());
        clusterBasicVO.setBrokerNum(ClusterMetadataManager.getBrokerIdList(clusterDO.getId()).size());
        clusterBasicVO.setTopicNum(ClusterMetadataManager.getTopicNameList(clusterDO.getId()).size());
        return clusterBasicVO;
    }

    public static List<ClusterDetailVO> convert2ClusterDetailVOList(List<ClusterDO> clusterDOList,
                                                                    Map<Long, Long> clusterIdTagNumMap,
                                                                    Map<Long, Integer> consumerGroupNumMap) {
        List<ClusterDetailVO> clusterDetailVOList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterDOList) {
            ClusterDetailVO clusterDetailVO = new ClusterDetailVO();
            clusterDetailVO.setClusterId(clusterDO.getId());
            clusterDetailVO.setClusterName(clusterDO.getClusterName());
            clusterDetailVO.setZookeeper(clusterDO.getZookeeper());
            clusterDetailVO.setBootstrapServers(clusterDO.getBootstrapServers());
            clusterDetailVO.setKafkaVersion(clusterDO.getKafkaVersion());
            clusterDetailVO.setSecurityProtocol(clusterDO.getSecurityProtocol());
            clusterDetailVO.setSaslMechanism(clusterDO.getSaslMechanism());
            clusterDetailVO.setSaslJaasConfig(clusterDO.getSaslJaasConfig());
            clusterDetailVO.setAlarmFlag(clusterDO.getAlarmFlag());
            clusterDetailVO.setStatus(clusterDO.getStatus());
            clusterDetailVO.setGmtCreate(clusterDO.getGmtCreate().getTime());
            clusterDetailVO.setGmtModify(clusterDO.getGmtModify().getTime());

            if (DBStatusEnum.DELETED.getStatus().equals(clusterDO.getStatus())) {
                clusterDetailVO.setBrokerNum(-1);
                clusterDetailVO.setTopicNum(-1);
                clusterDetailVO.setControllerId(-1);
                clusterDetailVO.setRegionNum(-1);
                clusterDetailVOList.add(clusterDetailVO);
                continue;
            }
            clusterDetailVO.setRegionNum(clusterIdTagNumMap.getOrDefault(clusterDO.getId(), 0L).intValue());
            clusterDetailVO.setBrokerNum(ClusterMetadataManager.getBrokerIdList(clusterDO.getId()).size());
            clusterDetailVO.setTopicNum(ClusterMetadataManager.getTopicNameList(clusterDO.getId()).size());
            ControllerData controllerData = ClusterMetadataManager.getControllerData(clusterDO.getId());
            if (controllerData == null) {
                clusterDetailVO.setControllerId(-1);
            } else {
                clusterDetailVO.setControllerId(controllerData.getBrokerid());
            }
            clusterDetailVO.setConsumerGroupNum(consumerGroupNumMap.getOrDefault(clusterDO.getId(), 0));
            clusterDetailVOList.add(clusterDetailVO);
        }
        return clusterDetailVOList;
    }

    public static List<KafkaControllerVO> convert2KafkaControllerVOList(List<ControllerDO> controllerDOList) {
        if (controllerDOList == null) {
            return new ArrayList<>();
        }
        List<KafkaControllerVO> kafkaControllerVOList = new ArrayList<>();
        for (ControllerDO kafkaControllerDO: controllerDOList) {
            KafkaControllerVO kafkaControllerVO = new KafkaControllerVO();
            kafkaControllerVO.setBrokerId(kafkaControllerDO.getBrokerId());
            kafkaControllerVO.setHost(kafkaControllerDO.getHost());
            kafkaControllerVO.setControllerVersion(kafkaControllerDO.getVersion());
            kafkaControllerVO.setControllerTimestamp(kafkaControllerDO.getTimestamp());
            kafkaControllerVOList.add(kafkaControllerVO);
        }
        return kafkaControllerVOList;
    }

    public static ClusterDO convert2ClusterDO(ClusterModel reqObj) {
        ClusterDO clusterDO = new ClusterDO();
        clusterDO.setClusterName(reqObj.getClusterName());
        clusterDO.setZookeeper(reqObj.getZookeeper());
        clusterDO.setKafkaVersion(reqObj.getKafkaVersion());
        clusterDO.setBootstrapServers(reqObj.getBootstrapServers());
        clusterDO.setId(reqObj.getClusterId());
        clusterDO.setAlarmFlag(reqObj.getAlarmFlag() == null? 0: reqObj.getAlarmFlag());
        clusterDO.setSecurityProtocol(reqObj.getSecurityProtocol() == null? "": reqObj.getSecurityProtocol());
        clusterDO.setSaslMechanism(reqObj.getSaslMechanism() == null? "": reqObj.getSaslMechanism());
        clusterDO.setSaslJaasConfig(reqObj.getSaslJaasConfig() == null? "": reqObj.getSaslJaasConfig());
        return clusterDO;
    }
}
