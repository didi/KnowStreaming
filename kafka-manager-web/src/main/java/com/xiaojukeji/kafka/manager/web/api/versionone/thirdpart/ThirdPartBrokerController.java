package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.openapi.common.vo.ThirdPartBrokerOverviewVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.openapi.common.vo.BrokerRegionVO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author zengqiao
 * @date 20/9/9
 */
@Api(tags = "开放接口-Broker相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_OP_PREFIX)
public class ThirdPartBrokerController {
    @Autowired
    private BrokerService brokerService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "Broker信息概览", notes = "")
    @RequestMapping(value = "{clusterId}/brokers/{brokerId}/overview", method = RequestMethod.GET)
    @ResponseBody
    public Result<ThirdPartBrokerOverviewVO> getBrokerOverview(@PathVariable Long clusterId,
                                                               @PathVariable Integer brokerId) {
        BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
        if (ValidateUtils.isNull(brokerMetadata)) {
            return Result.buildFrom(ResultStatus.BROKER_NOT_EXIST);
        }

        BrokerMetrics brokerMetrics = brokerService.getBrokerMetricsFromJmx(
                clusterId,
                brokerId,
                KafkaMetricsCollections.BROKER_STATUS_PAGE_METRICS
        );
        if (ValidateUtils.isNull(brokerMetrics)) {
            return Result.buildFrom(ResultStatus.OPERATION_FAILED);
        }
        Integer underReplicated = brokerMetrics.getSpecifiedMetrics("UnderReplicatedPartitionsValue", Integer.class);
        if (ValidateUtils.isNull(underReplicated)) {
            return Result.buildFrom(ResultStatus.OPERATION_FAILED);
        }

        return new Result<>(new ThirdPartBrokerOverviewVO(clusterId, brokerId, underReplicated.equals(0)));
    }

    @ApiOperation(value = "BrokerRegion信息", notes = "所有集群的")
    @RequestMapping(value = "broker-regions", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BrokerRegionVO>> getBrokerRegions() {
        List<ClusterDO> clusterDOList = clusterService.list();
        if (ValidateUtils.isNull(clusterDOList)) {
            clusterDOList = new ArrayList<>();
        }

        List<RegionDO> regionDOList = regionService.listAll();
        if (ValidateUtils.isNull(regionDOList)) {
            regionDOList = new ArrayList<>();
        }

        List<BrokerRegionVO> voList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterDOList) {
            Map<Integer, RegionDO> brokerIdRegionMap = regionService.convert2BrokerIdRegionMap(
                    regionDOList.stream().filter(elem -> clusterDO.getId().equals(elem.getClusterId())).collect(Collectors.toList())
            );
            for (Integer brokerId: PhysicalClusterMetadataManager.getBrokerIdList(clusterDO.getId())) {
                BrokerRegionVO vo = new BrokerRegionVO();
                vo.setClusterId(clusterDO.getId());
                vo.setBrokerId(brokerId);

                BrokerMetadata metadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterDO.getId(), brokerId);
                if (!ValidateUtils.isNull(metadata)) {
                    vo.setHostname(metadata.getHost());
                }
                RegionDO regionDO = brokerIdRegionMap.get(brokerId);
                if (!ValidateUtils.isNull(regionDO)) {
                    vo.setRegionName(regionDO.getName());
                }
                voList.add(vo);
            }
        }
        return new Result<>(voList);
    }
}