package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnector;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.connector.KSConnectorStateInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect.ConnectClusterBasicCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterConnectorOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ConnectorBasicCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ConnectorBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerBasicVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Triple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_SOURCE_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME;
import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_TARGET_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME;

public class ConnectConverter {
    public static ConnectorBasicCombineExistVO convert2BasicVO(ConnectCluster connectCluster, ConnectorPO connectorPO) {
        ConnectorBasicCombineExistVO vo = new ConnectorBasicCombineExistVO();
        if (connectCluster == null || connectorPO == null) {
            vo.setExist(false);
            return vo;
        }

        vo.setExist(true);
        vo.setConnectClusterId(connectorPO.getConnectClusterId());
        vo.setConnectClusterName(connectCluster.getName());
        vo.setConnectorName(connectorPO.getConnectorName());

        return vo;
    }

    public static List<ConnectorBasicVO> convert2BasicVOList(
            List<ConnectCluster> clusterList,
            List<ConnectorPO> poList) {
        Map<Long, ConnectCluster> clusterMap = new HashMap<>();
        clusterList.stream().forEach(elem -> clusterMap.put(elem.getId(), elem));

        List<ConnectorBasicVO> voList = new ArrayList<>();
        poList.stream().filter(item -> clusterMap.containsKey(item.getConnectClusterId())).forEach(elem -> {
            ConnectorBasicVO vo = new ConnectorBasicVO();
            vo.setConnectClusterId(elem.getConnectClusterId());
            vo.setConnectClusterName(clusterMap.get(elem.getConnectClusterId()).getName());
            vo.setConnectorName(elem.getConnectorName());

            voList.add(vo);
        });

        return voList;
    }

    public static List<MirrorMakerBasicVO> convert2MirrorMakerBasicVOList(
            List<ConnectCluster> clusterList,
            List<ConnectorPO> poList) {
        Map<Long, ConnectCluster> clusterMap = new HashMap<>();
        clusterList.stream().forEach(elem -> clusterMap.put(elem.getId(), elem));

        List<MirrorMakerBasicVO> voList = new ArrayList<>();
        poList.stream().filter(item -> clusterMap.containsKey(item.getConnectClusterId())).forEach(elem -> {
            MirrorMakerBasicVO vo = new MirrorMakerBasicVO();
            vo.setConnectClusterId(elem.getConnectClusterId());
            vo.setConnectClusterName(clusterMap.get(elem.getConnectClusterId()).getName());
            vo.setConnectorName(elem.getConnectorName());

            voList.add(vo);
        });

        return voList;
    }

    public static ConnectClusterBasicCombineExistVO convert2ConnectClusterBasicCombineExistVO(ConnectCluster connectCluster) {
        if (connectCluster == null) {
            ConnectClusterBasicCombineExistVO combineExistVO = new ConnectClusterBasicCombineExistVO();
            combineExistVO.setExist(false);

            return combineExistVO;
        }

        ConnectClusterBasicCombineExistVO combineExistVO = ConvertUtil.obj2Obj(connectCluster, ConnectClusterBasicCombineExistVO.class);
        combineExistVO.setExist(true);
        return combineExistVO;
    }

    public static List<ClusterConnectorOverviewVO> convert2ClusterConnectorOverviewVOList(List<ConnectCluster> clusterList,
                                                                                          List<ConnectorPO> poList,
                                                                                          List<ConnectorMetrics> metricsList) {
        Map<Long, ConnectCluster> clusterMap = new HashMap<>();
        clusterList.stream().forEach(elem -> clusterMap.put(elem.getId(), elem));

        Map<String, ConnectorMetrics> metricMap = metricsList.stream().collect(Collectors.toMap(elem -> elem.getConnectClusterId() + "@" + elem.getConnectorName(), Function.identity()));

        List<ClusterConnectorOverviewVO> voList = new ArrayList<>();
        poList.stream().filter(item -> clusterMap.containsKey(item.getConnectClusterId())).forEach(elem -> {
            ClusterConnectorOverviewVO vo = new ClusterConnectorOverviewVO();
            vo.setConnectClusterId(elem.getConnectClusterId());
            vo.setConnectClusterName(clusterMap.get(elem.getConnectClusterId()).getName());
            vo.setConnectorName(elem.getConnectorName());
            vo.setConnectorClassName(elem.getConnectorClassName());
            vo.setConnectorType(elem.getConnectorType());
            vo.setState(elem.getState());
            vo.setTaskCount(elem.getTaskCount());
            vo.setTopicNameList(CommonUtils.string2StrList(elem.getTopics()));
            vo.setLatestMetrics(metricMap.getOrDefault(elem.getConnectClusterId() + "@" + elem.getConnectorName(), new ConnectorMetrics(elem.getConnectClusterId(), elem.getConnectorName())));
            voList.add(vo);
        });

        return voList;
    }

