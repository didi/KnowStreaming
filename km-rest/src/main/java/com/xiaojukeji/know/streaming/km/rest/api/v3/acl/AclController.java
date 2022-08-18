package com.xiaojukeji.know.streaming.km.rest.api.v3.acl;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.kafkaacl.KafkaAclManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.acl.AclAtomDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.core.service.acl.OpKafkaAclService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "ACL-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class AclController {
    @Autowired
    private KafkaAclManager kafkaAclManager;

    @Autowired
    private OpKafkaAclService opKafkaAclService;

    @ApiOperation(value = "KafkaACL创建", notes = "")
    @PostMapping(value ="kafka-acls/batch")
    @ResponseBody
    public Result<Void> createKafkaAcl(@Validated @RequestBody List<AclAtomDTO> dtoList) {
        return kafkaAclManager.batchCreateKafkaAcl(dtoList, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "KafkaACL删除", notes = "")
    @DeleteMapping(value ="kafka-acls")
    @ResponseBody
    public Result<Void> deleteKafkaAcl(@Validated @RequestBody AclAtomDTO dto) {
        return opKafkaAclService.deleteKafkaAcl(KafkaAclConverter.convert2ACLAtomParam(dto), HttpRequestUtil.getOperator());
    }
}
