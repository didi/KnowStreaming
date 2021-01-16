package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionAddGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionDeleteGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionModifyGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.GatewayConfigService;
import com.xiaojukeji.kafka.manager.web.converters.GatewayModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(tags = "OP-Gateway配置相关接口(REST)")
@RestController
public class OpGatewayConfigController {
    @Autowired
    private GatewayConfigService gatewayConfigService;

    @ApiOperation(value = "创建Gateway配置", notes = "")
    @RequestMapping(value = "gateway-configs", method = RequestMethod.POST)
    @ResponseBody
    public Result createGatewayConfig(@RequestBody OrderExtensionAddGatewayConfigDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return gatewayConfigService.insert(GatewayModelConverter.convert2GatewayConfigDO(dto));
    }

    @ApiOperation(value = "修改Gateway配置", notes = "")
    @RequestMapping(value = "gateway-configs", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyGatewayConfig(@RequestBody OrderExtensionModifyGatewayConfigDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return gatewayConfigService.updateById(GatewayModelConverter.convert2GatewayConfigDO(dto));
    }

    @ApiOperation(value = "删除Gateway配置", notes = "")
    @RequestMapping(value = "gateway-configs", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteGatewayConfig(@RequestBody OrderExtensionDeleteGatewayConfigDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return gatewayConfigService.deleteById(dto.getId());
    }
}
