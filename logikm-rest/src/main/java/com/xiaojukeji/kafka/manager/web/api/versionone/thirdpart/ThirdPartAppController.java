package com.xiaojukeji.kafka.manager.web.api.versionone.thirdpart;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.constant.SystemCodeConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.web.converters.AppConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/23
 */
@Api(tags = "开放接口-App相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_PREFIX)
public class ThirdPartAppController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThirdPartAppController.class);

    @Autowired
    private AppService appService;

    @ApiOperation(value = "查询负责的应用", notes = "")
    @RequestMapping(value = "principal-apps/{principal}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AppVO>> searchPrincipalApps(@PathVariable("principal") String principal,
                                                   @RequestParam("system-code") String systemCode) {
        LOGGER.info("search principal-apps, principal:{} systemCode:{}.", principal, systemCode);
        if (ValidateUtils.isBlank(principal) || ValidateUtils.isBlank(systemCode)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        if (!SystemCodeConstant.KAFKA_MANAGER.equals(systemCode)) {
            return Result.buildFrom(ResultStatus.OPERATION_FORBIDDEN);
        }
        return new Result<>(AppConverter.convert2AppVOList(
                appService.getByPrincipal(principal)
        ));
    }
}