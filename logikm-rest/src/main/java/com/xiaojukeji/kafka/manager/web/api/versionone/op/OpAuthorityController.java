package com.xiaojukeji.kafka.manager.web.api.versionone.op;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.openapi.common.dto.TopicAuthorityDTO;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.web.converters.AuthorityConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Authority操作相关接口
 * @author zengqiao
 * @date 21/5/18
 */
@Api(tags = "OP-Authority操作相关接口(REST)")
@RestController
public class OpAuthorityController {
    @Autowired
    private TopicManagerService topicManagerService;

    @ApiOperation(value = "权限调整",notes = "权限调整")
    @PostMapping(value = "topic-authorities")
    @ResponseBody
    public Result addAuthority(@RequestBody TopicAuthorityDTO dto) {
        //非空校验
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(topicManagerService.addAuthority(AuthorityConverter.convert2AuthorityDO(dto)));
    }
}
