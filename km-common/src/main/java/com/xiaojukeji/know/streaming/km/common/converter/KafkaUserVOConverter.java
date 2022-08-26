package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkauser.KafkaUser;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaUserPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser.KafkaUserTokenVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser.KafkaUserVO;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;

import java.util.List;

public class KafkaUserVOConverter {
    private KafkaUserVOConverter() {
    }

    public static KafkaUserTokenVO convert2KafkaUserTokenVO(KafkaUser kafkaUser, boolean decrypt, String rawToken) {
        KafkaUserTokenVO vo = new KafkaUserTokenVO();
        vo.setClusterId(kafkaUser.getClusterPhyId());
        vo.setName(kafkaUser.getName());
        vo.setToken(decrypt? rawToken: kafkaUser.getCredentialString());
        vo.setAuthType(ClusterAuthTypeEnum.SASL_SCRAM.getAuthType());
        vo.setAuthName(ClusterAuthTypeEnum.SASL_SCRAM.getAuthName());
        vo.setDecrypt(decrypt);
        return vo;
    }

    public static List<KafkaUserVO> convert2KafkaUserVOList(Long clusterPhyId, List<KafkaUserPO> poList) {
        List<KafkaUserVO> voList = ConvertUtil.list2List(poList, KafkaUserVO.class);

        voList.stream().forEach(elem -> {
            elem.setClusterId(clusterPhyId);
            elem.setAuthName(ClusterAuthTypeEnum.SASL_SCRAM.getAuthName());
            elem.setAuthType(ClusterAuthTypeEnum.SASL_SCRAM.getAuthType());
        });

        return voList;
    }
}
