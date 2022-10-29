package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper.Znode;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.zookeeper.ZookeeperUtils;
import org.apache.zookeeper.data.Stat;

public class ZnodeConverter {
    ZnodeConverter(){

    }

    public static Znode convert2Znode(ClusterPhy clusterPhy, Tuple<byte[], Stat> dataAndStat, String path) {
        Znode znode = new Znode();
        znode.setStat(dataAndStat.getV2());
        znode.setData(dataAndStat.getV1() == null ? null : new String(dataAndStat.getV1()));
        znode.setName(path.substring(path.lastIndexOf('/') + 1));
        znode.setNamespace(ZookeeperUtils.getNamespace(clusterPhy.getZookeeper()));

        return znode;
    }
}
