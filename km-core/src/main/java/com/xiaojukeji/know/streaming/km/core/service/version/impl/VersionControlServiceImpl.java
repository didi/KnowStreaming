package com.xiaojukeji.know.streaming.km.core.service.version.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMethodInfo;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.VersionUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@Slf4j
@DependsOn("springTool")
@Service("versionControlService")
public class VersionControlServiceImpl implements VersionControlService {

    @Autowired
    private ClusterPhyService clusterPhyService;

    private final Map<Integer, List<VersionControlItem>>              versionItemMap           = new ConcurrentHashMap<>();
    private final Map<Integer, Map<String, List<VersionControlItem>>> versionItemMetricNameMap = new ConcurrentHashMap<>();

    private final Map<String, Function<VersionItemParam, Object>>      functionMap             = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        Map<String, VersionControlMetricService> itemMap = SpringTool.getBeansOfType( VersionControlMetricService.class);

        for(VersionControlMetricService service : itemMap.values()){
            versionItemMap.put(service.versionItemType(), service.init());
            versionItemMetricNameMap.put(service.versionItemType(), service.initMap());
        }
    }

    @Override
    public void registerHandler(VersionItemTypeEnum typeEnum, String methodName, Function<VersionItemParam, Object> func){
        functionMap.put(typeEnum.getCode() + "@" + methodName , func);
    }

    @Override
    public void registerHandler(VersionItemTypeEnum typeEnum, String action, long minVersion, long maxVersion,
                                String methodName, Function<VersionItemParam, Object> func){
        Integer typeCode = typeEnum.getCode();

        VersionMethodInfo versionMethodInfo =  new VersionMethodInfo();
        versionMethodInfo.setMethodName(methodName);

        VersionControlItem controlItem = new VersionControlItem();
        controlItem.name(action).minVersion(minVersion).maxVersion(maxVersion)
                .type(typeCode).extend(versionMethodInfo);

        List<VersionControlItem> controlItems = (null == versionItemMap.get(typeCode)) ?
            new CopyOnWriteArrayList<>() : versionItemMap.get(typeCode);

        controlItems.add(controlItem);
        versionItemMap.put(typeCode, controlItems);

        Map<String, List<VersionControlItem>> itemMap = new HashMap<>();
        itemMap.put(action, controlItems);
        versionItemMetricNameMap.put(typeCode, itemMap);

        functionMap.put(typeCode + "@" + methodName , func);
    }

    @Nullable
    @Override
    public Object doHandler(VersionItemTypeEnum typeEnum, String methodName, VersionItemParam param) throws VCHandlerNotExistException {
        Function<VersionItemParam, Object> func = functionMap.get(typeEnum.getCode() + "@" + methodName);
        if(null == func) {
            throw new VCHandlerNotExistException(typeEnum.getCode() + "@" + methodName);
        }

        return func.apply(param);
    }

    @Override
    public List<VersionControlItem> listVersionControlItem(Long clusterId, Integer type) {
        String versionStr  = clusterPhyService.getVersionFromCacheFirst(clusterId);
        long   versionLong = VersionUtil.normailze(versionStr);

        List<VersionControlItem> items = versionItemMap.get(type);
        if(CollectionUtils.isEmpty(items)) {
            return new ArrayList<>();
        }

        List<VersionControlItem> versionControlItems = new ArrayList<>();
        for(VersionControlItem item : items){
            if(versionLong >= item.getMinVersion() && versionLong < item.getMaxVersion()){
                versionControlItems.add(item);
            }
        }

        return versionControlItems;
    }

    @Override
    public List<VersionControlItem> listVersionControlItem(Integer type){
        return versionItemMap.get(type);
    }

    @Override
    public List<VersionControlItem> listVersionControlItem(Integer type, String itemName){
        Map<String, List<VersionControlItem>> listMap = versionItemMetricNameMap.get(type);
        return listMap.get(itemName);
    }

    @Override
    public VersionControlItem getVersionControlItem(Long clusterId, Integer type, String itemName) {
        List<VersionControlItem> items = listVersionControlItem(clusterId, type);

        for(VersionControlItem item : items){
            if(itemName.equals(item.getName())){
                return item;
            }
        }

        return null;
    }

    @Override
    public boolean isClusterSupport(Long clusterId, VersionControlItem item){
        String versionStr  = clusterPhyService.getVersionFromCacheFirst(clusterId);
        long   versionLong = VersionUtil.normailze(versionStr);

        return item.getMinVersion() <= versionLong && versionLong < item.getMaxVersion();
    }

    @Override
    public Map<String, VersionControlItem> getVersionControlItems(Long clusterId, Integer type, List<String> itemNames){
        Map<String, VersionControlItem> versionControlItemMap = new HashMap<>();

        for(String itemName : itemNames){
            VersionControlItem item = getVersionControlItem(clusterId, type, itemName);
            if(null != item){
                versionControlItemMap.put(itemName, item);
            }
        }

        return versionControlItemMap;
    }
}
