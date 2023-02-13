package com.xiaojukeji.kafka.manager.service.biz.ha.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.app.AppRelateTopicsVO;
import com.xiaojukeji.kafka.manager.common.utils.FutureUtil;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaAppManager;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
public class HaAppManagerImpl implements HaAppManager {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Autowired
    private TopicConnectionService topicConnectionService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private TopicManagerService topicManagerService;

    private static final FutureUtil<Result<List<AppRelateTopicsVO>>> ConnectionsSearchTP = FutureUtil.init(
            "ConnectionsSearchTP",
            5,
            5,
            500
    );

    @Override
    public Result<List<AppRelateTopicsVO>> appRelateTopics(Boolean ha, Long clusterPhyId, List<String> filterTopicNameList) {
        // 获取关联的Topic列表
        Map<String, Set<String>> userTopicMap = this.appRelateTopicsMap(clusterPhyId, filterTopicNameList);

        Map<String, Set<String>> appClientSetMap = haASRelationService.listAllHAClient(clusterPhyId, userTopicMap.keySet());

        // 获取集群已建立HA的Topic列表
        Set<String> haTopicNameSet = haASRelationService.listAllHAFromDB(clusterPhyId, HaResTypeEnum.TOPIC)
                .stream()
                .map(elem -> elem.getActiveResName())
                .collect(Collectors.toSet());

        Set<String> topicNameSet = null;
        if (ha) {
            topicNameSet = haTopicNameSet;
        }else {
            List<TopicDO>  topicDOS = topicManagerService.getByClusterId(clusterPhyId);
            topicNameSet = topicDOS.stream()
                    .filter(topicBizPO -> !haTopicNameSet.contains(topicBizPO.getTopicName()))
                    .map(TopicDO::getTopicName).collect(Collectors.toSet());
        }

        Set<String> filterTopicNameSet = new HashSet<>(filterTopicNameList);

        List<AppRelateTopicsVO> voList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry: userTopicMap.entrySet()) {
            AppRelateTopicsVO vo = new AppRelateTopicsVO();
            vo.setClusterPhyId(clusterPhyId);
            vo.setKafkaUser(entry.getKey());
            vo.setHaClientIdList(new ArrayList<>(appClientSetMap.getOrDefault(entry.getKey(), new HashSet<>())));
            vo.setSelectedTopicNameList(new ArrayList<>());
            vo.setNotSelectTopicNameList(new ArrayList<>());
            vo.setNotHaTopicNameList(new ArrayList<>());
            Set<String> finalTopicNameSet = topicNameSet;
            entry.getValue().forEach(elem -> {
                if (elem.startsWith("__")) {
                    // ignore
                    return;
                }

                if (!finalTopicNameSet.contains(elem)) {
                    vo.getNotHaTopicNameList().add(elem);
                } else if (filterTopicNameSet.contains(elem)) {
                    vo.getSelectedTopicNameList().add(elem);
                } else {
                    vo.getNotSelectTopicNameList().add(elem);
                }
            });

            voList.add(vo);
        }

