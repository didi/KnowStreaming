package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author didi
 * 模糊搜索
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFuzzy extends SearchQuery{
    private String queryValue;

    public SearchFuzzy(String queryName, String queryValue){
        super(queryName);
        this.queryValue = queryValue;
    }

    @Override
    public boolean valid(){
        return validSuper() && !StringUtils.isEmpty( queryValue );
    }
}
