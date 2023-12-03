package com.xiaojukeji.know.streaming.km.core.service.zookeeper;

import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.ZookeeperInfo;
import com.xiaojukeji.know.streaming.km.core.service.meta.MetaDataService;

import java.util.List;

public interface ZookeeperService extends MetaDataService<ZookeeperInfo> {
    List<ZookeeperInfo> listFromDBByCluster(Long clusterPhyId);

    /**
     * 所有服务挂掉
     * @return
     */
    boolean allServerDown(Long clusterPhyId);

    /**
     * 存在服务挂掉
     * @return
     */
    boolean existServerDown(Long clusterPhyId);
}
