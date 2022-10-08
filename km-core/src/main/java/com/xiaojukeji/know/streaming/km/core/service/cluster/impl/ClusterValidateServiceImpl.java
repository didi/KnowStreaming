package com.xiaojukeji.know.streaming.km.core.service.cluster.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.util.KafkaValidate;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.enums.valid.ValidateKafkaAddressErrorEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterValidateService;
import com.xiaojukeji.know.streaming.km.persistence.jmx.JmxDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.impl.KafkaZKDAOImpl;
import kafka.server.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.internals.Topic;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import java.time.Duration;
import java.util.*;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Slf4j
@Service
public class ClusterValidateServiceImpl implements ClusterValidateService {
    private static final ILog logger = LogFactory.getLog(KafkaZKDAOImpl.class);

    @Autowired
    private JmxDAO jmxDAO;

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Override
    public Result<KafkaValidate> checkKafkaLegal(String bootstrapServers, Properties clientProps, String zookeeper) {
        if (ValidateUtils.isBlank(bootstrapServers) && ValidateUtils.isBlank(zookeeper)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "bootstrapServers与zookeeper不能同时为空");
        }

        if (!ValidateUtils.isBlank(bootstrapServers) && !ValidateUtils.isBlank(zookeeper)) {
            // 检查ZK 和 BS
            return this.checkZKAndBSLegal(bootstrapServers, clientProps, zookeeper);
        }

        if (!ValidateUtils.isBlank(zookeeper)) {
            // 仅检查ZK
            return Result.buildSuc(this.checkZKLegalAndTryGetInfo(zookeeper));
        }

