package com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author wyb
 * @date 2022/12/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MirrorMakerTopic {

    /**
     * mm2集群别名
     */
    private String clusterAlias;

    /**
     * topic名称
     */
    private String topicName;

    /**
     * partition在connect上的分布  Map<PartitionId,WorkerId>
     */
    private Map<Integer,String> partitionMap;

}
