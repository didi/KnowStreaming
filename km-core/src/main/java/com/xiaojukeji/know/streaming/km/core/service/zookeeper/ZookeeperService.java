package com.xiaojukeji.know.streaming.km.core.service.zookeeper;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;

import java.util.List;

public interface ZookeeperService {
    /**
     * 从ZK集群中获取ZK信息
     */
    Result<List<ZookeeperInfo>> listFromZookeeper(Long clusterPhyId, String zookeeperAddress, ZKConfig zkConfig);

    void batchReplaceDataInDB(Long clusterPhyId, List<ZookeeperInfo> infoList);

    List<ZookeeperInfo> listFromDBByCluster(Long clusterPhyId);
}
