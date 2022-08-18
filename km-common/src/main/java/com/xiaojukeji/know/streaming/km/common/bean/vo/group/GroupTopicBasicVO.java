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
public class GroupTopicBasicVO {
    @ApiModelProperty(value = "Topic名称", example = "know-streaming-test")
    protected String topicName;

    @ApiModelProperty(value = "Group名称", example = "group-know-streaming-test")
    protected String groupName;

    @ApiModelProperty(value = "Group状态", example = "Empty")
    protected String state;

    @ApiModelProperty(value = "member数", example = "12")
    protected Integer memberCount;
}
