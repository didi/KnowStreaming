package com.xiaojukeji.know.streaming.km.core.service.version;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author didi
 */
public interface VersionControlMetricService {

    /**
     * versionItemType
     * @return
     */
    int versionItemType();

    /**
     * init
     * @return
     */
    <T extends VersionControlItem> List<T> init();

    default <T extends VersionControlItem> Map<String, List<T>> initMap() {
        Map<String, List<T>> itemMap = new HashMap<>();

        List<T> items = init();

        for(T v : items){
            String metricName = v.getName();
            List<T> temp = itemMap.getOrDefault(metricName, new ArrayList<>());

            temp.add(v);
            itemMap.put(metricName, temp);
        }

        return itemMap;
    }
}
