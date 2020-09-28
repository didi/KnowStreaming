package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.bizenum.*;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author zengqiao
 * @date 20/4/20
 */
@Api(tags = "Normal-Config相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalConfigController {

    @Autowired
    private ConfigUtils configUtils;

    @ApiOperation(value = "集群类型", notes = "")
    @RequestMapping(value = "configs/cluster-modes", method = RequestMethod.GET)
    @ResponseBody
    public Result getClusterModesEnum() {
        return new Result<>(JsonUtils.toJson(ClusterModeEnum.class));
    }

    @ApiOperation(value = "集群套餐", notes = "")
    @RequestMapping(value = "configs/cluster-combos", method = RequestMethod.GET)
    @ResponseBody
    public Result getClusterCombosEnum() {
        return new Result<>(JsonUtils.toJson(ClusterComboEnum.class));
    }

    @ApiOperation(value = "数据中心", notes = "")
    @RequestMapping(value = "configs/idc", method = RequestMethod.GET)
    @ResponseBody
    public Result getIDC() {
        return new Result<>(ListUtils.string2StrList(configUtils.getIdc()));
    }

    @ApiIgnore
    @ApiOperation(value = "数据中心集合", notes = "")
    @RequestMapping(value = "configs/idcs", method = RequestMethod.GET)
    @ResponseBody
    public Result getInternetDataCenters() {
        return new Result<>(JsonUtils.toJson(IDCEnum.class));
    }

    @ApiOperation(value = "峰值状态", notes = "")
    @RequestMapping(value = "configs/peak-flow-status", method = RequestMethod.GET)
    @ResponseBody
    public Result getPeakFlowStatusEnum() {
        return new Result<>(JsonUtils.toJson(PeakFlowStatusEnum.class));
    }

    @ApiOperation(value = "任务状态", notes = "")
    @RequestMapping(value = "configs/task-status", method = RequestMethod.GET)
    @ResponseBody
    public Result getTaskStatusEnum() {
        return new Result<>(JsonUtils.toJson(TaskStatusEnum.class));
    }
}
