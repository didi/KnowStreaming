package com.xiaojukeji.know.streaming.km.common.bean.vo.job;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "job类型")
public class JobTypeVO extends BaseTimeVO {
    @ApiModelProperty(value = "任务类型")
    private Integer type;

    @ApiModelProperty(value = "描述信息")
    private String message;

    public JobTypeVO(Integer type, String message) {
        this.type = type;
        this.message = message;
    }
}
