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
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyBaseVO;
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
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ClusterMetricVersionItems;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.common.BalanceMetricConstant;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.ClusterBalanceItemState;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MultiClusterPhyManagerImpl implements MultiClusterPhyManager {
    private static final ILog log = LogFactory.getLog(MultiClusterPhyManagerImpl.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ClusterMetricService clusterMetricService;

    @Autowired
    private ClusterBalanceService clusterBalanceService;

    @Override
    public ClusterPhysState getClusterPhysState() {
        List<ClusterPhy> clusterPhyList = clusterPhyService.listAllClusters();
        ClusterPhysState physState = new ClusterPhysState(0, 0, 0, clusterPhyList.size());

        for (ClusterPhy clusterPhy : clusterPhyList) {
            ClusterMetrics metrics = clusterMetricService.getLatestMetricsFromCache(clusterPhy.getId());
            Float state = metrics.getMetric(ClusterMetricVersionItems.CLUSTER_METRIC_HEALTH_STATE);
            if (state == null) {
                physState.setUnknownCount(physState.getUnknownCount() + 1);
            } else if (state.intValue() == HealthStateEnum.DEAD.getDimension()) {
                physState.setDownCount(physState.getDownCount() + 1);
            } else {
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

    @Override
    public Result<List<ClusterPhyBaseVO>> getClusterPhysBasic() {
        // 获取集群
        List<ClusterPhy> clusterPhyList = clusterPhyService.listAllClusters();

        // 转为vo格式，方便后续进行分页筛选等
        return Result.buildSuc(ConvertUtil.list2List(clusterPhyList, ClusterPhyBaseVO.class));
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

            Result<ClusterMetrics> balanceMetricsResult = this.getClusterLoadReBalanceInfo(vo.getId());
            if (balanceMetricsResult.hasData()) {
                clusterMetrics.putMetric(balanceMetricsResult.getData().getMetrics());
            }

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

    private Result<ClusterMetrics> getClusterLoadReBalanceInfo(Long clusterPhyId) {
        Result<ClusterBalanceItemState> stateResult = clusterBalanceService.getItemStateFromCacheFirst(clusterPhyId);
        if (stateResult.failed()) {
            return Result.buildFromIgnoreData(stateResult);
        }

        ClusterBalanceItemState state = stateResult.getData();

        ClusterMetrics metric = ClusterMetrics.initWithMetrics(clusterPhyId, BalanceMetricConstant.CLUSTER_METRIC_LOAD_RE_BALANCE_ENABLE, state.getEnable()? Constant.YES: Constant.NO);
        metric.putMetric(BalanceMetricConstant.CLUSTER_METRIC_LOAD_RE_BALANCE_CPU, state.getResItemState(Resource.CPU).floatValue());
        metric.putMetric(BalanceMetricConstant.CLUSTER_METRIC_LOAD_RE_BALANCE_NW_IN, state.getResItemState(Resource.NW_IN).floatValue());
        metric.putMetric(BalanceMetricConstant.CLUSTER_METRIC_LOAD_RE_BALANCE_NW_OUT, state.getResItemState(Resource.NW_OUT).floatValue());
        metric.putMetric(BalanceMetricConstant.CLUSTER_METRIC_LOAD_RE_BALANCE_DISK, state.getResItemState(Resource.DISK).floatValue());

        return Result.buildSuc(metric);
    }
}
