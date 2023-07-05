package com.xiaojukeji.know.streaming.km.common.bean.entity.param.zookeeper;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.ZKConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
public class ZookeeperParam extends ClusterPhyParam {
    private List<Tuple<String, Integer>>    zkAddressList;

    private ZKConfig                        zkConfig;

    public ZookeeperParam(Long clusterPhyId, List<Tuple<String, Integer>> zkAddressList, ZKConfig zkConfig) {
        super(clusterPhyId);
        this.zkAddressList = zkAddressList;
        this.zkConfig = zkConfig;
    }
}
