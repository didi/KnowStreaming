package com.xiaojukeji.know.streaming.km.common.component;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author linyunan
 * @date 2021-04-26
 */
@Component
public class HandleFactory extends BaseExtendFactory {

    private Map<String, BaseHandle> baseHandleMap = Maps.newConcurrentMap();

    /**
     * 根据类名前缀获取接口BaseHandle的实现类实例
     * 注意： handleNamePre 需要保证全局唯一
     */
    public BaseHandle getByHandlerNamePer(String handleNamePre) {
        if (baseHandleMap.containsKey(handleNamePre)) {
            return baseHandleMap.get(handleNamePre);
        } else {
            BaseHandle baseHandle = getByClassNamePer(handleNamePre, BaseHandle.class);
            baseHandleMap.put(handleNamePre, baseHandle);
            return baseHandle;
        }
    }
}
