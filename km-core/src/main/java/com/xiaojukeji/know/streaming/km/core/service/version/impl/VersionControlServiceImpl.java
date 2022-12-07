package com.xiaojukeji.know.streaming.km.core.service.version.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMethodInfo;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.VersionUtil;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
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

@DependsOn("springTool")
@Service("versionControlService")
public class VersionControlServiceImpl implements VersionControlService {
    /**
     * key：versionItemType
     */
    private final Map<Integer, List<VersionControlItem>>                versionItemMap           = new ConcurrentHashMap<>();

    /**
     * key：versionItemType
     * key1：metricName
     */
    private final Map<Integer, Map<String, List<VersionControlItem>>>   versionItemMetricNameMap = new ConcurrentHashMap<>();

    /**
     * key : VersionItemTypeEnum.code@methodName
     */
    private final Map<String, Function<VersionItemParam, Object>>       functionMap              = new ConcurrentHashMap<>();

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
        functionMap.put(versionFunctionKey(typeEnum.getCode(), methodName), func);
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

        functionMap.put(versionFunctionKey(typeCode, methodName), func);
    }

    @Nullable
    @Override
    public Object doHandler(VersionItemTypeEnum typeEnum, String methodName, VersionItemParam param) throws VCHandlerNotExistException {
        Function<VersionItemParam, Object> func = functionMap.get(versionFunctionKey(typeEnum.getCode(), methodName));
        if(null == func) {
            throw new VCHandlerNotExistException(versionFunctionKey(typeEnum.getCode(), methodName));
        }

        return func.apply(param);
    }

    @Override
    public List<VersionControlItem> listVersionControlItem(String version, Integer type) {
        long versionLong = VersionUtil.normailze(version);

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
    public VersionControlItem getVersionControlItem(String version, Integer type, String itemName) {
        List<VersionControlItem> items = listVersionControlItem(version, type);

        for(VersionControlItem item : items){
            if(itemName.equals(item.getName())){
                return item;
            }
        }

        return null;
    }

    @Override
    public boolean isClusterSupport(String version, VersionControlItem item) {
        long   versionLong = VersionUtil.normailze(version);
        return item.getMinVersion() <= versionLong && versionLong < item.getMaxVersion();
    }

    /**************************************************** private method ****************************************************/
    private String versionFunctionKey(int typeCode, String methodName){
        return typeCode + "@" + methodName;
    }
}
