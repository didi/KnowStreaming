package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.GatewayConfigVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.GatewayConfigService;
import com.xiaojukeji.kafka.manager.web.converters.GatewayModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "RD-Gateway配置相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdGatewayConfigController {
    @Autowired
    private GatewayConfigService gatewayConfigService;

    @ApiOperation(value = "Gateway相关配置信息", notes = "")
    @GetMapping(value = "gateway-configs")
    @ResponseBody
    public Result<List<GatewayConfigVO>> getGatewayConfigs() {
        List<GatewayConfigDO> doList = gatewayConfigService.list();
        if (ValidateUtils.isEmptyList(doList)) {
            return Result.buildSuc();
        }
        return Result.buildSuc(GatewayModelConverter.convert2GatewayConfigVOList(doList));
    }
}
