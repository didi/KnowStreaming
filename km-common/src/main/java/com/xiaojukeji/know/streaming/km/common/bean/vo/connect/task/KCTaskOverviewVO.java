package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Task信息概览
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Task信息概览")
public class KCTaskOverviewVO extends BaseVO {

    @ApiModelProperty(value = "connect集群ID", example = "1")
    private Long connectClusterId;

    @ApiModelProperty(value = "taskId", example = "1")
    private Integer taskId;

    @ApiModelProperty(value = "worker地址", example = "127.0.0.1:8080")
    private String workerId;

    @ApiModelProperty(value = "task状态", example = "RUNNING")
    private String state;

    @ApiModelProperty(value = "错误原因", example = "asx")
    private String trace;
}
