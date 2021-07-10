package com.xiaojukeji.kafka.manager.web.api;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author zengqiao
 * @date 20/6/18
 */
@ApiIgnore
@Api(tags = "web应用探活接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_THIRD_PART_PREFIX)
public class HealthController {

    @ApiIgnore
    @RequestMapping(path = "health", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "探活接口", notes = "")
    public Result<String> health() {
        return Result.buildSuc();
    }
}