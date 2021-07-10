package com.xiaojukeji.kafka.manager.task.dispatch.biz;

import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.BrokerService;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.config.RegionCapacityConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 计算Region容量
 * @author zengqiao
 * @date 20/6/30
 */
@CustomScheduled(name = "calRegionCapacity", cron = "0 0 0/12 * * ?", threadNum = 1)
public class CalRegionCapacity extends AbstractScheduledTask<RegionDO> {
    @Autowired
    private RegionService regionService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private ConfigService configService;

    private static final String REGION_CAPACITY_CONFIG_KEY = "REGION_CAPACITY_CONFIG";

    @Override
    protected List<RegionDO> listAllTasks() {
        return regionService.listAll();
    }

    @Override
    public void processTask(RegionDO regionDO) {
        List<RegionCapacityConfig> configList
                = configService.getArrayByKey(REGION_CAPACITY_CONFIG_KEY, RegionCapacityConfig.class);
        if (ValidateUtils.isNull(configList)) {
            configList = new ArrayList<>();
        }
        Map<Long, RegionCapacityConfig> configMap = new HashMap<>();
        for (RegionCapacityConfig elem: configList) {
            configMap.put(elem.getClusterId(), elem);
        }

        calAndUpdateRegionCapacity(
                regionDO,
                configMap.getOrDefault(regionDO.getClusterId(), new RegionCapacityConfig())
        );
    }

    private void calAndUpdateRegionCapacity(RegionDO regionDO, RegionCapacityConfig capacityConfig) {
        List<Integer> brokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
        long notAliveBrokerNum = PhysicalClusterMetadataManager.getNotAliveBrokerNum(regionDO.getClusterId(), brokerIdList);


        // 默认的容量是brokerNum * capacity
        regionDO.setCapacity(capacityConfig.getMaxCapacityUnitB() * brokerIdList.size());
        if (notAliveBrokerNum > 1) {
            // 挂掉的机器数大于1, 容量计算失败
            regionDO.setCapacity(-1L);
        }

        Double sumMaxAvgBytesIn = 0.0;
        for (Integer brokerId: brokerIdList) {
            sumMaxAvgBytesIn += brokerService.calBrokerMaxAvgBytesIn(
                    regionDO.getClusterId(),
                    brokerId,
                    capacityConfig.getDuration(),
                    new Date(System.currentTimeMillis() - capacityConfig.getLatestTimeUnitMs()),
                    new Date()
            );
        }
        regionDO.setRealUsed(sumMaxAvgBytesIn.longValue());
        regionDO.setEstimateUsed(sumMaxAvgBytesIn.longValue());
        regionService.updateCapacityById(regionDO);
    }
}