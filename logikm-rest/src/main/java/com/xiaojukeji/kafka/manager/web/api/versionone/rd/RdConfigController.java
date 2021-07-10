package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaBrokerRoleEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.ConfigConverter;
import com.xiaojukeji.kafka.manager.common.entity.dto.config.ConfigDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.ConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/19
 */
@Api(tags = "RD-Config相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdConfigController {
    @Autowired
    private ConfigService configService;

    @ApiOperation(value = "配置列表")
    @RequestMapping(value = "configs", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ConfigVO>> getConfigList() {
        return new Result<>(ConfigConverter.convert2ConfigVOList(configService.listAll()));
    }

    @ApiOperation(value = "修改配置")
    @RequestMapping(value = "configs", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyConfig(@RequestBody ConfigDTO dto) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(configService.updateByKey(dto));
    }

    @ApiOperation(value = "新增配置")
    @RequestMapping(value = "configs", method = RequestMethod.POST)
    @ResponseBody
    public Result createConfig(@RequestBody ConfigDTO dto) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(configService.insert(dto));
    }

    @ApiOperation(value = "删除配置")
    @RequestMapping(value = "configs", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteById(@RequestParam("config-key") String configKey) {
        return Result.buildFrom(configService.deleteByKey(configKey));
    }

    @ApiOperation(value = "Kafka的角色列表", notes = "")
    @RequestMapping(value = "configs/kafka-roles", method = RequestMethod.GET)
    @ResponseBody
    public Result getKafkaBrokerRoleEnum() {
        return new Result<>(JsonUtils.toJson(KafkaBrokerRoleEnum.class));
    }
}