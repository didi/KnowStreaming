/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.kafka.config.manager;

import com.didichuxing.datachannel.kafka.config.HATopicConfig;
import kafka.server.MirrorTopic;
import kafka.server.ReplicaManager;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.apache.kafka.common.internals.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TopicConfigManager {

    private static final ConfigDef DIDI_HA_TOPIC_CONFIGS = HATopicConfig.configDef();
    private static final Map<String, MirrorTopic> mirrorTopics = new HashMap<>();
    private static final Set<String> clusterRelatedConfigs = new HashSet<>();

    static {
        clusterRelatedConfigs.add(HATopicConfig.DIDI_HA_REMOTE_CLUSTER);
    }

    public static final Logger log = LoggerFactory.getLogger(TopicConfigManager.class);

    private final ReplicaManager replicaManager;

    public TopicConfigManager(ReplicaManager replicaManager) {
        this.replicaManager = replicaManager;
    }

    public static void validateConfigs(Properties configs) {
        for (String key : configs.stringPropertyNames()) {
            if (!HATopicConfig.configNames().contains(key))
                throw new InvalidConfigurationException(String.format("Unknown ha-topic config name: %s", key));
        }
        Map<String, Object> realConfigs = DIDI_HA_TOPIC_CONFIGS.parse(configs);
        for (String key : configs.stringPropertyNames()) {
            if (DIDI_HA_TOPIC_CONFIGS.configKeys().get(key).validator != null)
                DIDI_HA_TOPIC_CONFIGS.configKeys().get(key).validator.ensureValid(key, realConfigs.get(key));
        }

    }

    //某Topic的配置中是否有对集群cluster的依赖
    public static Boolean ifConfigsClusterRelated(Properties configs, String cluster) {
        for (String clusterRelatedConfig : clusterRelatedConfigs) {
            if (configs.containsKey(clusterRelatedConfig) && Objects.equals(configs.getProperty(clusterRelatedConfig), cluster))
                return true;
        }
        return false;
    }

    public void configure(String topicName, Properties configs) {
        if (Topic.isInternal(topicName) && !Objects.equals(topicName, Topic.GROUP_METADATA_TOPIC_NAME))
            return;
        //the remoteTopic cannot be any of InternalTopics
        Map<String, Object> realConfiges = DIDI_HA_TOPIC_CONFIGS.parse(configs);
        if (configs.containsKey(HATopicConfig.DIDI_HA_REMOTE_CLUSTER)) {
            String remoteTopicName = (String) configs.getOrDefault(HATopicConfig.DIDI_HA_REMOTE_TOPIC, topicName);
            Boolean syncTopicPartitions = (Boolean) realConfiges.getOrDefault(HATopicConfig.DIDI_HA_SYNC_TOPIC_PARTITIONS_ENABLED, false);
            Boolean syncTopicConfigs = (Boolean) realConfiges.getOrDefault(HATopicConfig.DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED, false);
            Boolean syncTopicAcls = (Boolean) realConfiges.getOrDefault(HATopicConfig.DIDI_HA_SYNC_TOPIC_ACLS_ENABLED, false);
            MirrorTopic mirrorTopic = new MirrorTopic(remoteTopicName,
                    configs.getProperty(HATopicConfig.DIDI_HA_REMOTE_CLUSTER),
                    topicName,
                    syncTopicPartitions,
                    syncTopicConfigs,
                    syncTopicAcls);
            replicaManager.addMirrorTopics(mirrorTopic);
            mirrorTopics.put(topicName, mirrorTopic);
        } else {
            replicaManager.removeMirrorTopicsByLocalTopic(topicName);
            mirrorTopics.remove(topicName);
        }
    }

    public static boolean isMirrorTopicByLocal(String topicName) {
        if (Topic.GROUP_METADATA_TOPIC_NAME.equals(topicName)) {
            return false;
        }
        return mirrorTopics.containsKey(topicName);
    }

}
