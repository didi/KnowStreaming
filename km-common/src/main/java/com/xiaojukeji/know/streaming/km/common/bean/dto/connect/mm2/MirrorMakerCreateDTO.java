package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorCreateDTO;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.kafka.clients.CommonClientConfigs;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 2022-12-12
 */
@Data
@ApiModel(description = "创建MM2")
public class MirrorMakerCreateDTO extends ConnectorCreateDTO {
    @NotNull(message = "sourceKafkaClusterId不允许为空")
    @ApiModelProperty(value = "源Kafka集群ID", example = "")
    private Long sourceKafkaClusterId;

    @Valid
    @ApiModelProperty(value = "heartbeat-connector的信息", example = "")
    private Properties heartbeatConnectorConfigs;

    @Valid
    @ApiModelProperty(value = "checkpoint-connector的信息", example = "")
    private Properties checkpointConnectorConfigs;

    public void unifyData(Long sourceKafkaClusterId, String sourceBootstrapServers, Properties sourceKafkaProps,
                          Long targetKafkaClusterId, String targetBootstrapServers, Properties targetKafkaProps) {
        if (sourceKafkaProps == null) {
            sourceKafkaProps = new Properties();
        }

        if (targetKafkaProps == null) {
            targetKafkaProps = new Properties();
        }

        this.unifyData(this.getSuitableConfig(), sourceKafkaClusterId, sourceBootstrapServers, sourceKafkaProps, targetKafkaClusterId, targetBootstrapServers, targetKafkaProps);

        if (heartbeatConnectorConfigs != null) {
            this.unifyData(this.heartbeatConnectorConfigs, sourceKafkaClusterId, sourceBootstrapServers, sourceKafkaProps, targetKafkaClusterId, targetBootstrapServers, targetKafkaProps);
        }

        if (checkpointConnectorConfigs != null) {
            this.unifyData(this.checkpointConnectorConfigs, sourceKafkaClusterId, sourceBootstrapServers, sourceKafkaProps, targetKafkaClusterId, targetBootstrapServers, targetKafkaProps);
        }
    }

    private void unifyData(Properties dataConfig,
                           Long sourceKafkaClusterId, String sourceBootstrapServers, Properties sourceKafkaProps,
                           Long targetKafkaClusterId, String targetBootstrapServers, Properties targetKafkaProps) {
        dataConfig.put(KafkaConnectConstant.MIRROR_MAKER_SOURCE_CLUSTER_ALIAS_FIELD_NAME, sourceKafkaClusterId);
        dataConfig.put(KafkaConnectConstant.MIRROR_MAKER_SOURCE_CLUSTER_FIELD_NAME + "." + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, sourceBootstrapServers);
        for (Object configKey: sourceKafkaProps.keySet()) {
            dataConfig.put(KafkaConnectConstant.MIRROR_MAKER_SOURCE_CLUSTER_FIELD_NAME + "." + configKey, sourceKafkaProps.getProperty((String) configKey));
        }

        dataConfig.put(KafkaConnectConstant.MIRROR_MAKER_TARGET_CLUSTER_ALIAS_FIELD_NAME, targetKafkaClusterId);
        dataConfig.put(KafkaConnectConstant.MIRROR_MAKER_TARGET_CLUSTER_FIELD_NAME + "." + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, targetBootstrapServers);
        for (Object configKey: targetKafkaProps.keySet()) {
            dataConfig.put(KafkaConnectConstant.MIRROR_MAKER_TARGET_CLUSTER_FIELD_NAME + "." + configKey, targetKafkaProps.getProperty((String) configKey));
        }
    }
}
