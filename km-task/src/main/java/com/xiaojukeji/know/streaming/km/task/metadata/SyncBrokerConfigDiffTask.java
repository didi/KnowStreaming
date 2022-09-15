package com.xiaojukeji.know.streaming.km.task.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.broker.BrokerConfigPO;
import com.xiaojukeji.know.streaming.km.common.enums.config.ConfigDiffTypeEnum;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerConfigService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Broker配置的diff信息同步到DB
 * @author zengqiao
 * @date 22/02/25
 */
@Task(name = "SyncBrokerConfigDiffTask",
        description = "Broker配置的Diff信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncBrokerConfigDiffTask extends AbstractAsyncMetadataDispatchTask {
    protected static final ILog log = LogFactory.getLog(SyncBrokerConfigDiffTask.class);

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerConfigService brokerConfigService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        // <configName, <BrokerId, ConfigValue>>
        Map<String, Map<Integer, String>> allConfigMap = new HashMap<>();

        List<Broker> brokerList = brokerService.listAliveBrokersFromDB(clusterPhy.getId());
        Set<Integer> brokerIdSet = brokerList.stream().map(elem -> elem.getBrokerId()).collect(Collectors.toSet());

        // 获取所有集群的配置
        for (Integer brokerId: brokerIdSet) {
            Result<List<KafkaConfigDetail>> configResult = brokerConfigService.getBrokerConfigDetailFromKafka(clusterPhy.getId(), brokerId);
            if (configResult.failed()) {
                log.error("method=processSubTask||clusterPhyId={}||brokerId={}||result={}||errMsg=get config failed!",
                        clusterPhy.getId(),
                        brokerId,
                        configResult
                );
                continue;
            }

            List<KafkaConfigDetail> configList = configResult.hasData()? configResult.getData(): new ArrayList<>();
            configList.forEach(elem -> {
                allConfigMap.putIfAbsent(elem.getName(), new HashMap<>());
                allConfigMap.get(elem.getName()).put(brokerId, elem.getValue());
            });
        }

        // 逐个比较
        List<BrokerConfigPO> poList = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, String>> configEntry: allConfigMap.entrySet()) {
            if (brokerIdSet.size() <= 1) {
                // 只有一台Broker，则无差异
                continue;
            }

            if (configEntry.getValue().size() == 1) {
                // 独有的
                Integer brokerId = new ArrayList<>(configEntry.getValue().keySet()).get(0);
                poList.add(new BrokerConfigPO(
                        clusterPhy.getId(),
                        brokerId,
                        configEntry.getKey(),
                        configEntry.getValue().getOrDefault(brokerId, ""),
                        ConfigDiffTypeEnum.ALONE_POSSESS.getCode(),
                        new Date(triggerTimeUnitMs))
                );
            }

            // 配置value集合
            Set<String> configValueSet = new HashSet<>(configEntry.getValue().values());
            if (configValueSet.size() <= 1) {
                // 无差异
                continue;
            }

            // Broker该配置存在差异
            configEntry.getValue().entrySet().stream().forEach(
                    elem -> poList.add(new BrokerConfigPO(
                            clusterPhy.getId(),
                            elem.getKey(),
                            configEntry.getKey(),
                            elem.getValue(),
                            ConfigDiffTypeEnum.UN_EQUAL.getCode(),
                            new Date(triggerTimeUnitMs))
                    )
            );
        }

        for (BrokerConfigPO po: poList) {
            try {
                brokerConfigService.replaceBrokerConfigDiff(po);
            } catch (Exception e) {
                log.error("method=processSubTask||clusterPhyId={}||data={}||errMsg=exception!", clusterPhy.getId(), po, e);
            }
        }

        // 删除10分钟前的差异
        brokerConfigService.deleteByUpdateTimeBeforeInDB(clusterPhy.getId(), new Date(triggerTimeUnitMs - 10 * 60 * 1000L));

        return TaskResult.SUCCESS;
    }
}
