package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.bizenum.RebalanceDimensionEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.RebalanceDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 20/9/23
 */
@Api(tags = "开放接口-OP相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_OP_PREFIX)
public class ThirdPartOpUtilController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "优先副本选举")
    @RequestMapping(value = "op/rebalance", method = RequestMethod.POST)
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
