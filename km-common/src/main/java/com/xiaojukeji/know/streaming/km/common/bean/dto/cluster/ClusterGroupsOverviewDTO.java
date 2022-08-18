package com.xiaojukeji.know.streaming.km.common.bean.dto.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationMulFuzzySearchDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
public class ClusterGroupsOverviewDTO extends PaginationMulFuzzySearchDTO {
    @ApiModelProperty("查找该Topic")
    private String topicName;

    @ApiModelProperty("查找该Group")
    private String groupName;
}
