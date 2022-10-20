package com.xiaojukeji.know.streaming.km.common.bean.vo.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wyb
 * @date 2022/10/9
 */
@Data
@ApiModel(value = "Group信息")
public class GroupOverviewVO {
    @ApiModelProperty(value = "Group名称", example = "group-know-streaming-test")
    private String name;

    @ApiModelProperty(value = "Group状态", example = "Empty")
    private String state;

    @ApiModelProperty(value = "group的成员数", example = "12")
    private Integer memberCount;

    @ApiModelProperty(value = "Topic列表", example = "[topic1,topic2]")
    private List<String> topicNameList;
}
