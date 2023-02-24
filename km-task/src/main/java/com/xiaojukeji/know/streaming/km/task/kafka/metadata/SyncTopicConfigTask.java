package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicConfig;
import com.xiaojukeji.know.streaming.km.common.bean.po.topic.TopicPO;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Topic配置同步
 * @author zengqiao
 * @date 22/02/25
 */
@Task(name = "SyncTopicConfigTask",
        description = "Topic保存时间配置同步DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncTopicConfigTask extends AbstractAsyncMetadataDispatchTask {
    protected static final ILog log = LogFactory.getLog(SyncTopicConfigTask.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicConfigService kafkaConfigService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        boolean success = true;

        List<TopicConfig> changedConfigList = new ArrayList<>();
        for (TopicPO topicPO: topicService.listTopicPOsFromDB(clusterPhy.getId())) {
            Result<TopicConfig> configResult = this.getTopicConfig(clusterPhy.getId(), topicPO.getTopicName());
            if (configResult.failed()) {
                success = false;
                continue;
            }

            TopicConfig config = configResult.getData();
            if (topicPO.getRetentionMs().equals(config.getRetentionMs())) {
                // 数据无变化，不需要加入待更新列表中
                continue;
            }

            config.setId(topicPO.getId());
            changedConfigList.add(configResult.getData());
        }

        topicService.batchReplaceChangedConfig(clusterPhy.getId(), changedConfigList);

        return success? TaskResult.SUCCESS: TaskResult.FAIL;
    }

    private Result<TopicConfig> getTopicConfig(Long clusterPhyId, String topicName) {
        Result<Map<String, String>> configResult = kafkaConfigService.getTopicConfigFromKafka(clusterPhyId, topicName);
        if (configResult.failed()) {
            return Result.buildFromIgnoreData(configResult);
        }

        Map<String, String> configMap = configResult.getData();
        if (configMap == null) {
            configMap = new HashMap<>();
        }

        TopicConfig config = new TopicConfig();
        config.setClusterPhyId(clusterPhyId);
        config.setTopicName(topicName);

        Long retentionMs = ConvertUtil.string2Long(configMap.get(org.apache.kafka.common.config.TopicConfig.RETENTION_MS_CONFIG));
        config.setRetentionMs(retentionMs == null? -1L: retentionMs);

        return Result.buildSuc(config);
    }
}
