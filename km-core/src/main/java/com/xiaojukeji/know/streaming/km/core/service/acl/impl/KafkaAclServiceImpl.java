package com.xiaojukeji.know.streaming.km.core.service.acl.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import scala.jdk.javaapi.CollectionConverters;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;


@Service
public class KafkaAclServiceImpl extends BaseVersionControlService implements KafkaAclService {
    private static final ILog log = LogFactory.getLog(KafkaAclServiceImpl.class);

    private static final String ACL_GET_FROM_KAFKA    = "getAclFromKafka";

    @Autowired
    private KafkaAclDAO kafkaAclDAO;

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
        registerVCHandler(ACL_GET_FROM_KAFKA,     V_0_10_0_0, V_2_8_0, "getAclByZKClient",          this::getAclByZKClient);
        registerVCHandler(ACL_GET_FROM_KAFKA,     V_2_8_0, V_MAX,      "getAclByKafkaClient",       this::getAclByKafkaClient);
    }

    @Override
    public Result<List<AclBinding>> getAclFromKafka(Long clusterPhyId) {
        if (LoadedClusterPhyCache.getByPhyId(clusterPhyId) == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        try {
            return (Result<List<AclBinding>>) versionControlService.doHandler(getVersionItemType(), getMethodName(clusterPhyId, ACL_GET_FROM_KAFKA), new ClusterPhyParam(clusterPhyId));
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(e.getResultStatus());
        }
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

        return (int)poList.stream().map(elem -> elem.getResourceName()).distinct().count();
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

        return (int)poList.stream().map(elem -> elem.getPrincipal()).distinct().count();
    }

    @Override
    public List<KafkaAclPO> getKafkaResTypeAclFromDB(Long clusterPhyId, Integer resType) {
        LambdaQueryWrapper<KafkaAclPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        queryWrapper.eq(KafkaAclPO::getResourceType, resType);
        return kafkaAclDAO.selectList(queryWrapper);
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

    @Override
    public List<KafkaAclPO> getGroupAclFromDB(Long clusterPhyId, String groupName) {
        LambdaQueryWrapper<KafkaAclPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KafkaAclPO::getClusterPhyId, clusterPhyId);
        queryWrapper.eq(KafkaAclPO::getResourceType, ResourceType.GROUP.code());
        queryWrapper.eq(KafkaAclPO::getResourceName, groupName);
        return kafkaAclDAO.selectList(queryWrapper);
    }

    /**************************************************** private method ****************************************************/

    private Result<List<AclBinding>> getAclByZKClient(VersionItemParam itemParam){
        ClusterPhyParam param = (ClusterPhyParam) itemParam;

        List<AclBinding> aclList = new ArrayList<>();
        for (ZkAclStore store: CollectionConverters.asJava(ZkAclStore.stores())) {
            Result<List<AclBinding>> rl = this.getSpecifiedTypeAclByZKClient(param.getClusterPhyId(), store.patternType());
            if (rl.failed()) {
                return rl;
            }

            aclList.addAll(rl.getData());
        }

        return Result.buildSuc(aclList);
    }

    private Result<List<AclBinding>> getAclByKafkaClient(VersionItemParam itemParam) {
        ClusterPhyParam param = (ClusterPhyParam) itemParam;
        try {
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
}
