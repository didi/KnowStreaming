package com.xiaojukeji.know.streaming.km.core.service.version;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyb
 * @date 2022/11/9
 */
public abstract class BaseConnectMetricService extends BaseConnectVersionControlService {
    private List<String> metricNames  = new ArrayList<>();

    @PostConstruct
    public void init(){
        initMetricFieldAndNameList();
        initRegisterVCHandler();
    }

    protected void initMetricFieldAndNameList(){
        metricNames  = listVersionControlItems().stream().map(v -> v.getName()).collect(Collectors.toList());
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
}
