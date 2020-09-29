package com.xiaojukeji.kafka.manager.web.api.versionone.gateway;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.annotations.ApiLevel;
import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;
import com.xiaojukeji.kafka.manager.common.entity.DeprecatedResponseResult;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.KafkaAclSearchDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.KafkaUserSearchDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.gateway.KafkaSecurityVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaAclDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.KafkaUserDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.SecurityService;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.GatewayModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/7
 */
@Api(tags = "GATEWAY-WEB-权限相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.GATEWAY_API_V1_PREFIX)
public class GatewaySecurityController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewaySecurityController.class);

    @Autowired
    private SecurityService securityService;

    @ApiLevel(level = ApiLevelContent.LEVEL_VIP_1)
    @ApiOperation(value = "Kafka用户查询", notes = "")
    @RequestMapping(value = "security/users", method = RequestMethod.POST)
    @ResponseBody
    public DeprecatedResponseResult<String> getKafkaUsers(@RequestBody KafkaUserSearchDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return DeprecatedResponseResult.failure("invalid request");
        }

        try {
            List<KafkaUserDO> doList = securityService.getKafkaUsers(
                    dto.getStart(),
                    dto.getEnd().equals(0L)? System.currentTimeMillis(): dto.getEnd()
            );
            if (ValidateUtils.isEmptyList(doList)) {
                return DeprecatedResponseResult.success();
            }

            KafkaSecurityVO vo = new KafkaSecurityVO();
            vo.setRows(new ArrayList<>(GatewayModelConverter.convert2KafkaUserVOList(doList)));
            return DeprecatedResponseResult.success(JSON.toJSONString(vo));
        } catch (Exception e) {
            LOGGER.error("get kafka users failed, req:{}.", dto, e);
            return DeprecatedResponseResult.failure("get kafka users exception");
        }
    }

    @ApiLevel(level = ApiLevelContent.LEVEL_IMPORTANT_2)
    @ApiOperation(value = "Kafka用户权限查询", notes = "")
    @RequestMapping(value = "security/acls", method = RequestMethod.POST)
    @ResponseBody
    public DeprecatedResponseResult<String> getKafkaAcls(@RequestBody KafkaAclSearchDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return DeprecatedResponseResult.failure("invalid request");
        }

        try {
            List<KafkaAclDO> doList = securityService.getKafkaAcls(
                    dto.getClusterId(),
                    dto.getStart(),
                    dto.getEnd().equals(0L)? System.currentTimeMillis(): dto.getEnd()
            );
            if (ValidateUtils.isEmptyList(doList)) {
                return DeprecatedResponseResult.success();
            }

            KafkaSecurityVO vo = new KafkaSecurityVO();
            vo.setRows(new ArrayList<>(GatewayModelConverter.convert2KafkaAclVOList(doList)));
            return DeprecatedResponseResult.success(JSON.toJSONString(vo));
        } catch (Exception e) {
            LOGGER.error("get kafka acls failed, req:{}.", dto, e);
            return DeprecatedResponseResult.failure("get kafka acls exception");
        }
    }
}