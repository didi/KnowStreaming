package com.xiaojukeji.know.streaming.km.core.service.connect.mm2.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.mm2.MetricsMirrorMakersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerTopicPartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.connect.mm2.MirrorMakerMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.connect.ConnectorMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.mm2.MirrorMakerMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionConnectJmxInfo;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.ConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseConnectMetricService;
import com.xiaojukeji.know.streaming.km.persistence.connect.ConnectJMXClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.mm2.MirrorMakerMetricESDAO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_CONNECT_MIRROR_MAKER;

/**
 * @author wyb
 * @date 2022/12/15
 */
@Service
public class MirrorMakerMetricServiceImpl extends BaseConnectMetricService implements MirrorMakerMetricService {
    protected static final ILog LOGGER = LogFactory.getLog(MirrorMakerMetricServiceImpl.class);

    public static final String MIRROR_MAKER_METHOD_DO_NOTHING                          = "doNothing";

    public static final String MIRROR_MAKER_METHOD_GET_HEALTH_SCORE                    = "getMetricHealthScore";

    public static final String MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_SUM = "getTopicPartitionMetricListSum";

    public static final String MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_AVG = "getTopicPartitionMetricListAvg";

    public static final String MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MIN = "getTopicPartitionMetricListMin";

    public static final String MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MAX = "getTopicPartitionMetricListMax";

    @Autowired
    private ConnectJMXClient connectJMXClient;

    @Autowired
    private MirrorMakerMetricESDAO mirrorMakerMetricESDAO;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private HealthStateService healthStateService;

    @Override
    protected List<String> listMetricPOFields() {
        return BeanUtil.listBeanFields(MirrorMakerMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler() {
        registerVCHandler(MIRROR_MAKER_METHOD_DO_NOTHING, this::doNothing);
        registerVCHandler(MIRROR_MAKER_METHOD_GET_HEALTH_SCORE, this::getMetricHealthScore);
        registerVCHandler(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_SUM, this::getTopicPartitionMetricListSum);
        registerVCHandler(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_AVG, this::getTopicPartitionMetricListAvg);
        registerVCHandler(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MAX, this::getTopicPartitionMetricListMax);
        registerVCHandler(MIRROR_MAKER_METHOD_GET_TOPIC_PARTITION_METRIC_LIST_MIN, this::getTopicPartitionMetricListMin);
    }

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return METRIC_CONNECT_MIRROR_MAKER;
    }

    @Override
    public Result<MirrorMakerMetrics> collectMirrorMakerMetricsFromKafka(Long connectClusterPhyId, String mirrorMakerName, List<MirrorMakerTopic> mirrorMakerTopicList, String metricName) {
        try {
            MirrorMakerMetricParam metricParam = new MirrorMakerMetricParam(connectClusterPhyId, mirrorMakerName, mirrorMakerTopicList, metricName);
            return (Result<MirrorMakerMetrics>) doVCHandler(connectClusterPhyId, metricName, metricParam);
        } catch (Exception e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listMirrorMakerClusterMetricsFromES(Long clusterPhyId, MetricsMirrorMakersDTO dto) {
        Long         startTime                      = dto.getStartTime();
        Long         endTime                        = dto.getEndTime();
        Integer      topN                           = dto.getTopNu();
        String       aggType                        = dto.getAggType();
        List<String> metricNameList                 = dto.getMetricsNames();

        List<Tuple<Long, String>> connectorList     = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dto.getConnectorNameList())){
            connectorList = dto.getConnectorNameList().stream()
                    .map(c -> new Tuple<>(c.getConnectClusterId(), c.getConnectorName()))
                    .collect(Collectors.toList());
        }

        Table<String/*metric*/, Tuple<Long, String>, List<MetricPointVO>> retTable;
        if(ValidateUtils.isEmptyList(connectorList)) {
            // 按照TopN的方式去获取
            List<Tuple<Long, String>> defaultConnectorList = this.listTopNMirrorMakerList(clusterPhyId, topN);

            retTable = mirrorMakerMetricESDAO.listMetricsByTopN(clusterPhyId, defaultConnectorList, metricNameList, aggType, topN, startTime, endTime);
        } else {
            // 制定集群ID去获取
            retTable = mirrorMakerMetricESDAO.listMetricsByConnectors(clusterPhyId, metricNameList, aggType, connectorList, startTime, endTime);
        }

        return Result.buildSuc(this.metricMap2VO(clusterPhyId, retTable.rowMap()));
    }

    @Override
    public Result<List<MirrorMakerMetrics>> getLatestMetricsFromES(Long clusterPhyId, List<Tuple<Long, String>> mirrorMakerList, List<String> metricNameList) {
        List<ConnectorMetricPO> connectorLatestMetricList = mirrorMakerMetricESDAO.getConnectorLatestMetric(clusterPhyId, mirrorMakerList, metricNameList);
        return Result.buildSuc(ConvertUtil.list2List(connectorLatestMetricList, MirrorMakerMetrics.class));
    }

    @Override
    public Result<MirrorMakerMetrics> getLatestMetricsFromES(Long connectClusterId, String connectorName, List<String> metricsNames) {
        ConnectorMetricPO connectorLatestMetric = mirrorMakerMetricESDAO.getConnectorLatestMetric(null, connectClusterId, connectorName, metricsNames);
        MirrorMakerMetrics mirrorMakerMetrics = ConvertUtil.obj2Obj(connectorLatestMetric, MirrorMakerMetrics.class);
        return Result.buildSuc(mirrorMakerMetrics);
    }

    private List<Tuple<Long, String>> listTopNMirrorMakerList(Long clusterPhyId, Integer topN) {
        List<ConnectorPO> poList = connectorService.listByKafkaClusterIdFromDB(clusterPhyId);

        if (CollectionUtils.isEmpty(poList)) {
            return new ArrayList<>();
        }

        return poList.subList(0, Math.min(topN, poList.size()))
                .stream()
                .map( c -> new Tuple<>(c.getId(), c.getConnectorName()) )
                .collect(Collectors.toList());
    }

    protected List<MetricMultiLinesVO> metricMap2VO(Long connectClusterId,
                                                    Map<String/*metric*/, Map<Tuple<Long, String>, List<MetricPointVO>>> map){
        List<MetricMultiLinesVO> multiLinesVOS = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            // 如果为空，则直接返回
            return multiLinesVOS;
        }

        for(String metric : map.keySet()){
            try {
                MetricMultiLinesVO multiLinesVO = new MetricMultiLinesVO();
                multiLinesVO.setMetricName(metric);

                List<MetricLineVO> metricLines = new ArrayList<>();

                Map<Tuple<Long, String>, List<MetricPointVO>> metricPointMap = map.get(metric);
                if(null == metricPointMap || metricPointMap.isEmpty()){continue;}

                for(Map.Entry<Tuple<Long, String>, List<MetricPointVO>> entry : metricPointMap.entrySet()){
                    MetricLineVO metricLineVO = new MetricLineVO();
                    metricLineVO.setName(entry.getKey().getV1() + "#" + entry.getKey().getV2());
                    metricLineVO.setMetricName(metric);
                    metricLineVO.setMetricPoints(entry.getValue());

                    metricLines.add(metricLineVO);
                }

                multiLinesVO.setMetricLines(metricLines);
                multiLinesVOS.add(multiLinesVO);
            } catch (Exception e){
                LOGGER.error("method=metricMap2VO||connectClusterId={}||msg=exception!", connectClusterId, e);
            }
        }

        return multiLinesVOS;
    }