    public static List<ClusterConnectorOverviewVO> supplyData2ClusterConnectorOverviewVOList(List<ClusterConnectorOverviewVO> voList,
                                                                                             List<MetricMultiLinesVO> metricLineVOList) {
        Map<String, List<MetricLineVO>> metricLineMap = new HashMap<>();
        if (metricLineVOList != null) {
            for (MetricMultiLinesVO metricMultiLinesVO : metricLineVOList) {
                metricMultiLinesVO.getMetricLines()
                        .forEach(metricLineVO -> {
                            String key = metricLineVO.getName();
                            List<MetricLineVO> metricLineVOS = metricLineMap.getOrDefault(key, new ArrayList<>());
                            metricLineVOS.add(metricLineVO);
                            metricLineMap.put(key, metricLineVOS);
                        });
            }
        }

        voList.forEach(elem -> {
            elem.setMetricLines(metricLineMap.get(genConnectorKey(elem.getConnectClusterId(), elem.getConnectorName())));
        });

        return voList;
    }

    public static KSConnector convert2KSConnector(Long kafkaClusterPhyId, Long connectClusterId, KSConnectorInfo connectorInfo, KSConnectorStateInfo stateInfo, List<String> topicNameList) {
        KSConnector ksConnector = new KSConnector();
        ksConnector.setKafkaClusterPhyId(kafkaClusterPhyId);
        ksConnector.setConnectClusterId(connectClusterId);
        ksConnector.setConnectorName(connectorInfo.getName());
        ksConnector.setConnectorClassName(connectorInfo.getConfig().getOrDefault(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME, ""));
        ksConnector.setConnectorType(connectorInfo.getType().name());
        ksConnector.setTopics(topicNameList != null? CommonUtils.strList2String(topicNameList): "");
        ksConnector.setTaskCount(connectorInfo.getTasks() != null? connectorInfo.getTasks().size(): 0);
        ksConnector.setState(stateInfo != null? stateInfo.getConnector().getState(): "");

        return ksConnector;
    }

    public static List<KSConnector> convertAndSupplyMirrorMakerInfo(ConnectCluster connectCluster, List<Triple<KSConnectorInfo, List<String>, KSConnectorStateInfo>> connectorFullInfoList) {
        // <connectorName, targetBootstrapServers + "@" + sourceBootstrapServers>
        Map<String, String> sourceMap = new HashMap<>();

        // <targetBootstrapServers + "@" + sourceBootstrapServers, connectorName>
        Map<String, String> heartbeatMap = new HashMap<>();
        Map<String, String> checkpointMap = new HashMap<>();

        // 获取每个类型的connector的map信息
        connectorFullInfoList.forEach(connector -> {
            Map<String, String> mm2Map = null;
            if (KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE.equals(connector.v1().getConfig().get(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME))) {
                mm2Map = sourceMap;
            } else if (KafkaConnectConstant.MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE.equals(connector.v1().getConfig().get(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME))) {
                mm2Map = heartbeatMap;
            } else if (KafkaConnectConstant.MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE.equals(connector.v1().getConfig().get(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME))) {
                mm2Map = checkpointMap;
            }

            String targetBootstrapServers = connector.v1().getConfig().get(MIRROR_MAKER_TARGET_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME);
            String sourceBootstrapServers = connector.v1().getConfig().get(MIRROR_MAKER_SOURCE_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME);

            if (ValidateUtils.anyBlank(targetBootstrapServers, sourceBootstrapServers) || mm2Map == null) {
                return;
            }

            if (KafkaConnectConstant.MIRROR_MAKER_SOURCE_CONNECTOR_TYPE.equals(connector.v1().getConfig().get(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME))) {
                // source 类型的格式和 heartbeat & checkpoint 的不一样
                mm2Map.put(connector.v1().getName(), targetBootstrapServers + "@" + sourceBootstrapServers);
            } else {
                mm2Map.put(targetBootstrapServers + "@" + sourceBootstrapServers, connector.v1().getName());
            }
        });


        List<KSConnector> connectorList = new ArrayList<>();
        connectorFullInfoList.forEach(connector -> {
            // 转化并添加到list中
            KSConnector ksConnector = ConnectConverter.convert2KSConnector(
                    connectCluster.getKafkaClusterPhyId(),
                    connectCluster.getId(),
                    connector.v1(),
                    connector.v3(),
                    connector.v2()
            );
            connectorList.add(ksConnector);

            // 补充mm2信息
            String targetAndSource = sourceMap.get(ksConnector.getConnectorName());
            if (ValidateUtils.isBlank(targetAndSource)) {
                return;
            }

            ksConnector.setHeartbeatConnectorName(heartbeatMap.getOrDefault(targetAndSource, ""));
            ksConnector.setCheckpointConnectorName(checkpointMap.getOrDefault(targetAndSource, ""));
        });

        return connectorList;
    }

    private static String genConnectorKey(Long connectorId, String connectorName){
        return connectorId + "#" + connectorName;
    }

    private ConnectConverter() {
    }
}
