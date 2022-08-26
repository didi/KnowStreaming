package com.xiaojukeji.know.streaming.km.common.bean.vo.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Km版本信息")
public class VersionItemVO {
    @ApiModelProperty(value = "指标或操作项分类", example = "901")
    private int type;

    @ApiModelProperty(value = "指标或操作项名称", example = "BytesIn")
    private String name;

    @ApiModelProperty(value = "指标的单位，非指标的没有", example = "GB/s")
    private String unit;

    @ApiModelProperty(value = "指标的类别", example = "GB/s")
    private String category;

    @ApiModelProperty(value = "指标的描述", example = "直接流入量")
    private String desc;

    @ApiModelProperty(value = "当前kafka版本是否支持", example = "true")
    private Boolean support;

    @ApiModelProperty(value = "当前指标支持的最小版本，归一化之后的版本号，包含", example = "0")
    private Long    minVersion;

    @ApiModelProperty(value = "当前指标支持的最大版本，归一化之后的版本号，不包含，所以该指标支持的版本区间是[minVersion, maxVersion)，左闭右开", example = "111111")
    private Long    maxVersion;
}