    private Result<MirrorMakerMetrics> doNothing(VersionItemParam metricParam) {
        MirrorMakerMetricParam param                = (MirrorMakerMetricParam) metricParam;
        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();
        return Result.buildSuc(new MirrorMakerMetrics(connectClusterId,mirrorMakerName));
    }

    private Result<MirrorMakerMetrics> getMetricHealthScore(VersionItemParam metricParam) {
        MirrorMakerMetricParam param                = (MirrorMakerMetricParam) metricParam;
        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();

        MirrorMakerMetrics metrics = healthStateService.calMirrorMakerHealthMetrics(connectClusterId, mirrorMakerName);
        return Result.buildSuc(metrics);
    }

    private Result<MirrorMakerMetrics> getTopicPartitionMetricListSum(VersionItemParam metricParam) {
        MirrorMakerMetricParam param                = (MirrorMakerMetricParam) metricParam;
        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();
        List<MirrorMakerTopic> mirrorMakerTopicList = param.getMirrorMakerTopicList();
        String metric = param.getMetric();

        Result<List<MirrorMakerTopicPartitionMetrics>> ret = this.getTopicPartitionMetricList(connectClusterId, mirrorMakerName, mirrorMakerTopicList, metric);
        if (!ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }
        Float sum = ret.getData().stream().map(elem -> elem.getMetric(metric)).reduce(Float::sum).get();
        return Result.buildSuc(MirrorMakerMetrics.initWithMetric(connectClusterId, mirrorMakerName, metric, sum));
    }

