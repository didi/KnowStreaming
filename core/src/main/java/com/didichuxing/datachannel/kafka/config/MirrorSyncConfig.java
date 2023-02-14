package com.didichuxing.datachannel.kafka.config;

import com.didichuxing.datachannel.kafka.config.manager.ClusterConfigManager;
import kafka.server.AdminManager;
import kafka.server.MirrorTopic;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.requests.DescribeConfigsResponse;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.server.authorizer.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.JavaConverters;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MirrorSyncConfig {
    private static final Logger log = LoggerFactory.getLogger(MirrorSyncConfig.class);
    private static final ResourcePatternFilter ANY_TOPIC = new ResourcePatternFilter(ResourceType.TOPIC, null, PatternType.ANY);
    private static final AclBindingFilter ANY_TOPIC_ACL = new AclBindingFilter(ANY_TOPIC, AccessControlEntryFilter.ANY);
    private final Map<String, AdminClient> adminClientMap = new ConcurrentHashMap<>();
    private static final String DIDI_CONFIG_PREFIX = "didi.ha";
    private final AdminManager adminManager;
    private final Option<Authorizer> authorizer;
    private HashMap<String, List<AclBinding>> cachedAcls = new HashMap<>();

    public MirrorSyncConfig(AdminManager adminManager, Option<Authorizer> authorizer) {
        this.adminManager = adminManager;
        this.authorizer = authorizer;
    }

    /**
     * 根据mirrorTopic关联的cluster进行初始化adminClient
     *
     * @param clusterSet
     */
    public void initAdminClient(Set<String> clusterSet) {
        clusterSet.forEach(clusterId -> {
            if (adminClientMap.get(clusterId) == null) {
                Properties properties = ClusterConfigManager.getConfigs(clusterId);
                if (!properties.isEmpty()) {
                    try {
                        adminClientMap.put(clusterId, AdminClient.create(properties));
                    } catch (Exception e) {
                        log.error("can not init adminClient for cluster {} with {} ", clusterId, properties, e);
                    }
                }
            }
        });
        //释放掉无关联的adminClient
        Set<String> removedSet = adminClientMap.keySet().stream().filter(y -> !clusterSet.contains(y)).collect(Collectors.toSet());
        removedSet.forEach(x -> {
            releaseAdminClient(x, adminClientMap.get(x));
        });
    }

    public void close() {
        adminClientMap.forEach(this::releaseAdminClient);
    }

    private synchronized void releaseAdminClient(String clusterId, AdminClient client) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            log.error("release cluster {} adminClient failure ", clusterId, e);
        } finally {
            adminClientMap.remove(clusterId);
        }
    }

    public void syncTopicsAcls(List<MirrorTopic> allTopics) {
        if (!authorizer.isEmpty()) {
            Authorizer auth = authorizer.get();
            Map<String, List<MirrorTopic>> remoteTopic = allTopics.stream().collect(Collectors.groupingBy(MirrorTopic::remoteCluster));
            remoteTopic.forEach((key, value) -> {
                AdminClient client = adminClientMap.get(key);
                if (client != null) {
                    Map<String, MirrorTopic> topics = value.stream().collect(Collectors.toMap(MirrorTopic::remoteTopic, x -> x));
                    syncMirrorTopicsAcl(client, key, topics, auth);
                }
            });
        }
    }

    public void syncTopicConfigs(List<MirrorTopic> allTopics) {
        Map<String, List<MirrorTopic>> remoteTopic = allTopics.stream().collect(Collectors.groupingBy(MirrorTopic::remoteCluster));
        remoteTopic.forEach((key, value) -> {
            AdminClient client = adminClientMap.get(key);
            if (client != null) {
                Map<String, MirrorTopic> topics = value.stream().collect(Collectors.toMap(MirrorTopic::remoteTopic, x -> x));
                syncMirrorTopicsConfig(client, key, topics);
            }
        });
    }

    private void syncMirrorTopicsAcl(AdminClient client, String clusterId, Map<String, MirrorTopic> topics, Authorizer auth) {
        try {
            Collection<AclBinding> result = client.describeAcls(ANY_TOPIC_ACL).values().get(30, TimeUnit.SECONDS);
            List<AclBinding> remoteBindings = result.stream()
                    .filter(x -> x.pattern().resourceType() == ResourceType.TOPIC)
                    .filter(x -> x.pattern().patternType() == PatternType.LITERAL)
                    .filter(x -> topics.containsKey(x.pattern().name()))
                    .map(x -> new AclBinding(new ResourcePattern(x.pattern().resourceType(), topics.get(x.pattern().name()).localTopic(), x.pattern().patternType()), x.entry()))
                    .collect(Collectors.toList());
            List<AclBinding> cacheClusterAcl = cachedAcls.get(clusterId);
            if (cacheClusterAcl != null) {
                //计算出远程集群修改过或已经删除的ACL信息，并同步删除本地ACL信息
                List<AclBinding> diffAcl = new ArrayList<>(cacheClusterAcl);
                diffAcl.removeAll(remoteBindings);
                List<AclBindingFilter> remoteAclBindingFilters = diffAcl.stream().map(AclBinding::toFilter).collect(Collectors.toList());
                if (!remoteAclBindingFilters.isEmpty()) {
                    auth.deleteAcls(null, remoteAclBindingFilters);
                }
            }
            if (!remoteBindings.isEmpty() && !remoteBindings.equals(cacheClusterAcl)) {
                auth.createAcls(null, remoteBindings);
            }
            //缓存当前的ACL信息
            cachedAcls.put(clusterId, remoteBindings);
        } catch (Exception e) {
            log.error("sync cluster {} topic {} acls config", clusterId, topics.keySet(), e);
        }
    }

    private void syncMirrorTopicsConfig(AdminClient client, String clusterId, Map<String, MirrorTopic> topics) {
        try {
            Set<ConfigResource> resources = topics.values().stream().filter(topic -> !topic.remoteTopic().equals(""))
                    .map(x -> new ConfigResource(ConfigResource.Type.TOPIC, x.remoteTopic()))
                    .collect(Collectors.toSet());
            DescribeConfigsOptions describeOptions = new DescribeConfigsOptions().includeSynonyms(true);
            //获取远程集群的Topic配置
            Map<String, Config> remoteConfigs = client.describeConfigs(resources, describeOptions).all().get(30, TimeUnit.SECONDS).entrySet().stream()
                    .collect(Collectors.toMap(x -> topics.get(x.getKey().name()).localTopic(), Map.Entry::getValue));
            scala.collection.mutable.Map<ConfigResource, Option<scala.collection.Set<String>>> localConfigNames = new scala.collection.mutable.HashMap<>();
            //筛选出用户动态改变的Topic配置
            Map<String, Map<String, String>> remoteConfigMap = new HashMap<>();
            for (Map.Entry<String, Config> entry : remoteConfigs.entrySet()) {
                List<String> configNames = new ArrayList<>();
                for (ConfigEntry cfg : entry.getValue().entries()) {
                    if (cfg.source() == ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG && !cfg.name().startsWith(DIDI_CONFIG_PREFIX)) {
                        configNames.add(cfg.name());
                        remoteConfigMap.computeIfAbsent(entry.getKey(), v -> new HashMap<>()).put(cfg.name(), cfg.value());
                    }
                }
                if (!configNames.isEmpty()) {
                    localConfigNames.put(new ConfigResource(ConfigResource.Type.TOPIC, entry.getKey()), Option.apply(JavaConverters.asScalaBuffer(configNames).toSet()));
                }
            }
            //根据远程集群的Topic配置项来获取本地集群的Topic配置
            scala.collection.Map<ConfigResource, DescribeConfigsResponse.Config> localConfigs = adminManager.describeConfigs(localConfigNames, false);
            scala.collection.mutable.Map<ConfigResource, scala.collection.immutable.List<AlterConfigOp>> alterConfig = new scala.collection.mutable.HashMap<>();
            for (Map.Entry<String, Config> entry : remoteConfigs.entrySet()) {
                Config remoteConfig = entry.getValue();
                String topic = entry.getKey();
                Map<String, String> remoteTopicConfig = remoteConfigMap.get(topic);
                List<AlterConfigOp> newEntry = new ArrayList<>();
                DescribeConfigsResponse.Config local = localConfigs.get(new ConfigResource(ConfigResource.Type.TOPIC, topic)).get();
                local.entries().forEach(e -> {
                    ConfigEntry configEntry = remoteConfig.get(e.name());
                    //筛选出本地态动配置
                    if (configEntry != null && e.source() == DescribeConfigsResponse.ConfigSource.TOPIC_CONFIG) {
                        //筛选出远程与本地不一致的配置项
                        if (!configEntry.value().equals(e.value())) {
                            newEntry.add(new AlterConfigOp(new ConfigEntry(configEntry.name(), configEntry.value()), AlterConfigOp.OpType.SET));
                        }
                        //删除远程与本地相同配置项,目的是把远程新增同步到本地
                        remoteTopicConfig.remove(e.name());
                    }
                });
                //增加远程新配置项
                remoteTopicConfig.forEach((k, v) -> newEntry.add(new AlterConfigOp(new ConfigEntry(k, v), AlterConfigOp.OpType.SET)));
                if (!newEntry.isEmpty()) {
                    alterConfig.put(new ConfigResource(ConfigResource.Type.TOPIC, topic), JavaConverters.asScalaBuffer(newEntry).toList());
                }
            }
            //更新本地Topic配置
            if (!alterConfig.isEmpty()) {
                adminManager.incrementalAlterConfigs(alterConfig, false);
            }
        } catch (Exception e) {
            log.error("sync cluster {} topic {} config", clusterId, topics.keySet(), e);
        }
    }

    /**
     * 如果zk节点配置发生变化只重建旧的adminClient,新增的不会创建
     *
     * @param clusterId
     */
    public void reloadAdminClient(String clusterId) {
        AdminClient client = adminClientMap.get(clusterId);
        if (client != null) {
            Properties properties = ClusterConfigManager.getConfigs(clusterId);
            try {
                releaseAdminClient(clusterId, client);
                if (!properties.isEmpty()) {
                    adminClientMap.put(clusterId, AdminClient.create(properties));
                }
            } catch (Exception e) {
                log.error("can not reload adminClient for cluster {} with {} ", clusterId, properties, e);
            }
        }
    }
}
