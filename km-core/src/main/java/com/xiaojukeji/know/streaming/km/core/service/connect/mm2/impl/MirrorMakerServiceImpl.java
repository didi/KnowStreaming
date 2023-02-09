package com.xiaojukeji.know.streaming.km.core.service.connect.mm2.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;
import com.xiaojukeji.know.streaming.km.common.jmx.JmxConnectorWrap;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.mm2.MirrorMakerService;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerService;
import com.xiaojukeji.know.streaming.km.persistence.connect.ConnectJMXClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.constant.connect.KafkaConnectConstant.MIRROR_MAKER_TOPIC_PARTITION_PATTERN;

/**
 * @author wyb
 * @date 2022/12/14
 */
@Service
public class MirrorMakerServiceImpl implements MirrorMakerService {
    private static final ILog LOGGER = LogFactory.getLog(MirrorMakerServiceImpl.class);

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ConnectJMXClient connectJMXClient;

    @Override
    public Result<Map<String, MirrorMakerTopic>> getMirrorMakerTopicMap(Long connectClusterId) {

        List<ConnectWorker> connectWorkerList = workerService.listFromDB(connectClusterId);

        //Map<TopicName,MirrorMakerTopic>
        Map<String, MirrorMakerTopic> topicMap = new HashMap<>();

        for (ConnectWorker connectWorker : connectWorkerList) {
            JmxConnectorWrap jmxConnectorWrap = connectJMXClient.getClientWithCheck(connectClusterId, connectWorker.getWorkerId());
            Set<ObjectName> objectNameSet = new HashSet<>();
            try {
                objectNameSet = jmxConnectorWrap.queryNames(new ObjectName(MIRROR_MAKER_TOPIC_PARTITION_PATTERN), null);
            } catch (Exception e) {
                LOGGER.error("method=getMirrorMakerTopic||connectClusterId={}||workerId={}||queryNames failed!",
                        connectClusterId, connectWorker.getWorkerId());
                continue;
            }

            //解析数据
            for (ObjectName objectName : objectNameSet) {
                try {
                    String[] paramList = objectName.getCanonicalName().split(",");

                    String clusterAlias = paramList[1].split("=")[1];
                    String topicName = paramList[2].split("=")[1];
                    Integer partition = Integer.valueOf(paramList[0].split("=")[1]);

                    MirrorMakerTopic mirrorMakerTopic = topicMap.get(topicName);

                    if (mirrorMakerTopic == null) {
                        mirrorMakerTopic = new MirrorMakerTopic(clusterAlias, topicName, new HashMap<>());
                        topicMap.put(topicName, mirrorMakerTopic);
                    }

                    mirrorMakerTopic.getPartitionMap().put(partition, connectWorker.getWorkerId());
                } catch (Exception e) {
                    LOGGER.error("method=getMirrorMakerTopic||connectClusterId={}||workerId={}||canonicalName={}||canonicalName explain error!",
                            connectClusterId, connectWorker.getWorkerId(), objectName.getCanonicalName());
                }

            }
        }
        return Result.buildSuc(topicMap);
    }

    @Override
    public List<MirrorMakerTopic> getMirrorMakerTopicList(ConnectorPO mirrorMaker, Map<String, MirrorMakerTopic> mirrorMakerTopicMap) {
        List<MirrorMakerTopic> mirrorMakerTopicList = new ArrayList<>();
        List<String> topicList = CommonUtils.string2StrList(mirrorMaker.getTopics());

        for (String topicName : topicList) {
            MirrorMakerTopic mirrorMakerTopic = mirrorMakerTopicMap.get(topicName);
            if (mirrorMakerTopic != null) {
                mirrorMakerTopicList.add(mirrorMakerTopic);
            }
        }
        return mirrorMakerTopicList;
    }
}
