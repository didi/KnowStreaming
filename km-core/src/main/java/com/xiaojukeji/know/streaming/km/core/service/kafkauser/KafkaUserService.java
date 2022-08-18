package com.xiaojukeji.know.streaming.km.core.service.kafkauser;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkauser.KafkaUser;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserReplaceParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaUserPO;

import java.util.List;
import java.util.Properties;

public interface KafkaUserService {
    /**************************************************** operate DB and Kafka Method ****************************************************/

    /**
     * 新增KafkaUser
     */
    Result<Void> createKafkaUser(KafkaUserReplaceParam param, String operator);

    /**
     * 删除KafkaUser
     */
    Result<Void> deleteKafkaUser(KafkaUserParam param, String operator);

    /**
     * 修改KafkaUser
     */
    Result<Void> modifyKafkaUser(KafkaUserReplaceParam param, String operator);

    /**
     * 查询KafkaUser
     */
    Result<List<KafkaUser>> getKafkaUserFromKafka(Long clusterPhyId);

    /**
     * 查询KafkaUser
     */
    Result<KafkaUser> getKafkaUserFromKafka(Long clusterPhyId, String kafkaUser);

    Result<Properties> generateScramCredential(Long clusterPhyId, String token);

    boolean isTokenEqual2CredentialProps(Long clusterPhyId, Properties credentialProps, String token);

    /**************************************************** operate DB-Method ****************************************************/

    void batchReplaceKafkaUserInDB(Long clusterPhyId, List<String> kafkaUserList);

    PaginationResult<KafkaUserPO> pagingKafkaUserFromDB(Long clusterPhyId, PaginationBaseDTO dto);

    List<KafkaUserPO> getKafkaUserByClusterIdFromDB(Long clusterPhyId, String searchKafkaUserName);

    List<KafkaUserPO> getKafkaUserFromDB(Long clusterPhyId);

    KafkaUserPO getKafkaUserFromDB(Long clusterPhyId, String kafkaUser);

    Integer countKafkaUserFromDB(Long clusterPhyId);

    boolean checkExistKafkaUserFromDB(Long clusterPhyId, String kafkaUser);
}
