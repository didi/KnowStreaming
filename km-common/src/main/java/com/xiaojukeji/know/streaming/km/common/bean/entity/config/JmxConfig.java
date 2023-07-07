package com.xiaojukeji.know.streaming.km.common.bean.entity.config;

import com.xiaojukeji.know.streaming.km.common.bean.entity.jmx.ServerIdJmxPort;
import com.xiaojukeji.know.streaming.km.common.enums.jmx.JmxEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "Jmx配置")
public class JmxConfig extends JmxAuthConfig {
    @ApiModelProperty(value="jmx端口，最低优先使用的端口", example = "8099")
    private Integer jmxPort;

    @ApiModelProperty(value="使用哪个endpoint网络", example = "EXTERNAL")
    private String useWhichEndpoint;

    @ApiModelProperty(value="指定server的JMX端口, 最高优先使用的端口", example = "")
    private List<ServerIdJmxPort> specifiedJmxPortList;

    /**
     * 选取最终的jmx端口
     * @param serverId 服务ID
     * @param metadataJmxPort ks从元信息中获取到的jmx端口
     */
    public Integer getFinallyJmxPort(String serverId, Integer metadataJmxPort) {
        if (specifiedJmxPortList == null || specifiedJmxPortList.isEmpty()) {
            // 未进行特殊指定时，zkJMX端口存在则优先使用zkJmxPort，否则使用配置的jmxPort
            return this.selectJmxPort(jmxPort, metadataJmxPort);
        }

        // 进行特殊配置时
        for (ServerIdJmxPort serverIdJmxPort: specifiedJmxPortList) {
            if (serverId.equals(serverIdJmxPort.getServerId()) && serverIdJmxPort.getJmxPort() != null) {
                // 当前server有指定具体的jmx端口时，则使用具体的端口
                return serverIdJmxPort.getJmxPort();
            }
        }

        return this.selectJmxPort(jmxPort, metadataJmxPort);
    }

    /**
     * 选取最终的jmx端口
     * @param serverId serverId
     */
    public Integer getFinallyJmxPort(String serverId) {
        return this.getFinallyJmxPort(serverId, null);
    }

    /**
     * 选取jmx端口
     * @param feJmxPort 前端页面配置的jmx端口
     * @param metadataJmxPort ks从元信息中获取到的jmx端口
     */
    private Integer selectJmxPort(Integer feJmxPort, Integer metadataJmxPort) {
        if (metadataJmxPort == null) {
            return feJmxPort != null? feJmxPort: JmxEnum.NOT_OPEN.getPort();
        }

        if (JmxEnum.NOT_OPEN.getPort().equals(metadataJmxPort)) {
            // 如果元信息提示未开启，则直接返回未开启
            return JmxEnum.NOT_OPEN.getPort();
        }

        if (JmxEnum.UNKNOWN.getPort().equals(metadataJmxPort)) {
            // 如果元信息提示未知，则直接返回feJmxPort 或者 未开启
            return feJmxPort != null? feJmxPort: JmxEnum.NOT_OPEN.getPort();
        }

        // 其他情况，返回 metadataJmxPort
        return metadataJmxPort;
    }
}


