package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.METRICS_DOT;

/**
 * @author didi
 * 模糊搜索
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class SearchQuery {
    protected String    queryName;
    protected boolean     metric  = false;
    protected boolean     field   = false;

    public SearchQuery(String queryName){
        this.queryName = queryName;
    }

    public SearchQuery(String queryName, boolean field){
        this.queryName = queryName;
        this.field = field;
    }

    /**
     * 如果 fieldName 是一个指标，那么在es中存储的时候，
     * 是存储在 "metrics" 属性节点下，因此查询es时需要在前面加上 "metrics."
     * @return
     */
    public String getRealMetricName(){
        return metric ? METRICS_DOT + queryName : queryName;
    }

    protected boolean validSuper(){
        return !StringUtils.isEmpty( queryName ) && (field || metric);
    }

    abstract protected boolean valid();
}
