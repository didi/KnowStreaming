package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionAddGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionDeleteGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionModifyGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.common.bizenum.gateway.GatewayConfigKeyEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.GatewayConfigService;
import com.xiaojukeji.kafka.manager.web.converters.GatewayModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "OP-Gateway配置相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpGatewayConfigController {
    @Autowired
    private GatewayConfigService gatewayConfigService;

    @ApiOperation(value = "Gateway配置类型", notes = "")
    @GetMapping(value = "gateway-configs/type-enums")
    @ResponseBody
    public Result getClusterModesEnum() {
        return new Result<>(JsonUtils.toJson(GatewayConfigKeyEnum.class));
    }

    @ApiOperation(value = "创建Gateway配置", notes = "")
    @PostMapping(value = "gateway-configs")
    @ResponseBody
    public Result createGatewayConfig(@RequestBody OrderExtensionAddGatewayConfigDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return gatewayConfigService.insert(GatewayModelConverter.convert2GatewayConfigDO(dto));
    }

    @ApiOperation(value = "修改Gateway配置", notes = "")
    @PutMapping(value = "gateway-configs")
    @ResponseBody
    public Result modifyGatewayConfig(@RequestBody OrderExtensionModifyGatewayConfigDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return gatewayConfigService.updateById(GatewayModelConverter.convert2GatewayConfigDO(dto));
    }

    @ApiOperation(value = "删除Gateway配置", notes = "")
    @DeleteMapping(value = "gateway-configs")
    @ResponseBody
    public Result deleteGatewayConfig(@RequestBody OrderExtensionDeleteGatewayConfigDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return gatewayConfigService.deleteById(dto.getId());
    }
}
