package com.xiaojukeji.know.streaming.km.common.bean.vo.metadata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="Group元信息带是否存在")
public class GroupMetadataCombineExistVO {
    @ApiModelProperty(value="物理集群ID", example = "6")
    private Long clusterPhyId;

    @ApiModelProperty(value="Group名称", example = "g-know-streaming")
    private String groupName;

    @ApiModelProperty(value="Topic名称", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value="是否存在，true:是 , false:否")
    private Boolean exist;
}
