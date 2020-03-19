package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.PreferredReplicaElectEnum;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.impl.AdminPreferredReplicaElectServiceImpl;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.web.model.RebalanceModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 优先副本选举
 * @author zengqiao
 * @date 2019-04-22
 */
@Api(value = "AdminRebalanceController", description = "优先副本选举相关接口")
@Controller
@RequestMapping("api/v1/admin/utils/")
public class AdminRebalanceController {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AdminPreferredReplicaElectServiceImpl adminPreferredReplicaElectService;

    private static final Logger logger = LoggerFactory.getLogger(AdminRebalanceController.class);

    @ApiOperation(value = "查看优先副本选举状态", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = PreferredReplicaElectEnum.class)
    @RequestMapping(value = "rebalance/clusters/{clusterId}/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<PreferredReplicaElectEnum> preferredReplicaElectStatus(@PathVariable Long clusterId) {
        if (clusterId == null || clusterId < 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(clusterId);
        if (clusterDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        PreferredReplicaElectEnum preferredReplicaElectEnum = adminPreferredReplicaElectService.preferredReplicaElectionStatus(clusterDO);
        return new Result<>(preferredReplicaElectEnum);
    }

    @ApiOperation(value = "进行优先副本选举", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "rebalance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result preferredReplicaElect(@RequestBody RebalanceModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        ClusterDO clusterDO = clusterService.getById(reqObj.getClusterId());
        if (clusterDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, cluster not exist");
        }
        String operator = SpringContextHolder.getUserName();

        PreferredReplicaElectEnum preferredReplicaElectEnum = null;
        if (reqObj.getDimension().equals(0)) {
            preferredReplicaElectEnum = adminPreferredReplicaElectService.preferredReplicaElection(clusterDO, operator);
        } else if (reqObj.getDimension().equals(1)) {
            preferredReplicaElectEnum = adminPreferredReplicaElectService.preferredReplicaElection(clusterDO, reqObj.getBrokerId(), operator);
        } else {
            // TODO: 19/7/8 Topic维度优先副本选举
        }
        if (PreferredReplicaElectEnum.SUCCESS.equals(preferredReplicaElectEnum)) {
            return new Result();
        }
        return new Result(StatusCode.OPERATION_ERROR, preferredReplicaElectEnum.getMessage());
    }
}
