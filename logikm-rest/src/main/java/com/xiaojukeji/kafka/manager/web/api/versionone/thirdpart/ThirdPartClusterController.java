package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 20/11/9
 */
@Api(tags = "开放接口-Cluster相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_PREFIX)
public class ThirdPartClusterController {

    @Autowired
    private BrokerService brokerService;

    @ApiOperation(value = "Broker信息概览", notes = "")
    @RequestMapping(value = "{clusterId}/broker-stabled", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> checkBrokerStabled(@PathVariable Long clusterId,
                                              @RequestParam("hostname") String hostname) {
        BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, hostname);
        if (ValidateUtils.isNull(brokerMetadata)) {
            return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
        }

        BrokerMetrics brokerMetrics = brokerService.getBrokerMetricsFromJmx(
                clusterId,
                brokerMetadata.getBrokerId(),
                KafkaMetricsCollections.BROKER_STATUS_PAGE_METRICS
        );
        if (ValidateUtils.isNull(brokerMetrics)) {
            return Result.buildFrom(ResultStatus.OPERATION_FAILED);
        }
        Integer underReplicated = brokerMetrics.getSpecifiedMetrics("UnderReplicatedPartitionsValue", Integer.class);
        if (ValidateUtils.isNull(underReplicated)) {
            return Result.buildFrom(ResultStatus.OPERATION_FAILED);
        }

        return new Result<>(underReplicated.equals(0));
    }
}
