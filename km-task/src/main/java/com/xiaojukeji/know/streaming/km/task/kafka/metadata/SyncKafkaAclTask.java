package com.xiaojukeji.know.streaming.km.task.kafka.metadata;

import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.acl.OpKafkaAclService;
import org.apache.kafka.common.acl.AclBinding;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Task(name = "SyncKafkaAclTask",
        description = "KafkaAcl信息同步到DB",
        cron = "0 0/1 * * * ? *",
        autoRegister = true,
        consensual = ConsensualEnum.BROADCAST,
        timeout = 2 * 60)
public class SyncKafkaAclTask extends AbstractAsyncMetadataDispatchTask {
    private static final ILog log = LogFactory.getLog(SyncKafkaAclTask.class);

    @Autowired
    private KafkaAclService kafkaAclService;

    @Autowired
    private OpKafkaAclService opKafkaAclService;

    @Override
    public TaskResult processClusterTask(ClusterPhy clusterPhy, long triggerTimeUnitMs) {
        Result<List<AclBinding>> aclBindingListResult = kafkaAclService.getAclFromKafka(clusterPhy.getId());
        if (aclBindingListResult.failed()) {
            return TaskResult.FAIL;
        }

        if (!aclBindingListResult.hasData()) {
            return TaskResult.SUCCESS;
        }

        // 更新DB数据
        List<KafkaAclPO> poList = aclBindingListResult.getData()
                .stream()
                .map(elem -> KafkaAclConverter.convert2KafkaAclPO(clusterPhy.getId(), elem, triggerTimeUnitMs))
                .collect(Collectors.toList());

        opKafkaAclService.batchUpdateAcls(clusterPhy.getId(), poList);
        return TaskResult.SUCCESS;
    }
}
