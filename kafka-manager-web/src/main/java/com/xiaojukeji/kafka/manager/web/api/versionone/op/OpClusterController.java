package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.ControllerPreferredCandidateDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.ClusterDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.ClusterModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author zengqiao
 * @date 20/4/23
 */
@Api(tags = "OP-Cluster维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpClusterController {
    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "接入集群")
    @PostMapping(value = "clusters")
    @ResponseBody
    public Result addNew(@RequestBody ClusterDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                clusterService.addNew(ClusterModelConverter.convert2ClusterDO(dto), SpringTool.getUserName())
        );
    }

    @ApiOperation(value = "删除集群")
    @DeleteMapping(value = "clusters")
    @ResponseBody
    public Result delete(@RequestParam(value = "clusterId") Long clusterId) {
        return Result.buildFrom(clusterService.deleteById(clusterId, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改集群信息")
    @PutMapping(value = "clusters")
    @ResponseBody
    public Result modify(@RequestBody ClusterDTO reqObj) {
        if (ValidateUtils.isNull(reqObj) || !reqObj.legal() || ValidateUtils.isNull(reqObj.getClusterId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                clusterService.updateById(ClusterModelConverter.convert2ClusterDO(reqObj), SpringTool.getUserName())
        );
    }

    @ApiOperation(value = "开启|关闭集群监控")
    @PutMapping(value = "clusters/{clusterId}/monitor")
    @ResponseBody
    public Result modifyStatus(@PathVariable Long clusterId, @RequestParam("status") Integer status) {
        return Result.buildFrom(
                clusterService.modifyStatus(clusterId, status, SpringTool.getUserName())
        );
    }

    @ApiOperation(value = "增加Controller优先候选的Broker", notes = "滴滴内部引擎特性")
    @PostMapping(value = "cluster-controller/preferred-candidates")
    @ResponseBody
    public Result addControllerPreferredCandidates(@RequestBody ControllerPreferredCandidateDTO dto) {
        return clusterService.addControllerPreferredCandidates(dto.getClusterId(), dto.getBrokerIdList());
    }

    @ApiOperation(value = "删除Controller优先候选的Broker", notes = "滴滴内部引擎特性")
    @DeleteMapping(value = "cluster-controller/preferred-candidates")
    @ResponseBody
    public Result deleteControllerPreferredCandidates(@RequestBody ControllerPreferredCandidateDTO dto) {
        return clusterService.deleteControllerPreferredCandidates(dto.getClusterId(), dto.getBrokerIdList());
    }
}