package com.xiaojukeji.know.streaming.km.core.service.acl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterAuthTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.KafkaAclDAO;
import kafka.security.authorizer.AclAuthorizer;
import kafka.zk.KafkaZkClient;
import kafka.zk.ZkAclStore;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeAclsOptions;
import org.apache.kafka.clients.admin.DescribeAclsResult;
import org.apache.kafka.common.acl.*;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.apache.kafka.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import scala.jdk.javaapi.CollectionConverters;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;


@Service
public class KafkaAclServiceImpl extends BaseKafkaVersionControlService implements KafkaAclService {
    private static final ILog log = LogFactory.getLog(KafkaAclServiceImpl.class);

    private static final String ACL_GET_FROM_KAFKA    = "getAclFromKafka";

    @Autowired
    private KafkaAclDAO kafkaAclDAO;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.SERVICE_OP_ACL;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(ACL_GET_FROM_KAFKA,     V_0_10_0_0, V_2_8_0, "getAclByZKClient",          this::getAclByZKClient);
        registerVCHandler(ACL_GET_FROM_KAFKA,     V_2_8_0, V_MAX,      "getAclByKafkaClient",       this::getAclByKafkaClient);
    }

    @Override
    public Result<List<AclBinding>> getDataFromKafka(ClusterPhy clusterPhy) {
        try {
            Result<List<AclBinding>> dataResult = (Result<List<AclBinding>>) versionControlService.doHandler(getVersionItemType(), getMethodName(clusterPhy.getId(), ACL_GET_FROM_KAFKA), new ClusterPhyParam(clusterPhy.getId()));
            if (dataResult.failed()) {
                Result.buildFromIgnoreData(dataResult);
            }

            return Result.buildSuc(dataResult.getData());
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
    }

    @Override
    public void writeToDB(Long clusterPhyId, List<AclBinding> dataList) {
        Map<String, KafkaAclPO> dbPOMap = this.getKafkaAclFromDB(clusterPhyId).stream().collect(Collectors.toMap(KafkaAclPO::getUniqueField, Function.identity()));

        long now = System.currentTimeMillis();
        for (AclBinding aclBinding: dataList) {
            KafkaAclPO newPO = KafkaAclConverter.convert2KafkaAclPO(clusterPhyId, aclBinding, now);
            KafkaAclPO oldPO = dbPOMap.remove(newPO.getUniqueField());
            if (oldPO == null) {
                // 新增的ACL
                this.insertAndIgnoreDuplicate(newPO);
            }

            // 不需要update
        }

        // 删除已经不存在的
        for (KafkaAclPO dbPO: dbPOMap.values()) {
            kafkaAclDAO.deleteById(dbPO);
        }
    }

    @Override
    public int deleteInDBByKafkaClusterId(Long clusterPhyId) {
        LambdaQueryWrapper<KafkaAclPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);

        return kafkaAclDAO.delete(lambdaQueryWrapper);
    }

    @Override
    public List<KafkaAclPO> getKafkaAclFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<KafkaAclPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        queryWrapper.orderByDesc(KafkaAclPO::getUpdateTime);
        return kafkaAclDAO.selectList(queryWrapper);
    }

    @Override
    public Integer countKafkaAclFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<KafkaAclPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        return kafkaAclDAO.selectCount(queryWrapper);
    }

    @Override
    public Integer countResTypeAndDistinctFromDB(Long clusterPhyId, ResourceType resourceType) {
        LambdaQueryWrapper<KafkaAclPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(KafkaAclPO::getResourceType, resourceType.code());
        lambdaQueryWrapper.ne(KafkaAclPO::getResourceName, "*"); // 等于*的不做统计

        List<KafkaAclPO> poList = kafkaAclDAO.selectList(lambdaQueryWrapper);
        if (ValidateUtils.isEmptyList(poList)) {
            return 0;
        }

        return (int)poList.stream().map(KafkaAclPO::getResourceName).distinct().count();
    }

    @Override
    public Integer countKafkaUserAndDistinctFromDB(Long clusterPhyId) {
        LambdaQueryWrapper<KafkaAclPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.ne(KafkaAclPO::getPrincipal, new KafkaPrincipal(KafkaPrincipal.USER_TYPE, "*").toString()); // 等于*的不做统计

        List<KafkaAclPO> poList = kafkaAclDAO.selectList(lambdaQueryWrapper);
        if (ValidateUtils.isEmptyList(poList)) {
            return 0;
        }

        return (int)poList.stream().map(KafkaAclPO::getPrincipal).distinct().count();
    }

    @Override
    public List<KafkaAclPO> getTopicAclFromDB(Long clusterPhyId, String topicName) {
        // Topic自身 & Topic为*的情况
        LambdaQueryWrapper<KafkaAclPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        queryWrapper.eq(KafkaAclPO::getResourceType, ResourceType.TOPIC.code());
        queryWrapper.and(qw -> qw.eq(KafkaAclPO::getResourceName, topicName).or().eq(KafkaAclPO::getResourceName, "*"));

        return kafkaAclDAO.selectList(queryWrapper);
    }

    /**************************************************** private method ****************************************************/

    private Result<List<AclBinding>> getAclByZKClient(VersionItemParam itemParam){
        ClusterPhyParam param = (ClusterPhyParam) itemParam;

        List<AclBinding> aclList = new ArrayList<>();
        for (ZkAclStore store: CollectionConverters.asJava(ZkAclStore.stores())) {
            Result<List<AclBinding>> rl = this.getSpecifiedTypeAclByZKClient(param.getClusterPhyId(), store.patternType());
            if (rl.failed()) {
                return Result.buildFromIgnoreData(rl);
            }

            aclList.addAll(rl.getData());
        }

        return Result.buildSuc(aclList);
    }

    private Result<List<AclBinding>> getAclByKafkaClient(VersionItemParam itemParam) {
        ClusterPhyParam param = (ClusterPhyParam) itemParam;
        try {
            // 获取集群
            ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(param.getClusterPhyId());
            if (clusterPhy == null) {
                return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(param.getClusterPhyId()));
            }

            // 判断是否开启认证
            if (!ClusterAuthTypeEnum.enableAuth(clusterPhy.getAuthType())) {
                log.warn("method=getAclByKafkaClient||clusterPhyId={}||msg=not open auth and ignore get acls", clusterPhy.getId());
                return Result.buildSuc(new ArrayList<>());
            }

            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            DescribeAclsResult describeAclsResult =
                    adminClient.describeAcls(AclBindingFilter.ANY, new DescribeAclsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));

            return Result.buildSuc(new ArrayList<>(describeAclsResult.values().get()));
        } catch (Exception e) {
            log.error("method=getAclByKafkaClient||clusterPhyId={}||errMsg={}", param.getClusterPhyId(), e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<AclBinding>> getSpecifiedTypeAclByZKClient(Long clusterPhyId, PatternType patternType) {
        List<AclBinding> kafkaAclList = new ArrayList<>();
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(clusterPhyId);

            for (String rType: CollectionConverters.asJava(kafkaZkClient.getResourceTypes(patternType))) {
                ResourceType resourceType = SecurityUtils.resourceType(rType);
                for (String resourceName : CollectionConverters.asJava(kafkaZkClient.getResourceNames(patternType, resourceType))) {
                    ResourcePattern resourcePattern = new ResourcePattern(resourceType, resourceName, patternType);
                    AclAuthorizer.VersionedAcls versionedAcls = kafkaZkClient.getVersionedAclsForResource(resourcePattern);

                    CollectionConverters.asJava(versionedAcls.acls()).forEach(elem -> kafkaAclList.add(new AclBinding(resourcePattern, elem.ace())));
                }
            }
        } catch (Exception e) {
            log.error("method=getSpecifiedTypeAclByZKClient||clusterPhyId={}||patternType={}||errMsg={}", clusterPhyId, patternType, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc(kafkaAclList);
    }

    private Result<Void> insertAndIgnoreDuplicate(KafkaAclPO kafkaAclPO) {
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
}
