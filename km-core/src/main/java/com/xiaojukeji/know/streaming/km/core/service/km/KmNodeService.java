package com.xiaojukeji.know.streaming.km.core.service.km;

import java.util.List;

public interface KmNodeService {

    /**
     * 获取 km 集群所有部署的 host 节点
     * @return
     */
    List<String> listKmHosts();
}
