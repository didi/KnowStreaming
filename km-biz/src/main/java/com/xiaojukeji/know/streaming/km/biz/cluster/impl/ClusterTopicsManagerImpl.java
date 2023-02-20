package com.xiaojukeji.know.streaming.km.biz.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.cluster.ClusterTopicsManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterTopicsOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res.ClusterPhyTopicsOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.TopicVOConverter;
import com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationMetricsUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.ha.HaActiveStandbyRelationService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class ClusterTopicsManagerImpl implements ClusterTopicsManager {
    private static final ILog log = LogFactory.getLog(ClusterTopicsManagerImpl.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private HaActiveStandbyRelationService haActiveStandbyRelationService;

    @Override
    public PaginationResult<ClusterPhyTopicsOverviewVO> getClusterPhyTopicsOverview(Long clusterPhyId, ClusterTopicsOverviewDTO dto) {
        // 获取集群所有的Topic信息
        List<Topic> topicList = topicService.listTopicsFromDB(clusterPhyId);

        // 获取集群所有Topic的指标
        Map<String, TopicMetrics> metricsMap = topicMetricService.getLatestMetricsFromCache(clusterPhyId);

        // 获取HA信息
        Set<String> haTopicNameSet = haActiveStandbyRelationService.listByClusterAndType(clusterPhyId, HaResTypeEnum.MIRROR_TOPIC).stream().map(elem -> elem.getResName()).collect(Collectors.toSet());

        // 转换成vo
        List<ClusterPhyTopicsOverviewVO> voList = TopicVOConverter.convert2ClusterPhyTopicsOverviewVOList(topicList, metricsMap, haTopicNameSet);

        // 请求分页信息
        PaginationResult<ClusterPhyTopicsOverviewVO> voPaginationResult = this.pagingTopicInLocal(voList, dto);
        if (voPaginationResult.failed()) {
            log.error("method=getClusterPhyTopicsOverview||clusterPhyId={}||result={}||errMsg=pagination in local failed", clusterPhyId, voPaginationResult);

            return PaginationResult.buildFailure(voPaginationResult, dto);
        }

        // 查询指标
        Result<List<MetricMultiLinesVO>> metricMultiLinesResult = topicMetricService.listTopicMetricsFromES(
                clusterPhyId,
                this.buildTopicOverviewMetricsDTO(voPaginationResult.getData().getBizData().stream().map(elem -> elem.getTopicName()).collect(Collectors.toList()), dto.getMetricLines())
        );

        if (metricMultiLinesResult.failed()) {
            // 查询ES失败或者ES无数据，则ES可能存在问题，此时降级返回Topic的基本信息数据
            log.error("method=getClusterPhyTopicsOverview||clusterPhyId={}||result={}||errMsg=get metrics from es failed", clusterPhyId, metricMultiLinesResult);
        }

        return PaginationResult.buildSuc(
                TopicVOConverter.supplyMetricLines(
                        voPaginationResult.getData().getBizData(),
                        metricMultiLinesResult.getData() == null? new ArrayList<>(): metricMultiLinesResult.getData()
                ),
                voPaginationResult
        );
    }

    /**************************************************** private method ****************************************************/

    private MetricsTopicDTO buildTopicOverviewMetricsDTO(List<String> topicNameList, MetricDTO metricDTO) {
        MetricsTopicDTO dto = ConvertUtil.obj2Obj(metricDTO, MetricsTopicDTO.class);
        dto.setTopics(topicNameList == null? new ArrayList<>(): topicNameList);
        return dto;
    }

    private PaginationResult<ClusterPhyTopicsOverviewVO> pagingTopicInLocal(List<ClusterPhyTopicsOverviewVO> voList, ClusterTopicsOverviewDTO dto) {
        List<ClusterPhyTopicsOverviewVO> metricsList = voList.stream().filter(elem -> {
                    if (dto.getShowInternalTopics() != null && dto.getShowInternalTopics()) {
                        // 仅展示系统Topic
                        return KafkaConstant.KAFKA_INTERNAL_TOPICS.contains(elem.getTopicName());
                    } else {
                        // 仅展示用户Topic
                        return !KafkaConstant.KAFKA_INTERNAL_TOPICS.contains(elem.getTopicName());
                    }
        }).collect(Collectors.toList());

        // 名称搜索
        metricsList = PaginationUtil.pageByFuzzyFilter(metricsList, dto.getSearchKeywords(), Arrays.asList("topicName"));

        if (!ValidateUtils.isBlank(dto.getSortField()) && !"createTime".equals(dto.getSortField())) {
            // 指标排序
            PaginationMetricsUtil.sortMetrics(metricsList, "latestMetrics", dto.getSortField(), "topicName", dto.getSortType());
        } else {
            // 信息排序
            PaginationUtil.pageBySort(metricsList, dto.getSortField(), dto.getSortType(), "topicName", dto.getSortType());
        }

        return PaginationUtil.pageBySubData(metricsList, dto);
    }
}
