package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRange extends SearchQuery{
    private float  min;
    private float  max;

    public SearchRange(String fieldName, float min, float max){
        super(fieldName);
        this.min    = min;
        this.max    = max;
    }

    @Override
    public boolean valid(){
        return validSuper() && (max > min);
    }
}
