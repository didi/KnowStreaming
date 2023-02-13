package com.xiaojukeji.kafka.manager.service.utils;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import kafka.admin.AdminUtils;
import kafka.admin.AdminUtils$;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;


/**
 * @author fengqiongfeng
 * @date 21/4/11
 */
public class HaKafkaUserCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaKafkaUserCommands.class);

    /**
     * 修改User配置
     */
    public static boolean modifyHaUserConfig(String zookeeper, String kafkaUser, Properties modifiedProps) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    zookeeper,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );
            // 获取当前配置
            Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.User(), kafkaUser);

            // 补充变更的配置
            props.putAll(modifiedProps);

            // 修改配置, 这里不使用changeUserOrUserClientIdConfig方法的原因是changeUserOrUserClientIdConfig这个方法会进行参数检查
            AdminUtils$.MODULE$.kafka$admin$AdminUtils$$changeEntityConfig(zkUtils, ConfigType.User(), sanitize(kafkaUser), props);
        } catch (Exception e) {
            LOGGER.error("method=changeHaUserConfig||zookeeper={}||kafkaUser={}||modifiedProps={}||errMsg=exception", zookeeper, kafkaUser, modifiedProps, e);
            return false;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
        return true;
    }

    /**
     * 删除 高可用集群的动态配置
     */
    public static boolean deleteHaUserConfig(String zookeeper, String kafkaUser, List<String> needDeleteConfigNameList){
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(
                    zookeeper,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );

            Properties presentProps = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.User(), kafkaUser);

            //删除需要删除的的配置
            for (String configName : needDeleteConfigNameList) {
                presentProps.remove(configName);
            }

            // 修改配置, 这里不使用changeUserOrUserClientIdConfig方法的原因是changeUserOrUserClientIdConfig这个方法会进行参数检查
            AdminUtils$.MODULE$.kafka$admin$AdminUtils$$changeEntityConfig(zkUtils, ConfigType.User(), sanitize(kafkaUser), presentProps);

            return true;
        }catch (Exception e){
            LOGGER.error("method=deleteHaUserConfig||zookeeper={}||kafkaUser={}||delProps={}||errMsg=exception", zookeeper, kafkaUser, ListUtils.strList2String(needDeleteConfigNameList), e);

        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }

        return false;
    }

    private HaKafkaUserCommands() {
    }

    private static String sanitize(String name) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(name, StandardCharsets.UTF_8.name());
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < encoded.length(); i++) {
                char c = encoded.charAt(i);
                if (c == '*') {         // Metric ObjectName treats * as pattern
                    builder.append("%2A");
                } else if (c == '+') {  // Space URL-encoded as +, replace with percent encoding
                    builder.append("%20");
                } else {
                    builder.append(c);
                }
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new KafkaException(e);
        }
    }

    /**
     * Desanitize name that was URL-encoded using {@link #sanitize(String)}. This
     * is used to obtain the desanitized version of node names in ZooKeeper.
     */
    private static String desanitize(String name) {
        try {
            return URLDecoder.decode(name, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new KafkaException(e);
        }
    }
}
