package com.xiaojukeji.kafka.manager.service.utils;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import kafka.admin.AdminOperationException;
import kafka.admin.AdminUtils;
import kafka.admin.AdminUtils$;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.errors.*;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.*;

/**
 * HA-Topic Commands
 */
public class HaTopicCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaTopicCommands.class);

    private static final String HA_TOPICS = "ha-topics";

    /**
     * 修改HA配置
     */
    public static ResultStatus modifyHaTopicConfig(ClusterDO clusterDO, String topicName, Properties props) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            AdminUtils$.MODULE$.kafka$admin$AdminUtils$$changeEntityConfig(zkUtils, HA_TOPICS, topicName, props);
        } catch (AdminOperationException aoe) {
            LOGGER.error("method=modifyHaTopicConfig||clusterPhyId={}||topicName={}||props={}||errMsg=exception", clusterDO.getId(), topicName, props, aoe);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_TOPIC_PARTITION;
        } catch (InvalidConfigurationException ice) {
            LOGGER.error("method=modifyHaTopicConfig||clusterPhyId={}||topicName={}||props={}||errMsg=exception", clusterDO.getId(), topicName, props, ice);
            return ResultStatus.TOPIC_OPERATION_TOPIC_CONFIG_ILLEGAL;
        } catch (Exception e) {
            LOGGER.error("method=modifyHaTopicConfig||clusterPhyId={}||topicName={}||props={}||errMsg=exception", clusterDO.getId(), topicName, props, e);
            return ResultStatus.TOPIC_OPERATION_UNKNOWN_ERROR;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }

        return ResultStatus.SUCCESS;
    }

    /**
     * 删除指定HA配置
     */
    public static ResultStatus deleteHaTopicConfig(ClusterDO clusterDO, String topicName, List<String> neeDeleteConfigNameList){
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            // 当前配置
            Properties presentProps = AdminUtils.fetchEntityConfig(zkUtils, HA_TOPICS, topicName);

            //删除需要删除的的配置
            for (String configName : neeDeleteConfigNameList) {
                presentProps.remove(configName);
            }

            AdminUtils$.MODULE$.kafka$admin$AdminUtils$$changeEntityConfig(zkUtils, HA_TOPICS, topicName, presentProps);
        } catch (Exception e){
            LOGGER.error("method=deleteHaTopicConfig||clusterPhyId={}||topicName={}||delProps={}||errMsg=exception", clusterDO.getId(), topicName, ListUtils.strList2String(neeDeleteConfigNameList), e);
            return ResultStatus.FAIL;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
        return ResultStatus.SUCCESS;
    }

    public static Properties fetchHaTopicConfig(ClusterDO clusterDO, String topicName){
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            return AdminUtils.fetchEntityConfig(zkUtils, HA_TOPICS, topicName);
        } catch (Exception e){
            LOGGER.error("method=fetchHaTopicConfig||clusterPhyId={}||topicName={}||errMsg=exception", clusterDO.getId(), topicName, e);
            return null;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
    }

    public static Map<String, Properties> fetchAllHaTopicConfig(ClusterDO clusterDO) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            return JavaConversions.asJavaMap(AdminUtils.fetchAllEntityConfigs(zkUtils, HA_TOPICS));
        } catch (Exception e){
            LOGGER.error("method=fetchAllHaTopicConfig||clusterPhyId={}||errMsg=exception", clusterDO.getId(), e);
            return null;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
    }

    private HaTopicCommands() {
    }
}