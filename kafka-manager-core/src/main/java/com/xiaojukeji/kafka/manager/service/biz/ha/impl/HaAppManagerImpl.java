package com.xiaojukeji.kafka.manager.service.biz.ha.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaResTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.app.AppRelateTopicsVO;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaAppManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaASRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class HaAppManagerImpl implements HaAppManager {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private HaASRelationService haASRelationService;

    @Override
    public Result<List<AppRelateTopicsVO>> appRelateTopics(Long clusterPhyId, List<String> filterTopicNameList) {
        // 获取关联的Topic列表
        Map<String, Set<String>> userTopicMap = this.appRelateTopicsMap(clusterPhyId, filterTopicNameList);

        // 获取集群已建立HA的Topic列表
        Set<String> haTopicNameSet = haASRelationService.listAllHAFromDB(clusterPhyId, HaResTypeEnum.TOPIC)
                .stream()
                .map(elem -> elem.getActiveResName())
                .collect(Collectors.toSet());

        Set<String> filterTopicNameSet = new HashSet<>(filterTopicNameList);

        List<AppRelateTopicsVO> voList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry: userTopicMap.entrySet()) {
            AppRelateTopicsVO vo = new AppRelateTopicsVO();
            vo.setClusterPhyId(clusterPhyId);
            vo.setKafkaUser(entry.getKey());
            vo.setSelectedTopicNameList(new ArrayList<>());
            vo.setNotSelectTopicNameList(new ArrayList<>());
            vo.setNotHaTopicNameList(new ArrayList<>());
            entry.getValue().forEach(elem -> {
                if (elem.startsWith("__")) {
                    // ignore
                    return;
                }

                if (!haTopicNameSet.contains(elem)) {
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
}
