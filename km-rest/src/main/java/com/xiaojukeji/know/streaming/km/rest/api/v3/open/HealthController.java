package com.xiaojukeji.know.streaming.km.rest.api.v3.open;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zengqiao
 * @date 20/6/18
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "直接开放-应用探活-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_OPEN_PREFIX)
public class HealthController {
    @ApiOperation(value = "探活", notes = "")
    @GetMapping(path = "health")
    @ResponseBody
    public Result<String> health() {
        return Result.buildSuc();
    }
}
