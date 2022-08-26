package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author didi
 * 精确搜索
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchShould extends SearchQuery{
    private List<String> queryValues;

    public List<? extends Object> getQueryValues(){
        return isMetric() ? queryValues.stream().map( f -> Double.valueOf(f)).collect(Collectors.toList())
                           : queryValues;
    }

    public SearchShould(String queryName){
        super(queryName);
    }

    public SearchShould(String queryName, List<String> queryValues){
        super(queryName);
        this.queryValues = queryValues;
    }

    public SearchShould(String queryName, List<String> queryValues, boolean field){
        super(queryName, field);
        this.queryValues = queryValues;
    }

    @Override
    public boolean valid(){
        return validSuper() && !CollectionUtils.isEmpty( queryValues );
    }
}
