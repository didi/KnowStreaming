package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Data
@ApiModel(description = "Connect插件信息")
public class ConnectPluginBasicVO extends BaseVO {
    @ApiModelProperty(value = "指标或操作项名称", example = "org.apache.kafka.connect.file.FileStreamSinkConnector")
    private String className;

    @ApiModelProperty(value = "类型", example = "source|sink")
    private String type;

    @ApiModelProperty(value = "版本", example = "2.5.1")
    private String version;

    @ApiModelProperty(value = "帮助文档地址", example = "")
    private String helpDocLink;
}
