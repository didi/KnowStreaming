package com.xiaojukeji.know.streaming.km.common.bean.dto.reassign;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description="迁移Topic概要信息")
public class ReassignTopicOverviewDTO extends BaseDTO {
    @Min(value = 1, message = "clusterId不允许为null或者小于0")
    @ApiModelProperty(value = "集群ID", example = "2")
    private Long clusterId;

    @NotNull(message = "topicNameList不允许为空")
    @ApiModelProperty(value = "Topic名称列表", example = "[ks-0, ks-1, ks-2]")
    private List<String> topicNameList;
}
