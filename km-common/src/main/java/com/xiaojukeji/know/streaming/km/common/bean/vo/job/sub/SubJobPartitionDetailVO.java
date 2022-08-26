package com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "subJob详情")
public class SubJobPartitionDetailVO extends BaseTimeVO {

    @ApiModelProperty(value = "partitionId")
    private Integer partitionId;

    @ApiModelProperty(value = "源BrokerID")
    private List<Integer> sourceBrokerIds;

    @ApiModelProperty(value = "目标BrokerID")
    private List<Integer> desBrokerIds;

    @ApiModelProperty(value = "需迁移MessageSize")
    private Double totalSize;

    @ApiModelProperty(value = "已完成MessageSize")
    private Double movedSize;

    @ApiModelProperty(value = "任务状态")
    private Integer status;

    @ApiModelProperty(value = "预计剩余时长")
    private Long  remainTime;
}
