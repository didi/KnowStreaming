package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.TIME_STAMP;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchSort extends SearchQuery{

    public static final SearchSort DEFAULT = new SearchSort(TIME_STAMP, true);

    private boolean desc;

    public SearchSort(String fieldName, boolean desc){
        super(fieldName);
        this.desc = desc;
    }

    public SearchSort(String fieldName, boolean desc, boolean metric){
        super(fieldName);
        this.desc   = desc;
        this.metric = metric;
    }

    @Override
    public boolean valid(){
        return validSuper();
    }
}
