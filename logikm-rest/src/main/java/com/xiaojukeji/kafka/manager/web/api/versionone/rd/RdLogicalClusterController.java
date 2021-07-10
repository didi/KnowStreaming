package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.LogicalClusterDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster.LogicalClusterVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.LogicalClusterService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.LogicalClusterModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/29
 */
@Api(tags = "RD-LogicalCluster维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdLogicalClusterController {
    @Autowired
    private LogicalClusterService logicalClusterService;

    @ApiOperation(value = "逻辑集群创建", notes = "")
    @RequestMapping(value = "logical-clusters", method = RequestMethod.POST)
    @ResponseBody
    public Result createNew(@RequestBody LogicalClusterDTO dto) {
        if (!dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                logicalClusterService.createLogicalCluster(LogicalClusterModelConverter.convert2LogicalClusterDO(dto))
        );
    }

    @ApiOperation(value = "逻辑集群删除", notes = "")
    @RequestMapping(value = "logical-clusters", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteById(@RequestParam("id") Long id) {
        return Result.buildFrom(logicalClusterService.deleteById(id));
    }

    @ApiOperation(value = "逻辑集群更新", notes = "")
    @RequestMapping(value = "logical-clusters", method = RequestMethod.PUT)
    @ResponseBody
    public Result updateById(@RequestBody LogicalClusterDTO dto) {
        if (!dto.legal() || ValidateUtils.isNull(dto.getId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(
                logicalClusterService.updateById(LogicalClusterModelConverter.convert2LogicalClusterDO(dto))
        );
    }

    @ApiOperation(value = "查询逻辑集群列表", notes = "")
    @RequestMapping(value = "logical-clusters", method = RequestMethod.GET)
    @ResponseBody
    public Result<LogicalClusterVO> getByLogicalClusterId(@RequestParam("id") Long physicalClusterId) {
        return new Result<>(
                LogicalClusterModelConverter.convert2LogicalClusterVO(
                        logicalClusterService.getById(physicalClusterId)
                )
        );
    }

    @ApiOperation(value = "查询逻辑集群列表", notes = "")
    @RequestMapping(value = "{physicalClusterId}/logical-clusters", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<LogicalClusterVO>> getByPhysicalClusterId(@PathVariable Long physicalClusterId) {
        return new Result<>(
                LogicalClusterModelConverter.convert2LogicalClusterVOList(
                        logicalClusterService.getByPhysicalClusterId(physicalClusterId)
                )
        );
    }
}