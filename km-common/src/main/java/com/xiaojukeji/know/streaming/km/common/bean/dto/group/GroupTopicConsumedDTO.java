package com.xiaojukeji.know.streaming.km.common.bean.dto.group;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
public class GroupTopicConsumedDTO extends PaginationSortDTO {
    @ApiModelProperty("需要指标点的信息")
    private List<String> latestMetricNames;
}
