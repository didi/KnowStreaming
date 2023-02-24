package com.xiaojukeji.know.streaming.km.core.service.version;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMethodInfo;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author didi
 */
@DependsOn("versionControlService")
public abstract class BaseVersionControlService {
    protected static final ILog LOGGER = LogFactory.getLog(BaseVersionControlService.class);

    @Autowired
    protected VersionControlService versionControlService;

    /**
     * 多版本兼容类型
     * @return
     */
    protected abstract VersionItemTypeEnum getVersionItemType();

    /**
     * 注册版本兼容的执行方法，[minVersion,maxVersion) 是一个左闭右开的范围，即：minVersion<= kafkaClusterVersion < maxVersion
     * @param action
     * @param minVersion
     * @param maxVersion
     * @param methodName
     * @param func
     */
    protected void registerVCHandler(String action,
                                     VersionEnum minVersion,
                                     VersionEnum maxVersion,
                                     String methodName,
                                     Function<VersionItemParam, Object> func) {
        versionControlService.registerHandler(getVersionItemType(), action, minVersion.getVersionL(), maxVersion.getVersionL(), methodName, func);
    }

    protected void registerVCHandler(String methodName, Function<VersionItemParam, Object> func) {
        versionControlService.registerHandler(getVersionItemType(), methodName, func);
    }

    @Nullable
    protected Tuple<Object, String> doVCHandler(String version, String action, VersionItemParam param) throws VCHandlerNotExistException {
        String methodName = getMethodName(version, action);

        return new Tuple<>(versionControlService.doHandler(getVersionItemType(), methodName, param), methodName);
    }

    protected String getMethodName(String version, String action) {
        VersionControlItem item = versionControlService.getVersionControlItem(version, getVersionItemType().getCode(), action);
        if (null == item) {
            return "";
        }

        if (item.getExtend() instanceof VersionMethodInfo) {
            return ((VersionMethodInfo) item.getExtend()).getMethodName();
        }

        return "";
    }

    protected VersionJmxInfo getJMXInfo(String version, String action){
        VersionControlItem item = versionControlService.getVersionControlItem(version, getVersionItemType().getCode(), action);
        if (null == item) {
            return null;
        }

        if (item.getExtend() instanceof VersionJmxInfo) {
            return ((VersionJmxInfo) item.getExtend());
        }

        return null;
    }

    protected List<VersionControlItem> listVersionControlItems(){
        List<VersionControlItem> controlItems = versionControlService.listVersionControlItem(getVersionItemType().getCode());
        if(CollectionUtils.isEmpty(controlItems)){
            return new ArrayList<>();
        }

        return controlItems;
    }
}
