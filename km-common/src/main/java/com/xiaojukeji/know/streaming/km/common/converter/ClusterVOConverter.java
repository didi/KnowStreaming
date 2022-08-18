package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyDashboardVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClusterVOConverter {
    private ClusterVOConverter() {
    }

    public static List<ClusterPhyDashboardVO> convert2ClusterPhyDashboardVOList(List<ClusterPhyDashboardVO> rowVOList,
                                                                                List<MetricMultiLinesVO> metricMultiLinesVOList,
                                                                                List<ClusterMetrics> clusterMetricsList) {
        Map<Long, ClusterPhyDashboardVO> voMap = rowVOList.stream().collect(Collectors.toMap(ClusterPhyDashboardVO::getId, Function.identity()));

        // <clusterId, List<metricLine>>
        Map<Long, List<MetricLineVO>> metricLineMap = new HashMap<>();
        if (metricMultiLinesVOList == null) {
            metricMultiLinesVOList = new ArrayList<>();
        }
        for (MetricMultiLinesVO multiLinesVO: metricMultiLinesVOList) {
            if (multiLinesVO.getMetricLines() == null) {
                continue;
            }
            for (MetricLineVO metricLineVO: multiLinesVO.getMetricLines()) {
                metricLineMap.putIfAbsent(ConvertUtil.string2Long(metricLineVO.getName()), new ArrayList<>());
                metricLineMap.get(ConvertUtil.string2Long(metricLineVO.getName())).add(metricLineVO);
            }
        }

        List<ClusterPhyDashboardVO> voList = new ArrayList<>();
        for (ClusterMetrics clusterMetrics: clusterMetricsList) {
            ClusterPhyDashboardVO vo = voMap.get(clusterMetrics.getClusterPhyId());
            if (vo == null) {
                vo = new ClusterPhyDashboardVO();
                vo.setId(clusterMetrics.getClusterPhyId());
            }

            vo.setLatestMetrics(clusterMetrics);
            vo.setMetricLines(metricLineMap.getOrDefault(vo.getId(), new ArrayList<>()));

            voList.add(vo);
        }

        return voList;
    }
}
