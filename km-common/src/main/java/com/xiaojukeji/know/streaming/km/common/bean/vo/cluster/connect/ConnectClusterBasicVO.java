package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 集群的Connect集群信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Connect集群基本信息")
public class ConnectClusterBasicVO extends BaseVO {
    @ApiModelProperty(value = "Connect集群ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "Connect集群名称", example = "know-streaming")
    private String name;

    @ApiModelProperty(value = "Connect集群使用的Group", example = "know-streaming")
    private String groupName;

    @ApiModelProperty(value = "Connect集群URL", example = "http://127.0.0.1:8080")
    private String clusterUrl;

    @ApiModelProperty(value = "Connect集群获取到的URL", example = "http://127.0.0.1:8080")
    private String memberLeaderUrl;

    @ApiModelProperty(value = "Connect集群版本", example = "2.5.1")
    private String version;

    @ApiModelProperty(value = "JMX配置", example = "")
    private String jmxProperties;

    /**
     * 集群使用的消费组状态，也表示集群状态
     * @see com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum
     */
    @ApiModelProperty(value = "状态，2表示Dead，只有Dead才可以删除", example = "")
    private Integer state;
}