        return Result.buildSuc(voList);
    }

    @Override
    public Result<List<AppRelateTopicsVO>> appAndClientRelateTopics(Long clusterPhyId, Set<String> filterTopicNameSet) {
        List<HaASRelationDO> haASRelationDOList = haASRelationService.listAllHAFromDB(clusterPhyId, HaResTypeEnum.CLUSTER);
        Long secondClusterId = null;
        for (HaASRelationDO asRelationDO: haASRelationDOList) {
            if (clusterPhyId.equals(asRelationDO.getActiveClusterPhyId())) {
                secondClusterId = asRelationDO.getStandbyClusterPhyId();
            } else {
                secondClusterId = asRelationDO.getActiveClusterPhyId();
            }

            break;
        }

        Map<String/*TopicName*/, Result<Map<String/*KafkaUser*/, Set<String>/*ClientID*/>>> connectionsResultMap = new ConcurrentHashMap<>();

        // 生效时间
        Long activeMin = configService.getLongValue(ConfigConstant.HA_CONNECTION_ACTIVE_TIME_UNIT_MIN, Constant.TOPIC_CONNECTION_LATEST_TIME_MS / 1000 / 60);

        // 获取Topic关联的连接
        for (String topicName: filterTopicNameSet) {
            Long tempSecondClusterId = secondClusterId;
            ConnectionsSearchTP.runnableTask(
                    String.format("clusterPhyId=%d||topicName=%s", clusterPhyId, topicName),
                    10000,
                    () -> {
                        Result<Map<String, Set<String>>> userAndClientMapResult = topicConnectionService.getHaKafkaUserAndClientIdByTopicName(
                                clusterPhyId,
                                tempSecondClusterId,
                                topicName,
                                new Date(System.currentTimeMillis() - activeMin * 60L * 1000L),
                                new Date()
                        );

                        connectionsResultMap.put(topicName, userAndClientMapResult);
                    }
            );

            ConnectionsSearchTP.waitExecute(10000);
        }

        // 因为接口比较重要，只要一出现异常，则直接返回错误
        for (Result<Map<String, Set<String>>> valueResult: connectionsResultMap.values()) {
            if (valueResult.failed()) {
                return Result.buildFromIgnoreData(valueResult);
            }
        }

        // 查询结果转Map
        Map<String/*KafkaUser*/, Set<String>/*ClientID*/> kafkaUserAndClientMap = new HashMap<>();
        for (Result<Map<String, Set<String>>> valueResult: connectionsResultMap.values()) {
            for (Map.Entry<String, Set<String>> entry: valueResult.getData().entrySet()) {
                kafkaUserAndClientMap.putIfAbsent(entry.getKey(), new HashSet<>());
                kafkaUserAndClientMap.get(entry.getKey()).addAll(entry.getValue());
            }
        }

        // 获取集群已建立HA的Topic列表
        Set<String> haTopicNameSet = haASRelationService.listAllHAFromDB(clusterPhyId, HaResTypeEnum.TOPIC)
                .stream()
                .map(elem -> elem.getActiveResName())
                .collect(Collectors.toSet());

        // 获取KafkaUser+Client下的Topic列表
        List<AppRelateTopicsVO> voList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry: kafkaUserAndClientMap.entrySet()) {
            Long tempSecondClusterId = secondClusterId;
            ConnectionsSearchTP.runnableTask(
                    "",
                    10000,
                    () -> {
                        Result<List<TopicConnectionDO>> doListResult = topicConnectionService.getByClusterAndAppId(
                                clusterPhyId,
                                tempSecondClusterId,
                                entry.getKey(),
                                new Date(System.currentTimeMillis() - activeMin * 60L * 1000L),
                                new Date()
                        );
                        if (doListResult.failed()) {
                            return Result.buildFromIgnoreData(doListResult);
                        }

                        return Result.buildSuc(convert2VOList(clusterPhyId, entry.getValue(), doListResult.getData(), haTopicNameSet, filterTopicNameSet));
                    }
            );

            for (Result<List<AppRelateTopicsVO>> elem: ConnectionsSearchTP.waitResult(10000)) {
                if (elem.failed()) {
                    Result.buildFromIgnoreData(elem);
                }

                voList.addAll(elem.getData());
            }
        }

        return Result.buildSuc(voList);
    }

    @Override
    public boolean isContainAllRelateAppTopics(Long clusterPhyId, List<String> filterTopicNameList) {
        Map<String, Set<String>> userTopicMap = this.appRelateTopicsMap(clusterPhyId, filterTopicNameList);

        Set<String> relateTopicSet = new HashSet<>();
        userTopicMap.values().forEach(elem -> relateTopicSet.addAll(elem));

        return filterTopicNameList.containsAll(relateTopicSet);
    }

    private Map<String, Set<String>> appRelateTopicsMap(Long clusterPhyId, List<String> filterTopicNameList) {
        Map<String, Set<String>> userTopicMap = new HashMap<>();
        for (String topicName: filterTopicNameList) {
            authorityService.getAuthorityByTopicFromCache(clusterPhyId, topicName)
                    .stream()
                    .map(elem -> elem.getAppId())
                    .filter(item -> !userTopicMap.containsKey(item))
                    .forEach(kafkaUser ->
                            userTopicMap.put(
                                    kafkaUser,
                                    authorityService.getAuthority(kafkaUser).stream().map(authorityDO -> authorityDO.getTopicName()).collect(Collectors.toSet())
                            )
                    );
        }

        return userTopicMap;
    }

    private List<AppRelateTopicsVO> convert2VOList(Long clusterPhyId,
                                                   Set<String> clientIdSet,
                                                   List<TopicConnectionDO> connectionList,
                                                   Set<String> haTopicNameSet,
                                                   Set<String> filterTopicNameSet) {
        Map<String/*clientID*/, AppRelateTopicsVO> voMap = new HashMap<>();
        for (TopicConnectionDO connection: connectionList) {
            if (connection.getTopicName().startsWith("__")) {
                // 忽略系统内部Topic
                continue;
            }

            if (!clientIdSet.contains("") && !clientIdSet.contains(connection.getClientId())) {
                continue;
            }

            AppRelateTopicsVO vo = voMap.get(connection.getClientId());
            if (vo == null) {
                vo = new AppRelateTopicsVO(clusterPhyId, connection.getAppId(), connection.getClientId());
            }

            if (!haTopicNameSet.contains(connection.getTopicName())) {
                vo.addNotHaIfNotExist(connection.getTopicName());
            }

            if (!filterTopicNameSet.contains(connection.getTopicName())) {
                vo.addNotSelectedIfNotExist(connection.getTopicName());
            } else {
                vo.addSelectedIfNotExist(connection.getTopicName());
            }

            voMap.put(connection.getClientId(), vo);
        }

        return new ArrayList<>(voMap.values());
    }
}
