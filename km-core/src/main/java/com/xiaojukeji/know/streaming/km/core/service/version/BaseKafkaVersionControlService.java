package com.xiaojukeji.know.streaming.km.core.service.version;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;

/**
 * @author didi
 */
public abstract class BaseKafkaVersionControlService extends BaseVersionControlService{
    @Autowired
    private ClusterPhyService clusterPhyService;

    @Nullable
    protected Object doVCHandler(Long clusterPhyId, String action, VersionItemParam param) throws VCHandlerNotExistException {
        String versionStr  = clusterPhyService.getVersionFromCacheFirst(clusterPhyId);

        LOGGER.info(
                "method=doVCHandler||clusterId={}||action={}||type={}||param={}",
                clusterPhyId, action, getVersionItemType().getMessage(), ConvertUtil.obj2Json(param)
        );

        Tuple<Object, String> ret = doVCHandler(versionStr, action, param);

        LOGGER.debug(
                "method=doVCHandler||clusterId={}||action={}||methodName={}||type={}||param={}||ret={}!",
                clusterPhyId, action, ret != null ?ret.getV2(): "", getVersionItemType().getMessage(), ConvertUtil.obj2Json(param), ConvertUtil.obj2Json(ret)
        );

        return ret == null? null: ret.getV1();
    }

    @Nullable
    protected String getMethodName(Long clusterPhyId, String action) {
        String versionStr  = clusterPhyService.getVersionFromCacheFirst(clusterPhyId);

        return getMethodName(versionStr, action);
    }

    @Nullable
    protected VersionJmxInfo getJMXInfo(Long clusterPhyId, String action){
        String versionStr  = clusterPhyService.getVersionFromCacheFirst(clusterPhyId);

        return getJMXInfo(versionStr, action);
    }
}
