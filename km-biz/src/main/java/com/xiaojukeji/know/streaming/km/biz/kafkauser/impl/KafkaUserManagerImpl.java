package com.xiaojukeji.know.streaming.km.biz.kafkauser.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.util.PWEncryptUtil;
import com.xiaojukeji.know.streaming.km.biz.kafkauser.KafkaUserManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.kafkauser.ClusterKafkaUserTokenDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkauser.KafkaUser;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserReplaceParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaUserPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser.KafkaUserTokenVO;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaUserVOConverter;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.AESUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.kafkauser.KafkaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaUserManagerImpl implements KafkaUserManager {
    private static final ILog log = LogFactory.getLog(KafkaUserManagerImpl.class);

    @Autowired
    private KafkaUserService kafkaUserService;

    @Override
    public Result<Void> createKafkaUserWithTokenEncrypted(ClusterKafkaUserTokenDTO dto, String operator) {
        if (!ClusterAuthTypeEnum.isScram(dto.getAuthType())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "不支持该认证方式");
        }

        String rawToken = AESUtils.decrypt(dto.getToken());
        if (rawToken == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "KafkaUser密钥解密失败");
        }

        return kafkaUserService.createKafkaUser(new KafkaUserReplaceParam(dto.getClusterId(), dto.getKafkaUser(), rawToken), operator);
    }

    @Override
    public Result<Void> modifyKafkaUserWithTokenEncrypted(ClusterKafkaUserTokenDTO dto, String operator) {
        if (!ClusterAuthTypeEnum.isScram(dto.getAuthType())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "不支持该认证方式");
        }

        String rawToken = AESUtils.decrypt(dto.getToken());
        if (rawToken == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "KafkaUser密钥解密失败");
        }

        return kafkaUserService.modifyKafkaUser(new KafkaUserReplaceParam(dto.getClusterId(), dto.getKafkaUser(), rawToken), operator);
    }

    @Override
    public Result<KafkaUserTokenVO> getKafkaUserTokenWithEncrypt(Long clusterPhyId, String kafkaUser) {
        Result<KafkaUserTokenVO> voResult = this.getKafkaUserToken(clusterPhyId, kafkaUser);
        if (voResult.failed() || ValidateUtils.isNull(voResult.getData().getToken())) {
            // 获取失败 或 无密钥信息，则直接返回
            return voResult;
        }

        // 对Token进行加密
        voResult.getData().setToken(AESUtils.encrypt(voResult.getData().getToken()));
        return voResult;
    }

    /**************************************************** private method ****************************************************/

    private Result<KafkaUserTokenVO> getKafkaUserToken(Long clusterPhyId, String kafkaUser) {
        Result<KafkaUser> kafkaUserResult = kafkaUserService.getKafkaUserFromKafka(clusterPhyId, kafkaUser);
        if (kafkaUserResult.failed()) {
            return Result.buildFromIgnoreData(kafkaUserResult);
        }

        KafkaUserPO kafkaUserPO = kafkaUserService.getKafkaUserFromDB(clusterPhyId, kafkaUser);
        if (kafkaUserPO == null) {
            // DB中无数据，则直接返回kafka中查询到的数据
            return Result.buildSuc(KafkaUserVOConverter.convert2KafkaUserTokenVO(kafkaUserResult.getData(), false, null));
        }

        try {
            String rawToken = PWEncryptUtil.decode(kafkaUserPO.getToken());

            if (kafkaUserService.isTokenEqual2CredentialProps(clusterPhyId, kafkaUserResult.getData().getProps(), rawToken)) {
                // 与DB中数据一致
                return Result.buildSuc(KafkaUserVOConverter.convert2KafkaUserTokenVO(kafkaUserResult.getData(), true, rawToken));
            } else {
                // 与DB中数据不一致
                return Result.buildSuc(KafkaUserVOConverter.convert2KafkaUserTokenVO(kafkaUserResult.getData(), false, rawToken));
            }
        } catch (Exception e) {
            // DB中数据不一致，则直接返回kafka中查询到的数据
            return Result.buildSuc(KafkaUserVOConverter.convert2KafkaUserTokenVO(kafkaUserResult.getData(), false, null));
        }
    }
}
