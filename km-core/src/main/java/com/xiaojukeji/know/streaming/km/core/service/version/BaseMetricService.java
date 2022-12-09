package com.xiaojukeji.know.streaming.km.core.service.version;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchQuery;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author didi
 */
public abstract class BaseMetricService extends BaseKafkaVersionControlService {
    private static final ILog LOGGER  = LogFactory.getLog(BaseMetricService.class);

    private List<String> metricNames  = new ArrayList<>();
    private List<String> metricFields = new ArrayList<>();

    @PostConstruct
    public void init(){
        initMetricFieldAndNameList();
        initRegisterVCHandler();
    }

    protected void initMetricFieldAndNameList(){
        metricNames  = listVersionControlItems().stream().map(v -> v.getName()).collect(Collectors.toList());
        metricFields = listMetricPOFields();
    }

    protected abstract List<String> listMetricPOFields();

    protected abstract void initRegisterVCHandler();

    protected <T> List<MetricMultiLinesVO> metricMap2VO(Long clusterId, Map<String/*metric*/, Map<T, List<MetricPointVO>>> metricsMap ){
        List<MetricMultiLinesVO> lineVOList = new ArrayList<>();
        if (metricsMap == null || metricsMap.isEmpty()) {
            // 如果为空，则直接返回
            return lineVOList;
        }

        for(Map.Entry<String/*metric*/, Map<T, List<MetricPointVO>>> entry : metricsMap.entrySet()){
            try {
                MetricMultiLinesVO multiLinesVO = new MetricMultiLinesVO();
                multiLinesVO.setMetricName(entry.getKey());

                if(null == entry.getValue() || entry.getValue().isEmpty()){
                    continue;
                }

                List<MetricLineVO> metricLines = new ArrayList<>();
                entry.getValue().entrySet().forEach(resNameAndMetricsEntry -> {
                    MetricLineVO metricLineVO = new MetricLineVO();
                    metricLineVO.setName(resNameAndMetricsEntry.getKey().toString());
                    metricLineVO.setMetricName(entry.getKey());
                    metricLineVO.setMetricPoints(resNameAndMetricsEntry.getValue());

                    metricLines.add(metricLineVO);
                });

                multiLinesVO.setMetricLines(metricLines);

                lineVOList.add(multiLinesVO);
            } catch (Exception e){
                LOGGER.error("method=metricMap2VO||clusterId={}||msg=exception!", clusterId, e);
            }
        }

        return lineVOList;
    }

    /**
     * 检查 str 是不是一个 metricName
     * @param str
     */
    protected boolean isMetricName(String str){
        return metricNames.contains(str);
    }

    /**
     * 检查 str 是不是一个 fieldName
     * @param str
     */
    protected boolean isMetricField(String str){
        return metricFields.contains(str);
    }

    protected void setQueryMetricFlag(SearchQuery query){
        if(null == query){return;}

        String fieldName = query.getQueryName();

        query.setMetric(isMetricName(fieldName));
        query.setField(isMetricField(fieldName));
    }

    protected <T extends SearchQuery> void setQueryMetricFlag(List<T> matches){
        if(CollectionUtils.isEmpty(matches)){return;}

        for (SearchQuery match : matches){
            setQueryMetricFlag(match);
        }
    }
}
