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
 * @author wyb
 * @date 2022/11/9
 */
public abstract class BaseConnectorMetricService extends BaseConnectorVersionControlService{
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
