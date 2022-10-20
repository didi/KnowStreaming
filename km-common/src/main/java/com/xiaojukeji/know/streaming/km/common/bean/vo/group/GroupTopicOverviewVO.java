package com.xiaojukeji.know.streaming.km.common.bean.vo.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "GroupTopic信息")
public class GroupTopicOverviewVO extends GroupTopicBasicVO {
    @ApiModelProperty(value = "最大Lag", example = "12345678")
    private Long maxLag;
}
