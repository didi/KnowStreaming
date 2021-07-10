package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ClusterBrokerStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ControllerPreferredCandidate;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalCluster;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.LogicalClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.ClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.ClusterMetrics;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.BrokerOverviewVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.TopicThrottleVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.LogicClusterVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.NormalClusterMetricsVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.TopicMetadataVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.KafkaControllerVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterBrokerStatusVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ControllerPreferredCandidateVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.RdClusterMetricsVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ControllerDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;

import java.util.*;

/**
 * cluster相关转换
 * @author huangyiminghappy@163.com
 * @date 2019/3/15
 */
public class ClusterModelConverter {
    public static List<LogicClusterVO> convert2LogicClusterVOList(List<LogicalCluster> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return new ArrayList<>();
        }
        List<LogicClusterVO> voList = new ArrayList<>();
        for (LogicalCluster elem: dtoList) {
            voList.add(convert2LogicClusterVO(elem));
        }
        return voList;
    }

    public static LogicClusterVO convert2LogicClusterVO(LogicalCluster logicalCluster) {
        LogicClusterVO vo = new LogicClusterVO();
        CopyUtils.copyProperties(vo, logicalCluster);
        vo.setClusterId(logicalCluster.getLogicalClusterId());
        vo.setClusterName(logicalCluster.getLogicalClusterName());
        vo.setClusterIdentification(logicalCluster.getLogicalClusterIdentification());
        return vo;
    }

    public static List<KafkaControllerVO> convert2KafkaControllerVOList(List<ControllerDO> doList) {
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }
        List<KafkaControllerVO> voList = new ArrayList<>();
        for (ControllerDO elem: doList) {
            KafkaControllerVO vo = new KafkaControllerVO();
            vo.setBrokerId(elem.getBrokerId());
            vo.setHost(elem.getHost());
            vo.setVersion(elem.getVersion());
            vo.setTimestamp(elem.getTimestamp());
            voList.add(vo);
        }
        return voList;
    }

    public static ClusterDO convert2ClusterDO(ClusterDTO reqObj) {
        ClusterDO clusterDO = new ClusterDO();
        CopyUtils.copyProperties(clusterDO, reqObj);
        clusterDO.setId(reqObj.getClusterId());
        clusterDO.setSecurityProperties(ValidateUtils.isNull(reqObj.getSecurityProperties())? "": reqObj.getSecurityProperties());
        clusterDO.setJmxProperties(ValidateUtils.isNull(reqObj.getJmxProperties())? "": reqObj.getJmxProperties());
        return clusterDO;
    }

    public static ClusterDetailVO convert2ClusterDetailVO(ClusterDetailDTO dto) {
        if (ValidateUtils.isNull(dto)) {
            return null;
        }
        ClusterDetailVO vo = new ClusterDetailVO();
        CopyUtils.copyProperties(vo, dto);
        if (ValidateUtils.isNull(vo.getRegionNum())) {
            vo.setRegionNum(0);
        }
        return vo;
    }

    public static List<ClusterDetailVO> convert2ClusterDetailVOList(List<ClusterDetailDTO> dtoList) {
        if (ValidateUtils.isNull(dtoList)) {
            return new ArrayList<>();
        }
        List<ClusterDetailVO> voList = new ArrayList<>();
        for (ClusterDetailDTO dto: dtoList) {
            voList.add(convert2ClusterDetailVO(dto));
        }
        return voList;
    }

    public static List<NormalClusterMetricsVO> convert2NormalClusterMetricsVOList(
            List<LogicalClusterMetrics> dataList) {
        if (ValidateUtils.isEmptyList(dataList)) {
            return new ArrayList<>();
        }
        List<NormalClusterMetricsVO> voList = new ArrayList<>();
        for (LogicalClusterMetrics data: dataList) {
            NormalClusterMetricsVO vo = new NormalClusterMetricsVO();
            CopyUtils.copyProperties(vo, data);
            voList.add(vo);
        }
        return voList;
    }

    public static List<RdClusterMetricsVO> convert2RdClusterMetricsVOList(List<ClusterMetricsDO> dataList) {
        if (ValidateUtils.isNull(dataList)) {
            return new ArrayList<>();
        }
        List<ClusterMetrics> metricsList = MetricsConvertUtils.convert2ClusterMetricsList(dataList);
        List<RdClusterMetricsVO> voList = new ArrayList<>();
        for (ClusterMetrics metrics: metricsList) {
            RdClusterMetricsVO vo = new RdClusterMetricsVO();
            vo.setClusterId(metrics.getClusterId());
            vo.setTopicNum(metrics.getSpecifiedMetrics(JmxConstant.TOPIC_NUM));
            vo.setPartitionNum(metrics.getSpecifiedMetrics(JmxConstant.PARTITION_NUM));
            vo.setBrokerNum(metrics.getSpecifiedMetrics(JmxConstant.BROKER_NUM));
            vo.setBytesInPerSec(metrics.getSpecifiedMetrics("BytesInPerSecOneMinuteRate"));
            vo.setBytesOutPerSec(metrics.getSpecifiedMetrics("BytesOutPerSecOneMinuteRate"));
            vo.setBytesRejectedPerSec(metrics.getSpecifiedMetrics("BytesRejectedPerSecOneMinuteRate"));
            vo.setMessagesInPerSec(metrics.getSpecifiedMetrics("MessagesInPerSecOneMinuteRate"));
            vo.setGmtCreate(metrics.getSpecifiedMetrics(JmxConstant.CREATE_TIME, Long.class));
            voList.add(vo);
        }
        return voList;
    }

    public static List<BrokerOverviewVO> convert2BrokerOverviewList(List<BrokerOverviewDTO> brokerOverviewDTOList,
                                                                    List<RegionDO> regionDOList) {
        if (ValidateUtils.isEmptyList(brokerOverviewDTOList)) {
            return new ArrayList<>();
        }

        Map<Integer, String> brokerIdRegionNameMap = convert2BrokerIdRegionNameMap(regionDOList);
        List<BrokerOverviewVO> brokerOverviewVOList = new ArrayList<>();
        for (BrokerOverviewDTO brokerOverviewDTO: brokerOverviewDTOList) {
            BrokerOverviewVO brokerOverviewVO = new BrokerOverviewVO();
            brokerOverviewVO.setBrokerId(brokerOverviewDTO.getBrokerId());
            brokerOverviewVO.setHost(brokerOverviewDTO.getHost());
            brokerOverviewVO.setPort(brokerOverviewDTO.getPort());
            brokerOverviewVO.setJmxPort(brokerOverviewDTO.getJmxPort());
            brokerOverviewVO.setStartTime(brokerOverviewDTO.getStartTime());
            brokerOverviewVO.setByteIn(brokerOverviewDTO.getByteIn());
            brokerOverviewVO.setByteOut(brokerOverviewDTO.getByteOut());
            brokerOverviewVO.setPartitionCount(brokerOverviewDTO.getPartitionCount());
            brokerOverviewVO.setUnderReplicated(brokerOverviewDTO.getUnderReplicated());
            brokerOverviewVO.setUnderReplicatedPartitions(brokerOverviewDTO.getUnderReplicatedPartitions());
            brokerOverviewVO.setStatus(brokerOverviewDTO.getStatus());
            brokerOverviewVO.setKafkaVersion(brokerOverviewDTO.getKafkaVersion());
            brokerOverviewVO.setLeaderCount(brokerOverviewDTO.getLeaderCount());
            brokerOverviewVO.setRegionName(brokerIdRegionNameMap.getOrDefault(brokerOverviewDTO.getBrokerId(), ""));
            brokerOverviewVO.setPeakFlowStatus(brokerOverviewDTO.getPeakFlowStatus());
            brokerOverviewVOList.add(brokerOverviewVO);
        }
        return brokerOverviewVOList;
    }

    private static Map<Integer, String> convert2BrokerIdRegionNameMap(List<RegionDO> regionDOList) {
        Map<Integer, String> brokerIdRegionNameMap = new HashMap<>();
        if (regionDOList == null) {
            regionDOList = new ArrayList<>();
        }
        for (RegionDO regionDO: regionDOList) {
            List<Integer> brokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (brokerIdList == null || brokerIdList.isEmpty()) {
                continue;
            }
            for (Integer brokerId: brokerIdList) {
                brokerIdRegionNameMap.put(brokerId, regionDO.getName());
            }
        }
        return brokerIdRegionNameMap;
    }

    public static List<TopicMetadataVO> convert2TopicMetadataVOList(List<TopicMetadata> metadataList) {
        if (ValidateUtils.isEmptyList(metadataList)) {
            return new ArrayList<>();
        }

        List<TopicMetadataVO> voList = new ArrayList<>();
        for (TopicMetadata topicMetadata: metadataList) {
            TopicMetadataVO vo = new TopicMetadataVO();
            vo.setTopicName(topicMetadata.getTopic());
            voList.add(vo);
        }
        return voList;
    }

    public static List<TopicMetadataVO> convert2TopicMetadataVOList(Long clusterId) {
        if (ValidateUtils.isNull(clusterId)) {
            return new ArrayList<>();
        }

        List<TopicMetadataVO> voList = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }
            try {
                TopicMetadataVO vo = new TopicMetadataVO();
                vo.setTopicName(topicMetadata.getTopic());
                vo.setPartitionIdList(new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet()));
                voList.add(vo);
            } catch (Exception e) {
            }
        }
        return voList;
    }

    public static List<TopicThrottleVO> convert2TopicThrottleVOList(List<TopicThrottledMetrics> metricsList) {
        if (ValidateUtils.isNull(metricsList)) {
            return new ArrayList<>();
        }
        List<TopicThrottleVO> voList = new ArrayList<>();
        for (TopicThrottledMetrics metrics: metricsList) {
            TopicThrottleVO vo = new TopicThrottleVO();
            vo.setTopicName(metrics.getTopicName());
            vo.setAppId(metrics.getAppId());
            vo.setBrokerIdList(new ArrayList<>(metrics.getBrokerIdSet()));
            vo.setThrottleClientType(metrics.getClientType().getName());
            voList.add(vo);
        }
        return voList;
    }

    public static ClusterBrokerStatusVO convert2ClusterBrokerStatusVO(ClusterBrokerStatus clusterBrokerStatus) {
        if (ValidateUtils.isNull(clusterBrokerStatus)) {
            return null;
        }
        ClusterBrokerStatusVO vo = new ClusterBrokerStatusVO();
        vo.setBrokerBytesInStatusList(clusterBrokerStatus.getBrokerBytesInStatusList());
        vo.setBrokerReplicaStatusList(clusterBrokerStatus.getBrokerReplicaStatusList());
        return vo;
    }

    public static List<ControllerPreferredCandidateVO> convert2ControllerPreferredCandidateVOList(List<ControllerPreferredCandidate> candidateList) {
        if (ValidateUtils.isEmptyList(candidateList)) {
            return new ArrayList<>();
        }

        List<ControllerPreferredCandidateVO> voList = new ArrayList<>();
        for (ControllerPreferredCandidate candidate: candidateList) {
            ControllerPreferredCandidateVO vo = new ControllerPreferredCandidateVO();
            vo.setBrokerId(candidate.getBrokerId());
            vo.setHost(candidate.getHost());
            vo.setStatus(candidate.getStatus());
            vo.setStartTime(candidate.getStartTime());
            voList.add(vo);
        }
        return voList;
    }
}
