package com.xiaojukeji.know.streaming.km.core.service.kafkauser.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.util.PWEncryptUtil;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkauser.KafkaUser;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser.KafkaUserReplaceParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaUserPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.kafkauser.KafkaUserService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.KafkaUserDAO;
import kafka.admin.ConfigCommand;
import kafka.server.ConfigType;
import kafka.zk.*;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.security.scram.ScramCredential;
import org.apache.kafka.common.security.scram.internals.ScramCredentialUtils;
import org.apache.kafka.common.security.scram.internals.ScramFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import scala.jdk.javaapi.CollectionConverters;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;


@Service
public class KafkaUserServiceImpl extends BaseKafkaVersionControlService implements KafkaUserService {
    private static final ILog log  = LogFactory.getLog(KafkaUserServiceImpl.class);

    private static final String KAFKA_USER_REPLACE                  = "replaceKafkaUser";
    private static final String KAFKA_USER_DELETE                   = "deleteKafkaUser";
    private static final String KAFKA_USER_GET                      = "getKafkaUser";

    @Autowired
    private KafkaUserDAO kafkaUserDAO;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.SERVICE_OP_KAFKA_USER;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(KAFKA_USER_REPLACE,           V_0_10_1_0, V_2_7_2, "replaceKafkaUserByZKClient",          this::replaceKafkaUserByZKClient);
        registerVCHandler(KAFKA_USER_REPLACE,           V_2_7_2,    V_MAX,   "replaceKafkaUserByKafkaClient",       this::replaceKafkaUserByKafkaClient);

        registerVCHandler(KAFKA_USER_DELETE,            V_0_10_1_0, V_2_7_2, "deleteKafkaUserByZKClient",           this::deleteKafkaUserByZKClient);
        registerVCHandler(KAFKA_USER_DELETE,            V_2_7_2,    V_MAX,   "deleteKafkaUserByKafkaClient",        this::deleteKafkaUserByKafkaClient);

