package com.xiaojukeji.know.streaming.km.core.service.connect.plugin;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config.ConnectConfigInfos;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.plugin.ConnectPluginBasic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;
import java.util.Properties;


/**
 * 查看Connector
 */
public interface PluginService {
    Result<ConnectConfigInfos> getConfig(Long connectClusterId, String pluginName);

    Result<ConnectConfigInfos> validateConfig(Long connectClusterId, Properties props);

    Result<List<ConnectPluginBasic>> listPluginsFromCluster(Long connectClusterId);
}
