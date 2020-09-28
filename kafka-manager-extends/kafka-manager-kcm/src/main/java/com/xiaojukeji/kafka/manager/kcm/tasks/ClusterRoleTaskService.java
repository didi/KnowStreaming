package com.xiaojukeji.kafka.manager.kcm.tasks;

import com.xiaojukeji.kafka.manager.kcm.common.entry.ao.CreationTaskData;
import com.xiaojukeji.kafka.manager.common.bizenum.KafkaBrokerRoleEnum;
import com.xiaojukeji.kafka.manager.kcm.common.entry.ClusterTaskConstant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.AbstractClusterTaskDTO;
import com.xiaojukeji.kafka.manager.kcm.common.entry.dto.ClusterRoleTaskDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/5/20
 */
@Service(ClusterTaskConstant.CLUSTER_ROLE_BEAN_NAME)
public class ClusterRoleTaskService extends AbstractClusterTaskService {

    @Override
    public Result<CreationTaskData> getOperationHosts(AbstractClusterTaskDTO abstractClusterTaskDTO) {
        /*
         * 考虑Kafka角色所在的Broker存在交集的情况, 同时Controller会漂移的情况
         * 1. 首先定义一台机器存在多种角色, 一台机器机器只会升级一次的这种简单规则,
         *    然后, 按照刚才定义的规则, 比如要求按照A, B, C顺序升级时, 则升级顺序为A, B-A, C-A-B
         *    最后是暂停点, 任意一种角色首次出现，都会被认定为暂停点. 暂停点操作机器后暂停, 不是操作集群之前暂停.
         * 2. Controller永远放在最后升级
         */

        ClusterRoleTaskDTO dto = (ClusterRoleTaskDTO) abstractClusterTaskDTO;
        Boolean existController = Boolean.FALSE;
        if (dto.getUpgradeSequenceList().remove(KafkaBrokerRoleEnum.CONTROLLER.getRole())) {
            existController = Boolean.TRUE;
        }

        Map<String, List<String>> kafkaRoleMap = dto.getKafkaRoleBrokerHostMap();
        if (existController
                && ValidateUtils.isEmptyList(kafkaRoleMap.get(KafkaBrokerRoleEnum.CONTROLLER.getRole()))) {
            return Result.buildFrom(ResultStatus.CONTROLLER_NOT_ALIVE);
        }

        // 获取到 controller
        String controller = "";
        if (!ValidateUtils.isEmptyList(kafkaRoleMap.get(KafkaBrokerRoleEnum.CONTROLLER.getRole()))) {
            controller = kafkaRoleMap.get(KafkaBrokerRoleEnum.CONTROLLER.getRole()).get(0);
        }

        if (ValidateUtils.isNull(dto.getIgnoreList())) {
            dto.setIgnoreList(new ArrayList<>());
        }

        // 获取每个角色对应的机器
        List<String> hostList = new ArrayList<>();
        List<String> pauseList = new ArrayList<>();

        Set<String> hostSet = new HashSet<>();
        for (String kafkaRole: dto.getUpgradeSequenceList()) {
            List<String> subHostList = kafkaRoleMap.get(kafkaRole);
            if (ValidateUtils.isEmptyList(subHostList)) {
                continue;
            }
            if (subHostList.contains(controller)) {
                existController = Boolean.TRUE;
                subHostList.remove(controller);
            }

            List<String> notUsedSubHostList = subHostList
                    .stream()
                    .filter(elem -> !(hostSet.contains(elem) || dto.getIgnoreList().contains(elem)))
                    .collect(Collectors.toList());
            if (ValidateUtils.isEmptyList(notUsedSubHostList)) {
                continue;
            }
            hostSet.addAll(notUsedSubHostList);

            // 按照机器名进行排序, 尽量保证按照region进行升级
            Collections.sort(notUsedSubHostList);
            pauseList.add(notUsedSubHostList.get(0));
            hostList.addAll(notUsedSubHostList);
        }

        if (existController && !ValidateUtils.isBlank(controller)) {
            // controller 放置于最后升级
            pauseList.add(controller);
            hostList.add(controller);
        }

        CreationTaskData creationTaskDTO = new CreationTaskData();
        creationTaskDTO.setHostList(hostList);
        creationTaskDTO.setPauseList(pauseList);
        return new Result<>(creationTaskDTO);
    }
}