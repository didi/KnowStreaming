package com.xiaojukeji.kafka.manager.common.entity.vo.ha;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@ApiModel(description="HA集群-Topic信息")
public class HaClusterTopicVO {
    @ApiModelProperty(value="当前查询的集群ID")
    private Long clusterId;

    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="生产Acl数量")
    private Integer produceAclNum;

    @ApiModelProperty(value="消费Acl数量")
    private Integer consumeAclNum;

    @ApiModelProperty(value="主集群ID")
    private Long activeClusterId;

    @ApiModelProperty(value="备集群ID")
    private Long standbyClusterId;

    @ApiModelProperty(value="主备状态")
    private Integer status;
}