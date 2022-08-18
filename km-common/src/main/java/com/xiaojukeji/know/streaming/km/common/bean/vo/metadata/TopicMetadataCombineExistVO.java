package com.xiaojukeji.know.streaming.km.common.bean.vo.metadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@ApiModel(description="Topic是否存在及元信息")
public class TopicMetadataCombineExistVO extends TopicMetadataVO {
    @ApiModelProperty(value="是否存在，true:是 , false:否")
    private Boolean exist;
}
