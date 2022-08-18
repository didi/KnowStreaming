package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author didi
 * 精确搜索
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchTerm extends SearchQuery{
    private String queryValue;

    private boolean equal = true;

    public Object getQueryValue(){
        //指标都是 double 类型
        return isMetric() ? Double.valueOf( queryValue ) : queryValue;
    }

    public SearchTerm(String queryName, String queryValue){
        super(queryName);
        this.queryValue = queryValue;
    }

    public SearchTerm(String queryName, String queryValue, boolean equal){
        super(queryName);
        this.queryValue = queryValue;
        this.equal      = equal;
    }

    @Override
    public boolean valid(){
        return validSuper() && null != queryValue;
    }
}
