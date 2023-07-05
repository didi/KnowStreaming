package com.xiaojukeji.know.streaming.km.core.service.connect.plugin.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config.ConnectConfigInfos;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.plugin.ConnectPluginBasic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.component.RestTool;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.connect.plugin.PluginService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_CONNECT_PLUGIN;

@Service
public class PluginServiceImpl extends BaseVersionControlService implements PluginService {
    private static final ILog LOGGER = LogFactory.getLog(PluginServiceImpl.class);

    @Autowired
    private RestTool restTool;

    @Autowired
    private ConnectClusterService connectClusterService;

    private static final String GET_PLUGIN_CONFIG_DESC_URI      = "/connector-plugins/%s/config/validate";
    private static final String GET_ALL_PLUGINS_URI             = "/connector-plugins";

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_CONNECT_PLUGIN;
    }

    @Override
    public Result<ConnectConfigInfos> getConfig(Long connectClusterId, String pluginName) {
        Properties props = new Properties();
        props.put(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME, pluginName);
        props.put(KafkaConnectConstant.CONNECTOR_TOPICS_FILED_NAME, KafkaConnectConstant.CONNECTOR_TOPICS_FILED_ERROR_VALUE);

        return this.validateConfig(connectClusterId, props);
    }

    @Override
    public Result<ConnectConfigInfos> validateConfig(Long connectClusterId, Properties props) {
        try {
            if (ValidateUtils.isBlank(props.getProperty(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME))) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "参数错误, connector.class字段数据不允许不存在或者为空");
            }

            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            // 通过参数检查接口，获取插件配置
            ConfigInfos configInfos = restTool.putJsonForObject(
                    connectCluster.getSuitableRequestUrl() + String.format(GET_PLUGIN_CONFIG_DESC_URI, props.getProperty(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME)),
                    props,
                    ConfigInfos.class
            );

            return Result.buildSuc(new ConnectConfigInfos(configInfos));
        } catch (Exception e) {
            LOGGER.error(
                    "method=validateConfig||connectClusterId={}||pluginName={}||errMsg=exception",
                    connectClusterId,
                    props.getProperty(KafkaConnectConstant.CONNECTOR_CLASS_FILED_NAME),
                    e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<List<ConnectPluginBasic>> listPluginsFromCluster(Long connectClusterId) {
        try {
            ConnectCluster connectCluster = connectClusterService.getById(connectClusterId);
            if (ValidateUtils.isNull(connectCluster)) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getConnectClusterNotExist(connectClusterId));
            }

            // 通过参数检查接口，获取插件配置
            List<ConnectPluginBasic> pluginList = restTool.getArrayObjectWithJsonContent(
                    connectCluster.getSuitableRequestUrl() + GET_ALL_PLUGINS_URI,
                    new HashMap<>(),
                    ConnectPluginBasic.class
            );

            return Result.buildSuc(pluginList);
        } catch (Exception e) {
            LOGGER.error(
                    "method=listPluginsFromCluster||connectClusterId={}||errMsg=exception",
                    connectClusterId, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_CONNECTOR_READ_FAILED, e.getMessage());
        }
    }
}
