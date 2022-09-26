package com.xiaojukeji.know.streaming.km.common.bean.dto.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description = "集群信息接入测试")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterPhyBaseDTO extends BaseDTO {
    @NotNull(message = "zookeeper不允许为null")
    @ApiModelProperty(value="ZK地址, 不允许修改", example = "127.0.0.1:2181")
    protected String zookeeper;

    @NotBlank(message = "bootstrapServers不允许为空串")
    @ApiModelProperty(value="bootstrap地址", example = "127.0.0.1:9093")
    protected String bootstrapServers;

    @NotNull(message = "clientProperties不允许为空")
    @ApiModelProperty(value="KM连接集群时使用的客户端配置")
    protected Properties clientProperties;

    @NotNull(message = "jmxProperties不允许为空")
    @ApiModelProperty(value="Jmx配置")
    protected JmxConfig jmxProperties;

    // TODO 前端页面增加时，需要加一个不为空的限制
    @ApiModelProperty(value="ZK配置")
    protected ZKConfig zkProperties;
}
