package com.xiaojukeji.know.streaming.km.core.service.config;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;

import java.util.List;
import java.util.Map;

/**
 * 平台集群配置
 * 与Logi-Common中的配置的差别在于该Service的配置是集群相关的配置
 */
public interface PlatformClusterConfigService {
    Result<Void> batchReplace(List<PlatformClusterConfigPO> poList, String operator);

    List<PlatformClusterConfigPO> getByClusterAndGroup(Long clusterPhyId, String group);

    Map<String, PlatformClusterConfigPO> getByClusterAndGroupWithoutDefault(Long clusterPhyId, String group);

    Map<Long, Map<String, PlatformClusterConfigPO>> listByGroup(String groupName);
}
