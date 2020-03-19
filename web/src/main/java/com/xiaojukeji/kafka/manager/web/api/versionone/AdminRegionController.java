package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.web.converters.RegionModelConverter;
import com.xiaojukeji.kafka.manager.web.vo.RegionVO;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.web.model.RegionModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/21
 */
@Api(value = "RegionController", description = "Region相关接口")
@Controller
@RequestMapping("api/v1/admin/")
public class AdminRegionController {
    private static final Logger logger = LoggerFactory.getLogger(AdminRegionController.class);

    @Autowired
    private RegionService regionService;

    @ApiOperation(value = "查询Region列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = RegionVO.class)
    @RequestMapping(value = "{clusterId}/regions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<RegionVO>> getRegionList(@ApiParam(name = "clusterId", required = true, value = "集群ID") @PathVariable Long clusterId) {
        if (clusterId == null || clusterId <= 0) {
            return new Result<>(StatusCode.PARAM_ERROR, "clusterId illegal");
        }
        return new Result<>(RegionModelConverter.convert2RegionVOList(regionService.getByClusterId(clusterId)));
    }

    @ApiOperation(value = "删除Region", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "regions/{regionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result deleteTag(@ApiParam(name = "regionId", required = true) @PathVariable Long regionId) {
        if (regionId == null || regionId < 0) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, regionId illegal");
        }
        if (!regionService.deleteById(regionId)) {
            return new Result(StatusCode.MY_SQL_DELETE_ERROR, "delete region failed, maybe region not exist");
        }
        return new Result();
    }

    @ApiOperation(value = "添加Region", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success")
    @RequestMapping(value = "regions/region", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result addNewRegion(@RequestBody RegionModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, data is empty or illegal");
        }
        try {
            String operator = SpringContextHolder.getUserName();
            return regionService.createRegion(RegionModelConverter.convert2RegionDO(reqObj, operator));
        } catch (Exception e) {
            logger.error("addNewRegion@RegionController, create region failed, req:{}.", reqObj, e);
        }
        return new Result(StatusCode.OPERATION_ERROR, "create region failed");
    }

    @ApiOperation(value = "更新Region", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success")
    @RequestMapping(value = "regions/region", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result updateRegion(@RequestBody RegionModel reqObj) {
        if (reqObj == null || !reqObj.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, data is empty or illegal");
        }

        try {
            String operator = SpringContextHolder.getUserName();
            return regionService.updateRegion(RegionModelConverter.convert2RegionDO(reqObj, operator));
        } catch (Exception e) {
            logger.error("updateRegion@RegionController, update region failed, req:{}.", reqObj, e);
        }
        return new Result(StatusCode.OPERATION_ERROR, "update region failed");
    }
}
