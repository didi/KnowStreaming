package com.xiaojukeji.know.streaming.km.biz.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.cluster.MultiClusterPhyManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsClusterPhyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhysHealthState;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhysState;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.MultiClusterDashboardDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyDashboardVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.ClusterVOConverter;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthStateEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationMetricsUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ClusterMetricVersionItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MultiClusterPhyManagerImpl implements MultiClusterPhyManager {
    private static final ILog log = LogFactory.getLog(MultiClusterPhyManagerImpl.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ClusterMetricService clusterMetricService;

    @Autowired
    private KafkaControllerService kafkaControllerService;

    @Override
    public ClusterPhysState getClusterPhysState() {
        List<ClusterPhy> clusterPhyList = clusterPhyService.listAllClusters();

        Map<Long, KafkaController> controllerMap = kafkaControllerService.getKafkaControllersFromDB(
                clusterPhyList.stream().map(elem -> elem.getId()).collect(Collectors.toList()),
                false
        );

        // TODO 后续产品上，看是否需要增加一个未知的状态，否则新接入的集群，因为新接入的集群，数据存在延迟
        ClusterPhysState physState = new ClusterPhysState(0, 0, clusterPhyList.size());
        for (ClusterPhy clusterPhy: clusterPhyList) {
            KafkaController kafkaController = controllerMap.get(clusterPhy.getId());

            if (kafkaController != null && !kafkaController.alive()) {
                // 存在明确的信息表示controller挂了
                physState.setDownCount(physState.getDownCount() + 1);
            } else if ((System.currentTimeMillis() - clusterPhy.getCreateTime().getTime() >= 5 * 60 * 1000) && kafkaController == null) {
                // 集群接入时间是在近5分钟内，同时kafkaController信息不存在，则设置为down
                physState.setDownCount(physState.getDownCount() + 1);
            } else {
                // 其他情况都设置为alive
                physState.setLiveCount(physState.getLiveCount() + 1);
            }
        }

        return physState;
    }

    @Override
    public ClusterPhysHealthState getClusterPhysHealthState() {
        List<ClusterPhy> clusterPhyList = clusterPhyService.listAllClusters();

        ClusterPhysHealthState physState = new ClusterPhysHealthState(clusterPhyList.size());
        for (ClusterPhy clusterPhy: clusterPhyList) {
            ClusterMetrics metrics = clusterMetricService.getLatestMetricsFromCache(clusterPhy.getId());
            Float state = metrics.getMetric(ClusterMetricVersionItems.CLUSTER_METRIC_HEALTH_STATE);
            if (state == null) {
                physState.setUnknownCount(physState.getUnknownCount() + 1);
            } else if (state.intValue() == HealthStateEnum.GOOD.getDimension()) {
                physState.setGoodCount(physState.getGoodCount() + 1);
            } else if (state.intValue() == HealthStateEnum.MEDIUM.getDimension()) {
                physState.setMediumCount(physState.getMediumCount() + 1);
            } else if (state.intValue() == HealthStateEnum.POOR.getDimension()) {
                physState.setPoorCount(physState.getPoorCount() + 1);
            } else if (state.intValue() == HealthStateEnum.DEAD.getDimension()) {
                physState.setDeadCount(physState.getDeadCount() + 1);
            } else {
                physState.setUnknownCount(physState.getUnknownCount() + 1);
            }
        }

        return physState;
    }

    @Override
    public PaginationResult<ClusterPhyDashboardVO> getClusterPhysDashboard(MultiClusterDashboardDTO dto) {
        // 获取集群
        List<ClusterPhy> clusterPhyList = clusterPhyService.listAllClusters();

        // 转为vo格式，方便后续进行分页筛选等
        List<ClusterPhyDashboardVO> voList = ConvertUtil.list2List(clusterPhyList, ClusterPhyDashboardVO.class);

        // TODO 后续产品上，看是否需要增加一个未知的状态，否则新接入的集群，因为新接入的集群，数据存在延迟
        // 获取集群controller信息并补充到vo中,
        Map<Long, KafkaController> controllerMap = kafkaControllerService.getKafkaControllersFromDB(clusterPhyList.stream().map(elem -> elem.getId()).collect(Collectors.toList()), false);
        for (ClusterPhyDashboardVO vo: voList) {
            KafkaController kafkaController = controllerMap.get(vo.getId());

            if (kafkaController != null && !kafkaController.alive()) {
                // 存在明确的信息表示controller挂了
                vo.setAlive(Constant.DOWN);
            } else if ((System.currentTimeMillis() - vo.getCreateTime().getTime() >= 5 * 60L * 1000L) && kafkaController == null) {
                // 集群接入时间是在近5分钟内，同时kafkaController信息不存在，则设置为down
                vo.setAlive(Constant.DOWN);
            } else {
                // 其他情况都设置为alive
                vo.setAlive(Constant.ALIVE);
            }
        }

        // 本地分页过滤
        voList = this.getAndPagingDataInLocal(voList, dto);

        // ES分页过滤
        PaginationResult<ClusterMetrics> latestMetricsResult = this.getAndPagingClusterWithLatestMetricsFromCache(voList, dto);
        if (latestMetricsResult.failed()) {
            log.error("method=getClusterPhysDashboard||pagingData={}||result={}||errMsg=search es data failed.", dto, latestMetricsResult);
            return PaginationResult.buildFailure(latestMetricsResult, dto);
        }

        // 获取历史指标
        Result<List<MetricMultiLinesVO>> linesMetricResult = clusterMetricService.listClusterMetricsFromES(
                this.buildMetricsClusterPhyDTO(
                        latestMetricsResult.getData().getBizData().stream().map(elem -> elem.getClusterPhyId()).collect(Collectors.toList()),
                        dto.getMetricLines()
        ));

        // 组装最终数据
        return PaginationResult.buildSuc(
                ClusterVOConverter.convert2ClusterPhyDashboardVOList(voList, linesMetricResult.getData(), latestMetricsResult.getData().getBizData()),
                latestMetricsResult
        );
    }


    /**************************************************** private method ****************************************************/


    private List<ClusterPhyDashboardVO> getAndPagingDataInLocal(List<ClusterPhyDashboardVO> voList, MultiClusterDashboardDTO dto) {
        // 时间排序
        if ("createTime".equals(dto.getSortField())) {
            voList = PaginationUtil.pageBySort(voList, "createTime", dto.getSortType(), "name", dto.getSortType());
        }

        // 名称搜索
        if (!ValidateUtils.isBlank(dto.getSearchKeywords())) {
            voList = PaginationUtil.pageByFuzzyFilter(voList, dto.getSearchKeywords(), Arrays.asList("name"));
        }

        // 精确搜索
        return PaginationUtil.pageByPreciseFilter(voList, dto.getPreciseFilterDTOList());
    }

    private PaginationResult<ClusterMetrics> getAndPagingClusterWithLatestMetricsFromCache(List<ClusterPhyDashboardVO> voList, MultiClusterDashboardDTO dto) {
        // 获取所有的metrics
        List<ClusterMetrics> metricsList = new ArrayList<>();
        for (ClusterPhyDashboardVO vo: voList) {
            ClusterMetrics clusterMetrics = clusterMetricService.getLatestMetricsFromCache(vo.getId());
            clusterMetrics.getMetrics().putIfAbsent(ClusterMetricVersionItems.CLUSTER_METRIC_HEALTH_STATE, (float) HealthStateEnum.UNKNOWN.getDimension());

            metricsList.add(clusterMetrics);
        }

        // 范围搜索
        metricsList = (List<ClusterMetrics>) PaginationMetricsUtil.rangeFilterMetrics(metricsList, dto.getRangeFilterDTOList());

        // 精确搜索
        metricsList = (List<ClusterMetrics>) PaginationMetricsUtil.preciseFilterMetrics(metricsList, dto.getPreciseFilterDTOList());

        // 排序
        PaginationMetricsUtil.sortMetrics(metricsList, dto.getSortField(), "clusterPhyId", dto.getSortType());

        // 分页
        return PaginationUtil.pageBySubData(metricsList, dto);
    }

    private MetricsClusterPhyDTO buildMetricsClusterPhyDTO(List<Long> clusterIdList, MetricDTO metricDTO) {
        MetricsClusterPhyDTO dto = ConvertUtil.obj2Obj(metricDTO, MetricsClusterPhyDTO.class);
        dto.setClusterPhyIds(clusterIdList);
        return dto;
    }
}
