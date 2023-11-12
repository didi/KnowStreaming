package com.xiaojukeji.know.streaming.km.core.service.version;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionConnectJmxInfo;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;

/**
 * @author wyb
 * @date 2022/11/8
 */
public abstract class BaseConnectVersionControlService extends BaseVersionControlService {

    @Autowired
    ConnectClusterService connectClusterService;

    @Nullable
    protected Object doVCHandler(Long connectClusterId, String action, VersionItemParam param) throws VCHandlerNotExistException {
        String versionStr = connectClusterService.getClusterVersion(connectClusterId);

        LOGGER.debug(
                "method=doVCHandler||connectClusterId={}||action={}||type={}||param={}",
                connectClusterId, action, getVersionItemType().getMessage(), ConvertUtil.obj2Json(param)
        );

        Tuple<Object, String> ret = doVCHandler(versionStr, action, param);

        LOGGER.debug(
                "method=doVCHandler||clusterId={}||action={}||methodName={}||type={}||param={}||ret={}!",
                connectClusterId, action, ret != null ?ret.getV2(): "", getVersionItemType().getMessage(), ConvertUtil.obj2Json(param), ConvertUtil.obj2Json(ret)
        );

        return ret == null? null: ret.getV1();
    }

    @Nullable
    protected String getMethodName(Long connectClusterId, String action) {
        String versionStr = connectClusterService.getClusterVersion(connectClusterId);

        return getMethodName(versionStr, action);
    }

    @Nullable
    protected VersionConnectJmxInfo getJMXInfo(Long connectClusterId, String action) {
        String versionStr = connectClusterService.getClusterVersion(connectClusterId);

        return (VersionConnectJmxInfo) getJMXInfo(versionStr, action);
    }

}
