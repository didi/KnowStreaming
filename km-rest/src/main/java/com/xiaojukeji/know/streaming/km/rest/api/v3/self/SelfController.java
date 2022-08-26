package com.xiaojukeji.know.streaming.km.rest.api.v3.self;

import com.xiaojukeji.know.streaming.km.biz.self.SelfManager;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.self.SelfMetricsVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/6/18
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "自身信息-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class SelfController {
    @Autowired
    private SelfManager selfManager;

    @ApiOperation(value = "指标", notes = "")
    @GetMapping(path = "self/metrics")
    @ResponseBody
    public Result<SelfMetricsVO> metrics() {
        return selfManager.metrics();
    }

    @ApiOperation(value = "版本", notes = "")
    @GetMapping(path = "self/version")
    @ResponseBody
    public Result<Properties> version() {
        return selfManager.version();
    }
}
