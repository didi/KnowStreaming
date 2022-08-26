package com.xiaojukeji.know.streaming.km.common.bean.dto.job;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "任务处理信息")
public class JobPaginationDTO extends PaginationBaseDTO {

    @ApiModelProperty("任务类型，-1：全部；0：Topic迁移；1：Topic扩缩副本；2：集群均衡")
    private Integer type = -1;

    @ApiModelProperty("执行任务对象")
    private String jobTarget;

    @ApiModelProperty("任务创建人")
    private String creator;

    @ApiModelProperty("运行状态，为空则代表全部状态")
    private List<Integer> status;
}
