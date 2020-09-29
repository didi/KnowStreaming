package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.RegionModelConverter;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.RegionVO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.RegionDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/21
 */
@Api(tags = "RD-Region维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdRegionController {
    @Autowired
    private RegionService regionService;

    @ApiOperation(value = "Region创建", notes = "")
    @RequestMapping(value = "regions", method = RequestMethod.POST)
    @ResponseBody
    public Result createNew(@RequestBody RegionDTO dto) {
        if (!dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(regionService.createRegion(RegionModelConverter.convert2RegionDO(dto)));
    }

    @ApiOperation(value = "Region删除", notes = "")
    @RequestMapping(value = "regions", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteById(@RequestParam("id") Long id) {
        return Result.buildFrom(regionService.deleteById(id));
    }

    @ApiOperation(value = "Region更新", notes = "")
    @RequestMapping(value = "regions", method = RequestMethod.PUT)
    @ResponseBody
    public Result updateRegion(@RequestBody RegionDTO dto) {
        if (!dto.legal() || ValidateUtils.isNull(dto.getId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(regionService.updateRegion(RegionModelConverter.convert2RegionDO(dto)));
    }

    @ApiOperation(value = "查询Region列表", notes = "")
    @RequestMapping(value = "{clusterId}/regions", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<RegionVO>> getRegionList(@PathVariable Long clusterId) {
        return new Result<>(RegionModelConverter.convert2RegionVOList(regionService.getByClusterId(clusterId)));
    }
}
