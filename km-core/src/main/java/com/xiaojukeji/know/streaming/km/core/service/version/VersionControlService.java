package com.xiaojukeji.know.streaming.km.core.service.version;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;

import java.util.List;
import java.util.function.Function;

/**
 * @author didi
 */
public interface VersionControlService {

    /**
     * registerHandler
     * @param typeEnum
     * @param method
     * @param func
     */
    void registerHandler(VersionItemTypeEnum typeEnum, String method, Function<VersionItemParam, Object> func);


    /**
     * registerHandler
     * @param typeEnum
     * @param action
     * @param minVersion
     * @param maxVersion
     * @param methodName
     * @param func
     */
    void registerHandler(VersionItemTypeEnum typeEnum, String action, long minVersion, long maxVersion,
                         String methodName, Function<VersionItemParam, Object> func);
    /**
     * handler
     * @param typeEnum
     * @param methodName
     * @param param
     * @return
     */
    Object doHandler(VersionItemTypeEnum typeEnum, String methodName, VersionItemParam param) throws VCHandlerNotExistException;

    /**
     * 获取对应集群的版本兼容项
     * @param version
     * @param type
     * @return
     */
    List<VersionControlItem> listVersionControlItem(String version, Integer type);

    /**
     * 获取对应type所有的的版本兼容项
     * @param type
     * @return
     */
    List<VersionControlItem> listVersionControlItem(Integer type);

    /**
     * 查询对应指标的版本兼容项
     * @param type
     * @param itemName
     * @return
     */
    List<VersionControlItem> listVersionControlItem(Integer type, String itemName);

    /**
     * 查询对应指标的版本兼容项
     * @param version
     * @param type
     * @param itemName
     * @return
     */
    VersionControlItem getVersionControlItem(String version, Integer type, String itemName);

    /**
     * 判断 item 是否被 clusterId 对应的版本支持
     * @param version
     * @param item
     * @return
     */
    boolean isClusterSupport(String version, VersionControlItem item);
}
