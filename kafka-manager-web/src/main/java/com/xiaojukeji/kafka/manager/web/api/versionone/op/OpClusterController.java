package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
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
    @RequestMapping(value = "clusters", method = RequestMethod.POST)
    @ResponseBody
    public Result addNew(@RequestBody ClusterDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                clusterService.addNew(
                        ClusterModelConverter.convert2ClusterDO(dto),
                        SpringTool.getUserName()
                )
        );
    }

    @ApiOperation(value = "删除集群")
    @RequestMapping(value = "clusters", method = RequestMethod.DELETE)
    @ResponseBody
    public Result delete(@RequestParam(value = "clusterId") Long clusterId) {
        return Result.buildFrom(clusterService.deleteById(clusterId));
    }

    @ApiOperation(value = "修改集群信息")
    @RequestMapping(value = "clusters", method = RequestMethod.PUT)
    @ResponseBody
    public Result modify(@RequestBody ClusterDTO reqObj) {
        if (ValidateUtils.isNull(reqObj) || !reqObj.legal() || ValidateUtils.isNull(reqObj.getClusterId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ResultStatus rs = clusterService.updateById(
                ClusterModelConverter.convert2ClusterDO(reqObj),
                SpringTool.getUserName()
        );
        return Result.buildFrom(rs);
    }

    @ApiOperation(value = "开启|关闭集群监控")
    @RequestMapping(value = "clusters/{clusterId}/monitor", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyStatus(@PathVariable Long clusterId,
                               @RequestParam("status") Integer status) {
        return Result.buildFrom(
                clusterService.modifyStatus(clusterId, status, SpringTool.getUserName())
        );
    }
}