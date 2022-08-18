package com.xiaojukeji.know.streaming.km.common.bean.entity.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPage {
    private int pageNo = 1;
    private int pageSize = 10;

    public int from(){
        return pageNo * pageSize;
    }
}
