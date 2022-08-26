package com.xiaojukeji.know.streaming.km.biz.kafkauser;


import com.xiaojukeji.know.streaming.km.common.bean.dto.kafkauser.ClusterKafkaUserTokenDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser.KafkaUserTokenVO;

public interface KafkaUserManager {
    /**
     * 新增KafkaUser
     */
    Result<Void> createKafkaUserWithTokenEncrypted(ClusterKafkaUserTokenDTO dto, String operator);

    /**
     * 修改KafkaUser
     */
    Result<Void> modifyKafkaUserWithTokenEncrypted(ClusterKafkaUserTokenDTO dto, String operator);

    /**
     * 查看密码
     */
    Result<KafkaUserTokenVO> getKafkaUserTokenWithEncrypt(Long clusterPhyId, String kafkaUser);
}
