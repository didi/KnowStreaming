package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.bizenum.RebalanceDimensionEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.RebalanceDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Leader操作[选举|切换]相关接口
 * @author zengqiao
 * @date 21/5/18
 */
@Api(tags = "OP-Leader操作相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpLeaderController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "优先副本选举状态")
    @RequestMapping(value = {"leaders/preferred-replica-election-status", "utils/rebalance-status"}, method = RequestMethod.GET)
    @ResponseBody
    public Result preferredReplicaElectStatus(@RequestParam("clusterId") Long clusterId) {
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        TaskStatusEnum statusEnum = adminService.preferredReplicaElectionStatus(clusterDO);
        return new Result<>(JsonUtils.toJson(statusEnum));
    }

    @ApiOperation(value = "优先副本选举")
    @RequestMapping(value = {"leaders/preferred-replica-election", "utils/rebalance"}, method = RequestMethod.POST)
    @ResponseBody
    public Result preferredReplicaElect(@RequestBody RebalanceDTO reqObj) {
        if (!reqObj.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (ValidateUtils.isNull(clusterDO)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        ResultStatus rs = null;
        if (RebalanceDimensionEnum.CLUSTER.getCode().equals(reqObj.getDimension())) {
            // 按照Cluster纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, SpringTool.getUserName());
        } else if (RebalanceDimensionEnum.BROKER.getCode().equals(reqObj.getDimension())) {
            // 按照Broker纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, reqObj.getBrokerId(), SpringTool.getUserName());
        } else if (RebalanceDimensionEnum.TOPIC.getCode().equals(reqObj.getDimension())) {
            // 按照Topic纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, reqObj.getTopicName(), SpringTool.getUserName());
        } else if (RebalanceDimensionEnum.PARTITION.getCode().equals(reqObj.getDimension())) {
            // 按照Partition纬度均衡
            rs = adminService.preferredReplicaElection(clusterDO, reqObj.getTopicName(), reqObj.getPartitionId(), SpringTool.getUserName());
        } else {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(rs);
    }
}
