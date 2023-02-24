package com.xiaojukeji.know.streaming.km.rest.api.v3.license;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author didi
 */
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class LicenseController {
    @GetMapping(value = "license")
    @ResponseBody
    public Result<Void> check() {
        return Result.buildSuc();
    }
}
