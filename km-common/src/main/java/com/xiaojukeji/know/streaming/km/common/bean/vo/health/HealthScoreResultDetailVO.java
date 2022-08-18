package com.xiaojukeji.know.streaming.km.common.bean.vo.health;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/03/01
 */
@Data
@ApiModel(description = "健康检查结果信息")
public class HealthScoreResultDetailVO extends HealthScoreBaseResultVO {
    @ApiModelProperty(value="未通过的资源列表", example = "")
    private List<String> notPassedResNameList;
}
