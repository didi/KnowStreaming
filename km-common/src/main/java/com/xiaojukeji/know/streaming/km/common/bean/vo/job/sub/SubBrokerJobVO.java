package com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public abstract class SubBrokerJobVO extends SubJobVO {
    @ApiModelProperty(value = "源BrokerId列表")
    protected List<Integer> sourceBrokers;

    @ApiModelProperty(value = "目标BrokerId列表")
    protected List<Integer> desBrokers;
}
