package com.xiaojukeji.know.streaming.km.core.service.acl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.acl.ACLAtomParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.core.service.acl.OpKafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.KafkaAclDAO;
import kafka.security.authorizer.AclAuthorizer;
import kafka.security.authorizer.AclEntry;
import kafka.zk.KafkaZkClient;
import kafka.zk.ZkVersion;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.acl.*;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import scala.jdk.javaapi.CollectionConverters;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;


@Service
public class OpKafkaAclServiceImpl extends BaseKafkaVersionControlService implements OpKafkaAclService {
    private static final ILog log = LogFactory.getLog(OpKafkaAclServiceImpl.class);

    private static final String ACL_CREATE            = "createKafkaAcl";

    private static final String ACL_DELETE            = "deleteKafkaAcl";

    @Autowired
    private KafkaAclDAO kafkaAclDAO;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.SERVICE_OP_ACL;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(ACL_CREATE,      V_0_10_0_0, V_2_7_2, "createAclByZKClient",       this::createAclByZKClient);
        registerVCHandler(ACL_CREATE,      V_2_7_2, V_MAX,      "createAclByKafkaClient",    this::createAclByKafkaClient);

        registerVCHandler(ACL_DELETE,      V_0_10_0_0, V_2_7_2, "deleteAclByZKClient",       this::deleteAclByZKClient);
        registerVCHandler(ACL_DELETE,      V_2_7_2, V_MAX,      "deleteAclByKafkaClient",    this::deleteAclByKafkaClient);
    }

    @Override
    public Result<Void> createKafkaAcl(ACLAtomParam aclAtomParam, String operator) {
        if (ValidateUtils.anyNull(aclAtomParam, operator)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        // 检查参数
        Result<Void> rv = aclAtomParam.checkFieldLegal();
        if (rv.failed()) {
            return rv;
        }

        // 创建ACL
        try {
            rv = (Result<Void>) versionControlService.doHandler(getVersionItemType(), getMethodName(aclAtomParam.getClusterPhyId(), ACL_CREATE), aclAtomParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }

        if (rv == null || rv.failed()) {
            // 创建ACL失败
            return rv;
        }

        KafkaAclPO po = KafkaAclConverter.convert2KafkaAclPO(aclAtomParam);

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.REPLACE.getDesc(),
                ModuleEnum.KAFKA_ACL.getDesc(),
                po.getUniqueField(),
                ConvertUtil.obj2Json(po)
        ));

        // 创建ACL成功，则将ACL信息写DB
        rv = this.insertAndIgnoreDuplicate(po);
        if (rv.failed()) {
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, "创建Kafka中的ACL信息成功，但是写入DB失败");
        }

        return rv;
    }

    @Override
    public Result<Void> deleteKafkaAcl(ACLAtomParam aclAtomParam, String operator) {
        if (ValidateUtils.anyNull(aclAtomParam, operator)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        // 检查参数
        Result<Void> rv = aclAtomParam.checkFieldLegal();
        if (rv.failed()) {
            return rv;
        }

        // 删除ACL
        try {
            rv = (Result<Void>) versionControlService.doHandler(getVersionItemType(), getMethodName(aclAtomParam.getClusterPhyId(), ACL_DELETE), aclAtomParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }

        if (rv == null || rv.failed()) {
            // 删除ACL失败
            return rv;
        }

        KafkaAclPO po = KafkaAclConverter.convert2KafkaAclPO(aclAtomParam);

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.DELETE.getDesc(),
                ModuleEnum.KAFKA_ACL.getDesc(),
                po.getUniqueField(),
                ConvertUtil.obj2Json(po)
        ));

        // 删除ACL成功，但是将ACL信息从DB中删除失败
        rv = this.deleteInDB(po);
        if (rv.failed()) {
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, "删除Kafka中的ACL成功，但是删除DB中的数据失败");
        }

        return rv;
    }

    @Override
    public Result<Void> insertAndIgnoreDuplicate(KafkaAclPO kafkaAclPO) {
        try {
            kafkaAclDAO.insert(kafkaAclPO);

            return Result.buildSuc();
        } catch (DuplicateKeyException dke) {
            // 直接写入，如果出现key冲突则直接忽略，因为key冲突时，表示该数据已完整存在，不需要替换任何数据
            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=insertAndIgnoreDuplicate||kafkaAclPO={}||errMsg=exception", kafkaAclPO, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    /**************************************************** private method ****************************************************/

    private Result<Void> deleteInDB(KafkaAclPO kafkaAclPO) {
        try {
            LambdaQueryWrapper<KafkaAclPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(KafkaAclPO::getUniqueField, kafkaAclPO.getUniqueField());
            kafkaAclDAO.delete(lambdaQueryWrapper);

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=deleteInDB||kafkaAclPO={}||errMsg=exception", kafkaAclPO, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> createAclByZKClient(VersionItemParam itemParam) {
        return updateAclByZKClient((ACLAtomParam) itemParam, false);
    }

    private Result<Void> createAclByKafkaClient(VersionItemParam itemParam) {
        ACLAtomParam param = (ACLAtomParam) itemParam;

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            CreateAclsResult createAclsResult = adminClient.createAcls(
                    Arrays.asList(new AclBinding(new ResourcePattern(param.getResourceType(), param.getResourceName(), param.getResourcePatternType()),
                            new AccessControlEntry(
                                    new KafkaPrincipal(KafkaPrincipal.USER_TYPE, param.getKafkaUserName()).toString(),
                                    param.getAclClientHost(),
                                    param.getAclOperation(),
                                    param.getAclPermissionType()
                            )
                    )),
                    new CreateAclsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );
            createAclsResult.all().get();

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=createAclByKafkaClient||parma={}||errMsg={}", itemParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> deleteAclByZKClient(VersionItemParam itemParam) {
        // 删除
        return this.updateAclByZKClient((ACLAtomParam) itemParam, true);
    }

    private Result<Void> deleteAclByKafkaClient(VersionItemParam itemParam) {
        ACLAtomParam param = (ACLAtomParam) itemParam;

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            // 构造需要删除的权限
            AclBindingFilter aclBindingFilter = new AclBindingFilter(
                    new ResourcePatternFilter(param.getResourceType(), param.getResourceName(), param.getResourcePatternType()),
                    new AccessControlEntryFilter(
                            new KafkaPrincipal(KafkaPrincipal.USER_TYPE, param.getKafkaUserName()).toString(),
                            param.getAclClientHost(),
                            param.getAclOperation(),
                            param.getAclPermissionType()
                    )
            );

            DeleteAclsResult deleteAclsResult = adminClient.deleteAcls(Arrays.asList(aclBindingFilter), new DeleteAclsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            deleteAclsResult.all().get();

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=deleteAclByKafkaClient||parma={}||errMsg={}", itemParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> updateAclByZKClient(ACLAtomParam aclAtomParam, boolean deleteAcl) {
        // 资源
        ResourcePattern resourcePattern = new ResourcePattern(
                aclAtomParam.getResourceType(),
                aclAtomParam.getResourceName(),
                aclAtomParam.getResourcePatternType()
        );

        // 权限
        AclEntry aclEntry = new AclEntry(new AccessControlEntry(
                new KafkaPrincipal(KafkaPrincipal.USER_TYPE, aclAtomParam.getKafkaUserName()).toString(),
                aclAtomParam.getAclClientHost(),
                aclAtomParam.getAclOperation(),
                aclAtomParam.getAclPermissionType()
        ));

        // 当前AclAuthorizer中有现成的方法，但是AclAuthorizer会加载集群的ACL信息，会比较重
        // ZK客户端无现成可用方法，因此基于ZK客户端提供出来的方法，自定义实现操作ACL方法
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(aclAtomParam.getClusterPhyId());

            // 创建ACL相关的ZK节点
            kafkaZkClient.createAclPaths();

            Result<Boolean> rb = this.updateZKAcl(kafkaZkClient, resourcePattern, aclEntry, deleteAcl);
            if (rb.failed() || !rb.hasData()) {
                return Result.buildFromIgnoreData(rb);
            }

            if (!rb.getData()) {
                // ACL操作失败
                return deleteAcl? Result.buildFailure("删除ACL失败"): Result.buildFailure("创建ACL失败");
            }
        } catch (Exception e) {
            log.error("method=updateAclByZKClient||parma={}||deleteAcl={}||msg=update acl failed||errMsg={}", aclAtomParam, deleteAcl, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }

        // 操作成功的情况下，再尝试创建change节点
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(aclAtomParam.getClusterPhyId());

            kafkaZkClient.createAclChangeNotification(resourcePattern);
        } catch (Exception e) {
            log.warn("method=updateAclByZKClient||parma={}||deleteAcl={}||msg=create change node failed||errMsg={}", aclAtomParam, deleteAcl, e.getMessage());

            return deleteAcl? Result.buildFailure("删除ACL成功，但是创建Change节点失败"): Result.buildFailure("创建ACL成功，但是创建Change节点失败");
        }

        return Result.buildSuc();
    }

    private Result<Boolean> updateZKAcl(KafkaZkClient kafkaZkClient, ResourcePattern resourcePattern, AclEntry aclEntry, boolean deleteAcl) {
        // 获取当前资源的ACL信息
        AclAuthorizer.VersionedAcls versionedAcls = kafkaZkClient.getVersionedAclsForResource(resourcePattern);

        Set<AclEntry> aclEntrySet = new HashSet<>(CollectionConverters.asJava(versionedAcls.acls()));

        if ((deleteAcl && !aclEntrySet.contains(aclEntry))
            || (!deleteAcl && aclEntrySet.contains(aclEntry))) {
            // 删除时，不存在该ACL则直接返回成功
            // 创建时，已存在该ACL则直接返回成功
            return Result.buildSuc(Boolean.TRUE);
        }

        if (deleteAcl) {
            // 移除当前ACL
            aclEntrySet.remove(aclEntry);
        } else {
            // 增加当前ACL
            aclEntrySet.add(aclEntry);
        }

        if (aclEntrySet.isEmpty()) {
            // 如果变更后为空，则删除节点
            return kafkaZkClient.deleteResource(resourcePattern)? Result.buildSuc(Boolean.TRUE): Result.buildSuc(Boolean.FALSE);
        } else if (ZkVersion.UnknownVersion() == versionedAcls.zkVersion()) {
            // 节点不存在
            return (boolean) kafkaZkClient.createAclsForResourceIfNotExists(
                    resourcePattern,
                    CollectionConverters.asScala(aclEntrySet).toSet()
            )._1()? Result.buildSuc(Boolean.TRUE): Result.buildSuc(Boolean.FALSE);
        } else {
            // 如果变更后非空，则更新节点
            return (boolean) kafkaZkClient.conditionalSetAclsForResource(
                    resourcePattern,
                    CollectionConverters.asScala(aclEntrySet).toSet(),
                    versionedAcls.zkVersion()
            )._1() ? Result.buildSuc(Boolean.TRUE): Result.buildSuc(Boolean.FALSE);
        }
    }
}
