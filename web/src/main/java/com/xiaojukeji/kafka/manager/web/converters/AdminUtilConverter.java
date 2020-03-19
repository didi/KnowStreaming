package com.xiaojukeji.kafka.manager.web.converters;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.web.model.topic.AdminTopicModel;
import com.xiaojukeji.kafka.manager.web.vo.topic.TopicDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 19/7/11
 */
public class AdminUtilConverter {
    public static TopicDO convert2TopicDO(AdminTopicModel reqObj) {
        TopicDO topicDO = new TopicDO();
        topicDO.setClusterId(reqObj.getClusterId());
        topicDO.setTopicName(reqObj.getTopicName());
        topicDO.setPrincipals(ListUtils.strList2String(reqObj.getPrincipalList()));
        topicDO.setStatus(DBStatusEnum.NORMAL.getStatus());
        topicDO.setDescription(StringUtils.isEmpty(reqObj.getDescription())? "": reqObj.getDescription());
        return topicDO;
    }

    public static TopicMetadata convert2TopicMetadata(String topicName,
                                                      Integer partitionNum,
                                                      Integer replicaNum,
                                                      List<Integer> brokerIdList) {
        TopicMetadata topicMetadata = new TopicMetadata();
        topicMetadata.setTopic(topicName);
        topicMetadata.setBrokerIdSet(new HashSet<>(brokerIdList));
        topicMetadata.setPartitionNum(partitionNum);
        topicMetadata.setReplicaNum(replicaNum);
        return topicMetadata;
    }

    public static TopicDetailVO convert2TopicDetailVO(ClusterDO clusterDO,
                                                      TopicMetadata topicMetadata,
                                                      Properties properties,
                                                      TopicDO topicDO) {
        TopicDetailVO topicDetailVO = new TopicDetailVO();
        topicDetailVO.setClusterId(clusterDO.getId());
        topicDetailVO.setTopicName(topicMetadata.getTopic());
        topicDetailVO.setGmtCreate(topicMetadata.getCreateTime());
        topicDetailVO.setGmtModify(topicMetadata.getModifyTime());
        topicDetailVO.setPartitionNum(topicMetadata.getPartitionNum());
        topicDetailVO.setReplicaNum(topicMetadata.getReplicaNum());
        if (topicDO != null) {
            topicDetailVO.setPrincipalList(ListUtils.string2StrList(topicDO.getPrincipals()));
            topicDetailVO.setDescription(topicDO.getDescription());
        }
        if (properties == null) {
            properties = new Properties();
        }
        topicDetailVO.setProperties(JSON.toJSONString(properties));
        Object retentionTime = properties.get("retention.ms");
        if (retentionTime != null && retentionTime instanceof String) {
            topicDetailVO.setRetentionTime(Long.valueOf((String) retentionTime ));
        } else if (retentionTime != null && retentionTime instanceof Long) {
            topicDetailVO.setRetentionTime((Long) retentionTime);
        } else if (retentionTime != null && retentionTime instanceof Integer) {
            topicDetailVO.setRetentionTime(Long.valueOf((Integer) retentionTime ));
        }
        return topicDetailVO;
    }
}