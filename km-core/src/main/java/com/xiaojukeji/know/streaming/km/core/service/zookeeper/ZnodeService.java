package com.xiaojukeji.know.streaming.km.core.service.zookeeper;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.Znode;

import java.util.List;

public interface ZnodeService {

    Result<List<String>> listZnodeChildren(Long clusterPhyId, String path, String keyword);

    Result<Znode> getZnode(Long clusterPhyId, String path);
}
