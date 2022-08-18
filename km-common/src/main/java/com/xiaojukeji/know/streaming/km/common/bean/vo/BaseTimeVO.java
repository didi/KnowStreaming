package com.xiaojukeji.know.streaming.km.common.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author d06679
 * @date 2019/3/13
 */
@ToString
@Data
public class BaseTimeVO extends BaseVO {
    @ApiModelProperty(value = "创建时间(ms)", example = "1645608135717")
    private Date createTime;

    @ApiModelProperty(value = "更新时间(ms)", example = "1645608135717")
    private Date updateTime;
}
