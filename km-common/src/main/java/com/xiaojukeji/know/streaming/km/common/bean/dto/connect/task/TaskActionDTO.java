package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.task;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorActionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@ApiModel(description = "操作Task")
public class TaskActionDTO extends ConnectorActionDTO {
    @NotNull(message = "taskId不允许为NULL")
    @ApiModelProperty(value = "taskId", example = "123")
    private Long taskId;
}
