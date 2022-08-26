package com.xiaojukeji.know.streaming.km.common.bean.dto.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationSortDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
public class ClusterBrokersOverviewDTO extends PaginationSortDTO {
    @NotNull(message = "latestMetricNames不允许为空")
    @ApiModelProperty("需要指标点的信息")
    private List<String> latestMetricNames;
}
