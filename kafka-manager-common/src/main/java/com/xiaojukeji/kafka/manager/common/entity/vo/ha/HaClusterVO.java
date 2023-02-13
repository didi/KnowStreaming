package com.xiaojukeji.kafka.manager.common.entity.vo.ha;

import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.ClusterBaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@Data
@ApiModel(description="HA集群-集群信息")
public class HaClusterVO extends ClusterBaseVO {
    @ApiModelProperty(value="broker数量")
    private Integer brokerNum;

    @ApiModelProperty(value="topic数量")
    private Integer topicNum;

    @ApiModelProperty(value="消费组数")
    private Integer consumerGroupNum;

    @ApiModelProperty(value="region数")
    private Integer regionNum;

    @ApiModelProperty(value="ControllerID")
    private Integer controllerId;

    /**
     * @see com.xiaojukeji.kafka.manager.common.bizenum.ha.HaStatusEnum
     */
    @ApiModelProperty(value="主备状态")
    private Integer haStatus;

    @ApiModelProperty(value="主topic数")
    private Long activeTopicCount;

    @ApiModelProperty(value="备topic数")
    private Long standbyTopicCount;

    @ApiModelProperty(value="备集群信息")
    private HaClusterVO haClusterVO;

    @ApiModelProperty(value="切换任务id")
    private Long haASSwitchJobId;

}