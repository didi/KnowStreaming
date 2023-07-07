package com.xiaojukeji.know.streaming.km.common.bean.dto.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 删除offset
 * @author zengqiao
 * @date 19/4/8
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupOffsetDeleteDTO extends BaseDTO {
    @Min(value = 0, message = "clusterPhyId不允许为null或者小于0")
    @ApiModelProperty(value = "集群ID", example = "6")
    private Long clusterPhyId;

    @NotBlank(message = "groupName不允许为空")
    @ApiModelProperty(value = "消费组名称", example = "g-know-streaming")
    private String groupName;

    @ApiModelProperty(value = "Topic名称，按照Topic纬度进行删除时需要传", example = "know-streaming")
    protected String topicName;

    @ApiModelProperty(value = "分区ID，按照分区纬度进行删除时需要传")
    private Integer partitionId;

    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.group.DeleteGroupTypeEnum
     */
    @NotNull(message = "deleteType不允许为空")
    @ApiModelProperty(value = "删除类型", example = "0:group纬度，1：Topic纬度，2：Partition纬度")
    private Integer deleteType;
}