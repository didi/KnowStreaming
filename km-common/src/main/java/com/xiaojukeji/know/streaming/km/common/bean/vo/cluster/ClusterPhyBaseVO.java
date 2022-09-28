package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "集群信息")
public class ClusterPhyBaseVO extends BaseTimeVO {
    @ApiModelProperty(value="集群ID", example = "1")
    protected Long id;

    @ApiModelProperty(value="集群名称", example = "KnowStreaming")
    protected String name;

    @ApiModelProperty(value="ZK地址, 不允许修改", example = "127.0.0.1:2181")
    protected String zookeeper;

    @ApiModelProperty(value="bootstrap地址", example = "127.0.0.1:9093")
    protected String bootstrapServers;

    @ApiModelProperty(value="KM连接集群时使用的客户端配置", example = "{}")
    protected String clientProperties;

    @ApiModelProperty(value="Jmx配置", example = "{}")
    protected String jmxProperties;

    @ApiModelProperty(value="ZK配置", example = "{}")
    protected String zkProperties;

    @ApiModelProperty(value="描述", example = "测试")
    protected String description;

    @ApiModelProperty(value="集群的kafka版本", example = "2.5.1")
    protected String kafkaVersion;

    @ApiModelProperty(value="集群的运行模式", example = "2：raft模式，其他是ZK模式")
    private Integer runState;
}