        registerVCHandler(KAFKA_USER_GET,               V_0_10_1_0, V_2_7_2, "getKafkaUserByZKClient",              this::getKafkaUserByZKClient);
        registerVCHandler(KAFKA_USER_GET,               V_2_7_2,    V_MAX,   "getKafkaUserByKafkaClient",           this::getKafkaUserByKafkaClient);
    }

    @Override
    public Result<Void> createKafkaUser(KafkaUserReplaceParam param, String operator) {
        if (this.checkExistKafkaUserFromDB(param.getClusterPhyId(), param.getKafkaUserName())) {
            // kafka-user已存在，则返回错误
            return Result.buildFromRSAndMsg(ResultStatus.DUPLICATION, MsgConstant.getKafkaUserDuplicate(param.getClusterPhyId(), param.getKafkaUserName()));
        }

        Result<Void> rv = null;

        // 修改Kafka
        try {
            rv = (Result<Void>) versionControlService.doHandler(getVersionItemType(), getMethodName(param.getClusterPhyId(), KAFKA_USER_REPLACE), param);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }

        if (rv == null || rv.failed()) {
            return rv;
        }

        // 写DB
        rv = this.replaceKafkaUserInDB(param.getClusterPhyId(), param.getKafkaUserName(), param.getKafkaUserToken());
        if (rv.failed()) {
            // rv中提示创建成功但是写DB失败
            rv = Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, "创建KafkaUser成功，但写入DB失败，错误信息: " + rv.getMessage());
        }

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.ADD.getDesc(),
                ModuleEnum.KAFKA_USER.getDesc(),
                MsgConstant.getKafkaUserBizStr(param.getClusterPhyId(), param.getKafkaUserName()),
                param.toString()
        ));

        // 返回结果
        return rv;
    }

    @Override
    public Result<Void> deleteKafkaUser(KafkaUserParam param, String operator) {
        // 查询DB判断是否存在
        if (!this.checkExistKafkaUserFromDB(param.getClusterPhyId(), param.getKafkaUserName())) {
            // kafka-user不存在，则返回错误
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getKafkaUserNotExist(param.getClusterPhyId(), param.getKafkaUserName()));
        }

        Result<Void> rv = null;

        // 修改Kafka
        try {
            rv = (Result<Void>) versionControlService.doHandler(getVersionItemType(), getMethodName(param.getClusterPhyId(), KAFKA_USER_DELETE), param);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }

        if (rv == null || rv.failed()) {
            return rv;
        }

        // 写DB
        rv = this.deleteKafkaUserInDB(param.getClusterPhyId(), param.getKafkaUserName());
        if (rv.failed()) {
            // rv中提示删除成功但是写DB失败
            rv = Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, "删除Kafka-User成功，但删除DB数据失败，错误信息: " + rv.getMessage());
        }

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.DELETE.getDesc(),
                ModuleEnum.KAFKA_USER.getDesc(),
                MsgConstant.getKafkaUserBizStr(param.getClusterPhyId(), param.getKafkaUserName()),
                param.toString()
        ));

        // 返回结果
        return rv;
    }

    @Override
    public Result<Void> modifyKafkaUser(KafkaUserReplaceParam param, String operator) {
        // 查询DB判断是否存在
        if (!this.checkExistKafkaUserFromDB(param.getClusterPhyId(), param.getKafkaUserName())) {
            // kafka-user不存在，则返回错误
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getKafkaUserNotExist(param.getClusterPhyId(), param.getKafkaUserName()));
        }

        Result<Void> rv = null;

        // 修改Kafka
        try {
            rv = (Result<Void>) versionControlService.doHandler(getVersionItemType(), getMethodName(param.getClusterPhyId(), KAFKA_USER_REPLACE), param);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }

        if (rv.failed()) {
            return rv;
        }

        // 写DB
        rv = this.replaceKafkaUserInDB(param.getClusterPhyId(), param.getKafkaUserName(), param.getKafkaUserToken());
        if (rv.failed()) {
            // rv中提示创建成功但是写DB失败
            rv = Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, "修改KafkaUser成功，但写入DB失败，错误信息: " + rv.getMessage());
        }

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.EDIT.getDesc(),
                ModuleEnum.KAFKA_USER.getDesc(),
                MsgConstant.getKafkaUserBizStr(param.getClusterPhyId(), param.getKafkaUserName()),
                param.toString()
        ));

        // 返回结果
        return rv;
    }

    @Override
    public Result<List<KafkaUser>> getKafkaUserFromKafka(Long clusterPhyId) {
        try {
            return (Result<List<KafkaUser>>) versionControlService.doHandler(
                    getVersionItemType(),
                    getMethodName(clusterPhyId, KAFKA_USER_GET),
                    new KafkaUserParam(clusterPhyId, null)
            );
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
    }

    @Override
    public Result<KafkaUser> getKafkaUserFromKafka(Long clusterPhyId, String kafkaUser) {
        try {
            Result<List<KafkaUser>> listResult = (Result<List<KafkaUser>>) versionControlService.doHandler(
                    getVersionItemType(),
                    getMethodName(clusterPhyId, KAFKA_USER_GET),
                    new KafkaUserParam(clusterPhyId, kafkaUser)
            );

            if (listResult.failed()) {
                return Result.buildFromIgnoreData(listResult);
            }

            if (ValidateUtils.isEmptyList(listResult.getData())) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getKafkaUserNotExist(clusterPhyId, kafkaUser));
            }

            return Result.buildSuc(listResult.getData().get(0));
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
    }

    @Override
    public Result<Properties> generateScramCredential(Long clusterPhyId, String token) {
        try {
            Properties props = new Properties();

            props.put(ScramMechanism.SCRAM_SHA_256.mechanismName(),
                    ScramCredentialUtils.credentialToString(new ScramFormatter(org.apache.kafka.common.security.scram.internals.ScramMechanism.SCRAM_SHA_256).generateCredential(token, ConfigCommand.DefaultScramIterations()))
            );

            props.put(ScramMechanism.SCRAM_SHA_512.mechanismName(),
                    ScramCredentialUtils.credentialToString(new ScramFormatter(org.apache.kafka.common.security.scram.internals.ScramMechanism.SCRAM_SHA_512).generateCredential(token, ConfigCommand.DefaultScramIterations()))
            );

            return Result.buildSuc(props);
        } catch (Exception e) {
            log.error("method=generateScramCredential||clusterPhyId={}||token={}||errMsg=exception", clusterPhyId, token, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public boolean isTokenEqual2CredentialProps(Long clusterPhyId, Properties credentialProps, String token) {
        try {
            boolean existScram = false;
            boolean existNotEqual = false;
            for (ScramMechanism scramMechanism: ScramMechanism.values()) {
                String value = credentialProps.getProperty(scramMechanism.mechanismName());
                if (ValidateUtils.isBlank(value)) {
                    continue;
                }

                existScram = true;
                ScramCredential rawScramCredential = ScramCredentialUtils.credentialFromString(value);

                ScramFormatter scramFormatter = ScramMechanism.SCRAM_SHA_256.equals(scramMechanism)?
                        new ScramFormatter(org.apache.kafka.common.security.scram.internals.ScramMechanism.SCRAM_SHA_256):
                        new ScramFormatter(org.apache.kafka.common.security.scram.internals.ScramMechanism.SCRAM_SHA_512);

                ScramCredential tokenScramCredential = scramFormatter.generateCredential(
                        rawScramCredential.salt(),
                        scramFormatter.saltedPassword(token, rawScramCredential.salt(), rawScramCredential.iterations()),
                        rawScramCredential.iterations()
                );

                if (!ScramCredentialUtils.credentialToString(rawScramCredential).equals(ScramCredentialUtils.credentialToString(tokenScramCredential))) {
                    // 存在不相等的情况
                    existNotEqual = true;
                    break;
                }
            }

            // 存在，并且没有不相等的情况，这返回true
            return existScram && !existNotEqual;
        } catch (Exception e) {
            log.error("method=isTokenEqual2CredentialProps||clusterPhyId={}||credentialProps={}||token={}||errMsg=exception", clusterPhyId, credentialProps, token, e);

            return false;
        }
    }


    /**************************************************** operate DB-Method ****************************************************/

    @Override
    public void batchReplaceKafkaUserInDB(Long clusterPhyId, List<String> kafkaUserList) {
        Map<String, KafkaUserPO> poMap = this.getKafkaUserFromDB(clusterPhyId).stream().collect(Collectors.toMap(KafkaUserPO::getName, Function.identity()));

        for (String kafkaUser: kafkaUserList) {
            KafkaUserPO kafkaUserPO = poMap.remove(kafkaUser);
            if (kafkaUserPO != null) {
                // 已存在，则略过
                continue;
            }

            // 不存时则插入
            try {
                kafkaUserPO = new KafkaUserPO();
                kafkaUserPO.setClusterPhyId(clusterPhyId);
                kafkaUserPO.setName(kafkaUser);
                kafkaUserDAO.insert(kafkaUserPO);
            } catch (DuplicateKeyException dke) {
                // ignore
            }
        }

        // 删除不存在的
        for (KafkaUserPO kafkaUserPO: poMap.values()) {
            kafkaUserDAO.deleteById(kafkaUserPO.getClusterPhyId());
        }
    }

    @Override
    public PaginationResult<KafkaUserPO> pagingKafkaUserFromDB(Long clusterPhyId, PaginationBaseDTO dto) {
        LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.like(!ValidateUtils.isBlank(dto.getSearchKeywords()), KafkaUserPO::getName, dto.getSearchKeywords());
        lambdaQueryWrapper.orderByDesc(KafkaUserPO::getCreateTime);

        Page<KafkaUserPO> poPage = kafkaUserDAO.selectPage(new Page<>(dto.getPageNo(), dto.getPageSize()), lambdaQueryWrapper);

        return PaginationResult.buildSuc(poPage.getRecords(), poPage);
    }

    @Override
    public List<KafkaUserPO> getKafkaUserByClusterIdFromDB(Long clusterPhyId, String searchKafkaUserName) {
        LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.like(!ValidateUtils.isBlank(searchKafkaUserName), KafkaUserPO::getName, searchKafkaUserName);

        return kafkaUserDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<KafkaUserPO> getKafkaUserFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);

        return kafkaUserDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public KafkaUserPO getKafkaUserFromDB(Long clusterPhyId, String kafkaUser) {
        LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(KafkaUserPO::getName, kafkaUser);

        return kafkaUserDAO.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Integer countKafkaUserFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);

        return kafkaUserDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public boolean checkExistKafkaUserFromDB(Long clusterPhyId, String kafkaUser) {
        LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(KafkaUserPO::getName, kafkaUser);

        return kafkaUserDAO.selectCount(lambdaQueryWrapper) > 0;
    }


    /**************************************************** private method ****************************************************/


    private Result<Void> replaceKafkaUserInDB(Long clusterPhyId, String kafkaUser, String rowToken) {
        try {
            boolean exist = true;

            KafkaUserPO kafkaUserPO = this.getKafkaUserFromDB(clusterPhyId, kafkaUser);
            if (kafkaUserPO == null) {
                kafkaUserPO = new KafkaUserPO();
                exist = false;
            }

            kafkaUserPO.setClusterPhyId(clusterPhyId);
            kafkaUserPO.setName(kafkaUser);
            kafkaUserPO.setToken(PWEncryptUtil.encode(rowToken));

            if (!exist) {
                kafkaUserDAO.insert(kafkaUserPO);
            } else {
                kafkaUserDAO.updateById(kafkaUserPO);
            }

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=insertKafkaUserToDB||clusterPhyId={}||kafkaUser={}||errMsg=exception.", clusterPhyId, kafkaUser, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> deleteKafkaUserInDB(Long clusterPhyId, String kafkaUser) {
        try {
            LambdaQueryWrapper<KafkaUserPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(KafkaUserPO::getClusterPhyId, clusterPhyId);
            lambdaQueryWrapper.eq(KafkaUserPO::getName, kafkaUser);

            kafkaUserDAO.delete(lambdaQueryWrapper);

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=deleteKafkaUserInDB||clusterPhyId={}||kafkaUser={}||errMsg=exception.", clusterPhyId, kafkaUser, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> replaceKafkaUserByZKClient(VersionItemParam itemParam) {
        KafkaUserReplaceParam param = (KafkaUserReplaceParam) itemParam;
        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(param.getClusterPhyId());
            Properties properties = adminZkClient.fetchEntityConfig(ConfigType.User(), param.getKafkaUserName());

            // 没有开放的接口，因此ZK的需要自己进行实现
            properties.put(ScramMechanism.SCRAM_SHA_256.mechanismName(),
                    ScramCredentialUtils.credentialToString(new ScramFormatter(org.apache.kafka.common.security.scram.internals.ScramMechanism.SCRAM_SHA_256).generateCredential(param.getKafkaUserToken(), ConfigCommand.DefaultScramIterations()))
            );
            properties.put(ScramMechanism.SCRAM_SHA_512.mechanismName(),
                    ScramCredentialUtils.credentialToString(new ScramFormatter(org.apache.kafka.common.security.scram.internals.ScramMechanism.SCRAM_SHA_512).generateCredential(param.getKafkaUserToken(), ConfigCommand.DefaultScramIterations()))
            );

            adminZkClient.changeConfigs(ConfigType.User(), param.getKafkaUserName(), properties);
            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=replaceKafkaUserByZKClient||parma={}||errMsg={}", itemParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> replaceKafkaUserByKafkaClient(VersionItemParam itemParam) {
        KafkaUserReplaceParam param = (KafkaUserReplaceParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());
            AlterUserScramCredentialsResult alterUserScramCredentialsResult = adminClient.alterUserScramCredentials(
                    Arrays.asList(
                            // scram-256
                            new UserScramCredentialUpsertion(
                                param.getKafkaUserName(),
                                new ScramCredentialInfo(ScramMechanism.SCRAM_SHA_256, ConfigCommand.DefaultScramIterations()),
                                param.getKafkaUserToken().getBytes(StandardCharsets.UTF_8),
                                ScramFormatter.secureRandomBytes(new SecureRandom())),

                            // scram-512
                            new UserScramCredentialUpsertion(
                                    param.getKafkaUserName(),
                                    new ScramCredentialInfo(ScramMechanism.SCRAM_SHA_512, ConfigCommand.DefaultScramIterations()),
                                    param.getKafkaUserToken().getBytes(StandardCharsets.UTF_8),
                                    ScramFormatter.secureRandomBytes(new SecureRandom()))
                    ),
                    new AlterUserScramCredentialsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );
            alterUserScramCredentialsResult.all().get();

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=replaceKafkaUserByKafkaClient||parma={}||errMsg={}", itemParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> deleteKafkaUserByZKClient(VersionItemParam itemParam) {
        KafkaUserParam param = (KafkaUserParam) itemParam;
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(param.getClusterPhyId());

            // 删除kafka-user
            kafkaZkClient.deletePath(ConfigEntityZNode.path(ConfigType.User(), param.getKafkaUserName()), ZkVersion.MatchAnyVersion(), false);

            kafkaZkClient.createConfigChangeNotification(ConfigType.User() + "/" + param.getKafkaUserName());
            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=deleteKafkaUserByZKClient||parma={}||errMsg={}", itemParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> deleteKafkaUserByKafkaClient(VersionItemParam itemParam) {
        KafkaUserParam param = (KafkaUserParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());
            AlterUserScramCredentialsResult alterUserScramCredentialsResult = adminClient.alterUserScramCredentials(
                    Arrays.asList(
                            // scram-256
                            new UserScramCredentialDeletion(
                                    param.getKafkaUserName(),
                                    ScramMechanism.SCRAM_SHA_256),

                            // scram-512
                            new UserScramCredentialDeletion(
                                    param.getKafkaUserName(),
                                    ScramMechanism.SCRAM_SHA_512)
                    ),
                    new AlterUserScramCredentialsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );
            alterUserScramCredentialsResult.all().get();

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=deleteKafkaUserByKafkaClient||parma={}||errMsg={}", itemParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<KafkaUser>> getKafkaUserByZKClient(VersionItemParam itemParam) {
        KafkaUserParam param = (KafkaUserParam) itemParam;
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(param.getClusterPhyId());

            List<String> kafkaUserNameList = null;
            if (ValidateUtils.isBlank(param.getKafkaUserName())) {
                kafkaUserNameList = CollectionConverters.asJava(kafkaZkClient.getChildren(ConfigEntityTypeZNode.path(ConfigType.User())));
            } else {
                kafkaUserNameList = Arrays.asList(param.getKafkaUserName());
            }

            List<KafkaUser> kafkaUserList = new ArrayList<>();
            for (String kafkaUser: kafkaUserNameList) {
                Properties properties = kafkaZkClient.getEntityConfigs(ConfigType.User(), param.getKafkaUserName());

                kafkaUserList.add(new KafkaUser(param.getClusterPhyId(), kafkaUser, null, properties));
            }

            return Result.buildSuc(kafkaUserList);
        } catch (Exception e) {
            log.error("method=getKafkaUserByZKClient||clusterPhyId={}||kafkaUser={}||errMsg=exception", param.getClusterPhyId(), param.getKafkaUserName(), e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<KafkaUser>> getKafkaUserByKafkaClient(VersionItemParam itemParam) {
        KafkaUserParam param = (KafkaUserParam) itemParam;
        try {
            // 获取集群
            ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(param.getClusterPhyId());
            if (clusterPhy == null) {
                return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(param.getClusterPhyId()));
            }

            // 判断认证模式，如果是非scram模式，直接返回
            if (!ClusterAuthTypeEnum.isScram(clusterPhy.getAuthType())) {
                log.warn("method=getKafkaUserByKafkaClient||clusterPhyId={}||msg=not scram auth type and ignore get users", clusterPhy.getId());
                return Result.buildSuc(new ArrayList<>());
            }

            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            // 查询集群kafka-user
            DescribeUserScramCredentialsResult describeUserScramCredentialsResult = null;
            if (ValidateUtils.isBlank(param.getKafkaUserName())) {
                describeUserScramCredentialsResult = adminClient.describeUserScramCredentials();
            } else {
                describeUserScramCredentialsResult = adminClient.describeUserScramCredentials(
                        Arrays.asList(param.getKafkaUserName()),
                        new DescribeUserScramCredentialsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                );
            }

            Map<String, UserScramCredentialsDescription> descriptionMap = describeUserScramCredentialsResult.all().get();

            List<KafkaUser> kafkaUserList = new ArrayList<>();
            for (Map.Entry<String, UserScramCredentialsDescription> entry: descriptionMap.entrySet()) {
                kafkaUserList.add(new KafkaUser(param.getClusterPhyId(), entry.getKey(), null, new Properties()));
            }

            return Result.buildSuc(kafkaUserList);
        } catch (Exception e) {
            log.error("method=getKafkaUserByKafkaClient||clusterPhyId={}||kafkaUser={}||errMsg=exception", param.getClusterPhyId(), param.getKafkaUserName(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }
}
