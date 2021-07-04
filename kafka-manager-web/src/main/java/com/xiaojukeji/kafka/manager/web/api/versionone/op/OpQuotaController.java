package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.TopicQuotaDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Quota操作相关接口
 * @author zengqiao
 * @date 21/5/18
 */
@Api(tags = "OP-Quota操作相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX)
public class OpQuotaController {
    @Autowired
    private QuotaService quotaService;

    @ApiOperation(value = "配额调整",notes = "配额调整")
    @RequestMapping(value = "topic-quotas",method = RequestMethod.POST)
    @ResponseBody
    public Result addTopicQuota(@RequestBody TopicQuotaDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            // 非空校验
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(quotaService.addTopicQuotaByAuthority(TopicQuota.buildFrom(dto)));
    }
}