        // 检查BS
        if (!checkBSLegal(bootstrapServers, clientProps)) {
            // BS 非法，则直接返回
            KafkaValidate kafkaValidate = new KafkaValidate();
            kafkaValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.BS_ERROR, ValidateKafkaAddressErrorEnum.BS_CONNECT_FAILED));
            return Result.buildSuc(kafkaValidate);
        }

        // BS合法，需要尝试获取其他信息
        return Result.buildSuc(this.getDataAndIgnoreCheckBSLegal(bootstrapServers, clientProps));
    }

    private Result<KafkaValidate> checkZKAndBSLegal(String bootstrapServers, Properties clientProps, String zookeeper) {
        KafkaValidate kafkaValidate = this.checkZKLegalAndTryGetInfo(zookeeper);
        if (!checkBSLegal(bootstrapServers, clientProps)) {
            kafkaValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.BS_ERROR, ValidateKafkaAddressErrorEnum.BS_CONNECT_FAILED));
        }

        return Result.buildSuc(kafkaValidate);
    }

    private boolean checkBSLegal(String bootstrapServers, Properties clientProps) {
        clientProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        clientProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        clientProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // 判断地址是否有问题
        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            kafkaConsumer = new KafkaConsumer<>(clientProps);
            kafkaConsumer.partitionsFor(Topic.GROUP_METADATA_TOPIC_NAME, Duration.ofMillis(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
        } catch (Exception e) {
            logger.error("method=checkBootstrapServersLegal||bootstrapServers={}||clientProps={}||errMsg=exception", bootstrapServers, clientProps, e);

            return false;
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
        }

        return true;
    }

    private KafkaValidate getDataAndIgnoreCheckBSLegal(String bootstrapServers, Properties clientProps) {
        AdminClient adminClient = null;

        KafkaValidate kafkaBSValidate = new KafkaValidate();
        try {
            adminClient = KafkaAdminClient.create(clientProps);

            // 获取集群broker节点
            DescribeClusterResult describeClusterResult = adminClient.describeCluster(new DescribeClusterOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            Collection<Node> nodeCollection = describeClusterResult.nodes().get();
            if (nodeCollection.isEmpty()) {
                kafkaBSValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.BS_ERROR, ValidateKafkaAddressErrorEnum.BS_CONNECT_FAILED));
                return kafkaBSValidate;
            }

            // 获取Broker中的ZK配置
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER, String.valueOf(new ArrayList<>(nodeCollection).get(0).id()));
            DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource), new DescribeConfigsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            Map<ConfigResource, Config> configMap = describeConfigsResult.all().get();

            ConfigEntry configEntry = configMap.get(configResource).get(KafkaConfig.ZkConnectProp());
            if (configEntry == null || configEntry.value() == null) {
                kafkaBSValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.BS_ERROR, ValidateKafkaAddressErrorEnum.BS_CONNECT_SUCCESS_BUT_RUN_RAFT));
                return kafkaBSValidate;
            }

            return this.checkZKLegalAndTryGetInfo(configEntry.value().replace(" ", ""));
        } catch (Exception e) {
            logger.error("method=checkBootstrapServersLegal||bootstrapServers={}||clientProps={}||msg=get cluster-info failed||errMsg=exception", bootstrapServers, clientProps, e);

            kafkaBSValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.BS_ERROR, ValidateKafkaAddressErrorEnum.BS_CONNECT_SUCCESS_BUT_API_NOT_SUPPORT));
            return kafkaBSValidate;
        } finally {
            if (adminClient != null) {
                adminClient.close();
            }
        }
    }


    private KafkaValidate checkZKLegalAndTryGetInfo(String zookeeper) {
        KafkaValidate kafkaZKValidate = new KafkaValidate();
        kafkaZKValidate.setZookeeper(zookeeper);

        Broker broker = null;
        try {
            broker = kafkaZKDAO.getBrokerMetadata(zookeeper);
            if (broker == null) {
                kafkaZKValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.ZK_ERROR, ValidateKafkaAddressErrorEnum.ZK_CONNECT_SUCCESS_BUT_NODE_NOT_EXIST));
            } else {
                kafkaZKValidate.setJmxPort(broker.getJmxPort());
            }
        } catch (KeeperException.NoNodeException nne) {
            logger.warn("method=checkZKLegalAndTryGetInfo||zookeeper={}||errMsg=NoNodeException", zookeeper);

            // ZK正常，但是节点不存在
            kafkaZKValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.ZK_ERROR, ValidateKafkaAddressErrorEnum.ZK_CONNECT_SUCCESS_BUT_NODE_NOT_EXIST));
        } catch (Exception e) {
            logger.error("method=checkZKLegalAndTryGetInfo||zookeeper={}||errMsg={}", zookeeper, e.getMessage());

            kafkaZKValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.ZK_ERROR, ValidateKafkaAddressErrorEnum.ZK_CONNECT_ERROR));
        }

        if (broker == null) {
            return kafkaZKValidate;
        }

        kafkaZKValidate.setJmxPort(broker.getJmxPort());
        if (broker.getJmxPort() == null || Constant.INVALID_CODE == broker.getJmxPort()) {
            kafkaZKValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.JMX_ERROR, ValidateKafkaAddressErrorEnum.JMX_WITHOUT_OPEN));
            return kafkaZKValidate;
        }

        try {
            Object jmxValue = jmxDAO.getJmxValue(broker.getHost(), broker.getJmxPort(), new JmxConfig(), new ObjectName(String.format("kafka.server:type=app-info,id=%d", broker.getBrokerId())), "Version");
            if (jmxValue instanceof String) {
                kafkaZKValidate.setKafkaVersion((String) jmxValue);
            } else {
                kafkaZKValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.JMX_ERROR, ValidateKafkaAddressErrorEnum.CONNECT_JMX_ERROR));
            }
        } catch (Exception e) {
            logger.error("method=checkZKLegalAndTryGetInfo||zookeeper={}||broker={}||errMsg={}", zookeeper, broker, e.getMessage());

            kafkaZKValidate.getErrList().add(Result.buildFrom(ValidateKafkaAddressErrorEnum.JMX_ERROR, ValidateKafkaAddressErrorEnum.CONNECT_JMX_ERROR));
        }


        return kafkaZKValidate;
    }
}
