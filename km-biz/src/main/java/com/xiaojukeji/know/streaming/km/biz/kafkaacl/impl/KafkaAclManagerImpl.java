package com.xiaojukeji.know.streaming.km.biz.kafkaacl.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.kafkaacl.KafkaAclManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.acl.AclAtomDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.acl.OpKafkaAclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaAclManagerImpl implements KafkaAclManager {
    private static final ILog log = LogFactory.getLog(KafkaAclManagerImpl.class);

    @Autowired
    private OpKafkaAclService opKafkaAclService;

    @Override
    public Result<Void> batchCreateKafkaAcl(List<AclAtomDTO> dtoList, String operator) {
        log.debug("method=batchCreateKafkaAcl||dtoList={}||operator={}", ConvertUtil.obj2Json(dtoList), operator);

        for (AclAtomDTO dto: dtoList) {
            Result<Void> rv = opKafkaAclService.createKafkaAcl(KafkaAclConverter.convert2ACLAtomParam(dto), operator);
            if (rv.failed()) {
                return rv;
            }
        }

        return Result.buildSuc();
    }
}
