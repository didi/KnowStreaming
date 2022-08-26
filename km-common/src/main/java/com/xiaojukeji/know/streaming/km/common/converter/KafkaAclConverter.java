package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.dto.acl.AclAtomDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.acl.ACLAtomParam;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.acl.AclBindingVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KafkaAclConverter {
    private KafkaAclConverter() {
    }

    public static List<AclBindingVO> convert2AclBindingVOList(List<KafkaAclPO> poList) {
        List<AclBindingVO> voList = new ArrayList<>();
        for (KafkaAclPO po: poList) {
            voList.add(convert2AclBindingVO(po));
        }
        return voList;
    }

    public static AclBindingVO convert2AclBindingVO(KafkaAclPO po) {
        AclBindingVO aclBindingVO = new AclBindingVO();
        aclBindingVO.setKafkaUser(po.getPrincipal());
        aclBindingVO.setAclOperation(po.getOperation());
        aclBindingVO.setAclPermissionType(po.getPermissionType());
        aclBindingVO.setAclClientHost(po.getHost());
        aclBindingVO.setResourceType(po.getResourceType());
        aclBindingVO.setResourceName(po.getResourceName());
        aclBindingVO.setResourcePatternType(po.getPatternType());
        return aclBindingVO;
    }

    public static KafkaAclPO convert2KafkaAclPO(Long clusterPhyId, AclBinding aclBinding, Long updateTime) {
        KafkaAclPO aclPO = new KafkaAclPO();
        aclPO.setClusterPhyId(clusterPhyId);
        aclPO.setPrincipal(aclBinding.entry().principal());
        aclPO.setOperation(aclBinding.entry().operation().ordinal());
        aclPO.setPermissionType(aclBinding.entry().permissionType().ordinal());
        aclPO.setHost(aclBinding.entry().host());
        aclPO.setResourceType(aclBinding.pattern().resourceType().ordinal());
        aclPO.setResourceName(aclBinding.pattern().name());
        aclPO.setPatternType(aclBinding.pattern().patternType().ordinal());
        aclPO.initUniqueField();
        aclPO.setUpdateTime(new Date(updateTime));
        return aclPO;
    }

    public static ACLAtomParam convert2ACLAtomParam(AclAtomDTO dto) {
        ACLAtomParam aclAtomParam = new ACLAtomParam();
        aclAtomParam.setClusterPhyId(dto.getClusterId());
        aclAtomParam.setKafkaUserName(dto.getKafkaUser().startsWith(Constant.KAFKA_PRINCIPAL_PREFIX)? dto.getKafkaUser().substring(Constant.KAFKA_PRINCIPAL_PREFIX.length()): dto.getKafkaUser());
        aclAtomParam.setAclOperation(AclOperation.fromCode(dto.getAclOperation().byteValue()));
        aclAtomParam.setAclPermissionType(AclPermissionType.fromCode(dto.getAclPermissionType().byteValue()));
        aclAtomParam.setAclClientHost(dto.getAclClientHost());
        aclAtomParam.setResourceType(ResourceType.fromCode(dto.getResourceType().byteValue()));
        aclAtomParam.setResourceName(dto.getResourceName());
        aclAtomParam.setResourcePatternType(PatternType.fromCode(dto.getResourcePatternType().byteValue()));
        return aclAtomParam;
    }

    public static KafkaAclPO convert2KafkaAclPO(ACLAtomParam aclAtomParam) {
        KafkaAclPO po = new KafkaAclPO();
        po.setClusterPhyId(aclAtomParam.getClusterPhyId());
        if (!aclAtomParam.getKafkaUserName().startsWith(Constant.KAFKA_PRINCIPAL_PREFIX)) {
            po.setPrincipal(Constant.KAFKA_PRINCIPAL_PREFIX + aclAtomParam.getKafkaUserName());
        } else {
            po.setPrincipal(aclAtomParam.getKafkaUserName());
        }

        po.setOperation(aclAtomParam.getAclOperation().ordinal());
        po.setPermissionType(aclAtomParam.getAclPermissionType().ordinal());
        po.setHost(aclAtomParam.getAclClientHost());
        po.setResourceType(aclAtomParam.getResourceType().ordinal());
        po.setResourceName(aclAtomParam.getResourceName());
        po.setPatternType(aclAtomParam.getResourcePatternType().ordinal());
        po.initUniqueField();
        return po;
    }
}
