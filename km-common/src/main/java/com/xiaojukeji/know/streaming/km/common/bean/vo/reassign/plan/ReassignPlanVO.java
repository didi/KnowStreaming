package com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "迁移计划")
public class ReassignPlanVO {
    @ApiModelProperty(value="迁移计划列表")
    private List<ReassignTopicPlanVO> topicPlanList;
}
