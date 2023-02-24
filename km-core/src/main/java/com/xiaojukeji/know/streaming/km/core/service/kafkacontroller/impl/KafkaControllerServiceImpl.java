package com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.kafkacontrollr.KafkaControllerPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.kafkacontroller.KafkaControllerDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KafkaControllerServiceImpl implements KafkaControllerService {
    private static final ILog log = LogFactory.getLog(KafkaControllerServiceImpl.class);

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private KafkaControllerDAO kafkaControllerDAO;

    @Override
    public Result<KafkaController> getControllerFromKafka(ClusterPhy clusterPhy) {
        if (clusterPhy.getRunState().equals(ClusterRunStateEnum.RUN_ZK.getRunState())) {
            return this.getControllerFromZKClient(clusterPhy);
        }

        return this.getControllerFromAdminClient(clusterPhy);
    }

    @Override
    public int insertAndIgnoreDuplicateException(KafkaController kafkaController, String controllerHost, String controllerRack) {
        try {
            KafkaControllerPO kafkaControllerPO = new KafkaControllerPO();
            kafkaControllerPO.setClusterPhyId(kafkaController.getClusterPhyId());
            kafkaControllerPO.setBrokerId(kafkaController.getBrokerId());
            kafkaControllerPO.setTimestamp(kafkaController.getTimestamp());
            kafkaControllerPO.setBrokerHost(controllerHost != null? controllerHost: "");
            kafkaControllerPO.setBrokerRack(controllerRack != null? controllerRack: "");
            kafkaControllerDAO.insert(kafkaControllerPO);
        } catch (DuplicateKeyException dke) {
            // ignore
        }

        // 删除错误的时间数据
        LambdaQueryWrapper<KafkaControllerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaControllerPO::getClusterPhyId, kafkaController.getClusterPhyId());
        lambdaQueryWrapper.eq(KafkaControllerPO::getBrokerId, Constant.INVALID_CODE);
        lambdaQueryWrapper.gt(KafkaControllerPO::getTimestamp, kafkaController.getTimestamp());
        kafkaControllerDAO.delete(lambdaQueryWrapper);
        return 0;
    }

    @Override
    public int setNoKafkaController(Long clusterPhyId, Long triggerTime) {
        KafkaController kafkaController = this.getKafkaControllerFromDB(clusterPhyId);
        if (kafkaController == null) {
            // 已经被设置为no kafka-controller了
            return 0;
        }

        KafkaController noKafkaController = new KafkaController();
        noKafkaController.setClusterPhyId(clusterPhyId);
        noKafkaController.setBrokerId(Constant.INVALID_CODE);

        // 归一化到秒, 并且将去1秒，避免gc导致时间不对
        noKafkaController.setTimestamp(triggerTime);
        return this.insertAndIgnoreDuplicateException(noKafkaController, "", "");
    }

    @Override
    public KafkaController getKafkaControllerFromDB(Long clusterPhyId) {
        Map<Long, KafkaController> controllerMap = this.getKafkaControllersFromDB(Arrays.asList(clusterPhyId), true);

        return controllerMap.get(clusterPhyId);
    }

    @Override
    public Map<Long, KafkaController> getKafkaControllersFromDB(List<Long> clusterPhyIdList, boolean notIncludeNotAlive) {
        List<KafkaControllerPO> poList = kafkaControllerDAO.listAllLatest();

        Map<Long, KafkaController> controllerMap = new HashMap<>();
        for (KafkaControllerPO po: poList) {
            if ((po.getBrokerId().equals(Constant.INVALID_CODE) && notIncludeNotAlive)
                    || !clusterPhyIdList.contains(po.getClusterPhyId())) {
                continue;
            }

            KafkaController kafkaController = new KafkaController();
            kafkaController.setClusterPhyId(po.getClusterPhyId());
            kafkaController.setBrokerId(po.getBrokerId());
            kafkaController.setTimestamp(po.getTimestamp());
            controllerMap.put(po.getClusterPhyId(), kafkaController);
        }

        return controllerMap;
    }

    @Override
    public IPage<KafkaControllerPO> pagingControllerHistories(Long clusterPhyId, Integer pageNo, Integer pageSize, String brokerHostSearchKeyword) {
        LambdaQueryWrapper<KafkaControllerPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KafkaControllerPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.like(!ValidateUtils.isBlank(brokerHostSearchKeyword), KafkaControllerPO::getBrokerHost, brokerHostSearchKeyword);
        lambdaQueryWrapper.orderByDesc(KafkaControllerPO::getTimestamp);

        return kafkaControllerDAO.selectPage(new Page<>(pageNo, pageSize), lambdaQueryWrapper);
    }

    /**************************************************** private method ****************************************************/

    private Result<KafkaController> getControllerFromAdminClient(ClusterPhy clusterPhy) {
        AdminClient adminClient = null;
        try {
            adminClient = kafkaAdminClient.getClient(clusterPhy.getId());
        } catch (Exception e) {
            log.error("method=getControllerFromAdminClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            // 集群已经加载进来，但是创建admin-client失败，则设置无controller
            return Result.buildSuc();
        }

        // 先从DB获取该集群controller
        KafkaController dbKafkaController = null;

        for (int i = 1; i <= Constant.DEFAULT_RETRY_TIME; ++i) {
            try {
                if (i == 1) {
                    // 获取DB中的controller信息
                    dbKafkaController = this.getKafkaControllerFromDB(clusterPhy.getId());
                }

                DescribeClusterResult describeClusterResult = adminClient.describeCluster(
                        new DescribeClusterOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                );

                Node controllerNode = describeClusterResult.controller().get();
                if (controllerNode == null) {
                    return Result.buildSuc();
                }

                if (dbKafkaController != null && controllerNode.id() == dbKafkaController.getBrokerId()) {
                    // ID没有变化，直接返回原先的
                    return Result.buildSuc(dbKafkaController);
                }

                // 发生了变化
                return Result.buildSuc(new KafkaController(
                        clusterPhy.getId(),
                        controllerNode.id(),
                        System.currentTimeMillis()
                ));
            } catch (Exception e) {
                log.error(
                        "method=getControllerFromAdminClient||clusterPhyId={}||tryTime={}||errMsg=exception",
                        clusterPhy.getId(), i, e
                );
            }
        }

        // 三次出错，则直接返回无controller
        return Result.buildSuc();
    }

    private Result<KafkaController> getControllerFromZKClient(ClusterPhy clusterPhy) {
        try {
            return Result.buildSuc(kafkaZKDAO.getKafkaController(clusterPhy.getId(), false));
        } catch (Exception e) {
            log.error("method=getControllerFromZKClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }
}