    private Result<MirrorMakerMetrics> getTopicPartitionMetricListAvg(VersionItemParam metricParam) {
        MirrorMakerMetricParam param                = (MirrorMakerMetricParam) metricParam;
        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();
        List<MirrorMakerTopic> mirrorMakerTopicList = param.getMirrorMakerTopicList();
        String metric = param.getMetric();

        Result<List<MirrorMakerTopicPartitionMetrics>> ret = this.getTopicPartitionMetricList(connectClusterId, mirrorMakerName, mirrorMakerTopicList, metric);

        if (!ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        Float sum = ret.getData().stream().map(elem -> elem.getMetric(metric)).reduce(Float::sum).get();
        return Result.buildSuc(MirrorMakerMetrics.initWithMetric(connectClusterId, mirrorMakerName, metric, sum / ret.getData().size()));
    }

    private Result<MirrorMakerMetrics> getTopicPartitionMetricListMax(VersionItemParam metricParam) {
        MirrorMakerMetricParam param                = (MirrorMakerMetricParam) metricParam;
        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();
        List<MirrorMakerTopic> mirrorMakerTopicList = param.getMirrorMakerTopicList();
        String metric = param.getMetric();

        Result<List<MirrorMakerTopicPartitionMetrics>> ret = this.getTopicPartitionMetricList(connectClusterId, mirrorMakerName, mirrorMakerTopicList, metric);

        if (!ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        Float max = ret.getData().stream().max((a, b) -> a.getMetric(metric).compareTo(b.getMetric(metric))).get().getMetric(metric);
        return Result.buildSuc(MirrorMakerMetrics.initWithMetric(connectClusterId, mirrorMakerName, metric, max));
    }

    private Result<MirrorMakerMetrics> getTopicPartitionMetricListMin(VersionItemParam metricParam) {
        MirrorMakerMetricParam param                = (MirrorMakerMetricParam) metricParam;
        Long connectClusterId                       = param.getConnectClusterId();
        String mirrorMakerName                      = param.getMirrorMakerName();
        List<MirrorMakerTopic> mirrorMakerTopicList = param.getMirrorMakerTopicList();
        String metric = param.getMetric();

        Result<List<MirrorMakerTopicPartitionMetrics>> ret = this.getTopicPartitionMetricList(connectClusterId, mirrorMakerName, mirrorMakerTopicList, metric);

        if (!ret.hasData() || ret.getData().isEmpty()) {
            return Result.buildFailure(NOT_EXIST);
        }

        Float min = ret.getData().stream().max((a, b) -> b.getMetric(metric).compareTo(a.getMetric(metric))).get().getMetric(metric);
        return Result.buildSuc(MirrorMakerMetrics.initWithMetric(connectClusterId, mirrorMakerName, metric, min));
    }


    private Result<List<MirrorMakerTopicPartitionMetrics>> getTopicPartitionMetricList(Long connectClusterId, String mirrorMakerName, List<MirrorMakerTopic> mirrorMakerTopicList, String metric) {
        List<MirrorMakerTopicPartitionMetrics> topicPartitionMetricsList = new ArrayList<>();
        for (MirrorMakerTopic mirrorMakerTopic : mirrorMakerTopicList) {
            for (Map.Entry<Integer, String> entry : mirrorMakerTopic.getPartitionMap().entrySet()) {
                Result<MirrorMakerTopicPartitionMetrics> ret = this.getMirrorMakerTopicPartitionMetric(connectClusterId, mirrorMakerName, mirrorMakerTopic.getClusterAlias(), mirrorMakerTopic.getTopicName(), entry.getKey(), entry.getValue(), metric);
                if (!ret.hasData() || ret.getData().getMetric(metric) == null) {
                    continue;
                }
                topicPartitionMetricsList.add(ret.getData());
            }
        }
        return Result.buildSuc(topicPartitionMetricsList);
    }

    private Result<MirrorMakerTopicPartitionMetrics> getMirrorMakerTopicPartitionMetric(Long connectClusterId, String mirrorMakerName, String clusterAlias, String topicName, Integer partitionId, String workerId, String metric) {
        VersionConnectJmxInfo jmxInfo = getJMXInfo(connectClusterId, metric);
        if (null == jmxInfo) {
            return Result.buildFailure(VC_ITEM_JMX_NOT_EXIST);
        }

        String jmxObjectName = String.format(jmxInfo.getJmxObjectName(), clusterAlias, topicName, partitionId);

        JmxConnectorWrap jmxConnectorWrap = connectJMXClient.getClientWithCheck(connectClusterId, workerId);
        if (ValidateUtils.isNull(jmxConnectorWrap)) {
            return Result.buildFailure(VC_JMX_INIT_ERROR);
        }
        try {
            //2、获取jmx指标
            String value = jmxConnectorWrap.getAttribute(new ObjectName(jmxObjectName), jmxInfo.getJmxAttribute()).toString();
            MirrorMakerTopicPartitionMetrics metrics = MirrorMakerTopicPartitionMetrics.initWithMetric(connectClusterId, mirrorMakerName, clusterAlias, topicName, partitionId, workerId, metric, Float.valueOf(value));
            return Result.buildSuc(metrics);
        } catch (InstanceNotFoundException e) {
            // 忽略该错误，该错误出现的原因是该指标在JMX中不存在
            return Result.buildSuc(new MirrorMakerTopicPartitionMetrics(connectClusterId, mirrorMakerName, clusterAlias, topicName, partitionId, workerId));
        } catch (Exception e) {
            LOGGER.error("method=getMirrorMakerTopicPartitionMetric||connectClusterId={}||mirrorMakerName={}||clusterAlias={}||topicName={}||partitionId={}||workerId={}||metrics={}||jmx={}||msg={}",
                    connectClusterId, mirrorMakerName, clusterAlias, topicName, partitionId, workerId, metric, jmxObjectName, e.getClass().getName());
            return Result.buildFailure(VC_JMX_CONNECT_ERROR);
        }
    }
}
