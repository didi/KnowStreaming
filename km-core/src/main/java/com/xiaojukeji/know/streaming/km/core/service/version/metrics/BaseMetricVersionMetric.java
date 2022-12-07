package com.xiaojukeji.know.streaming.km.core.service.version.metrics;

import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionConnectJmxInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMetricControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionMethodInfo;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionJmxInfo;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlMetricService;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.V_0_10_0_0;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.V_MAX;

/**
 * @author didi
 */
public abstract class BaseMetricVersionMetric implements VersionControlMetricService {

    public static final String BYTE_PER_SEC = "byte/s";

    protected VersionMetricControlItem buildAllVersionsItem(){
        VersionMetricControlItem item = new VersionMetricControlItem();
        item.setType(versionItemType());
        item.setMinVersion(V_0_10_0_0.getVersionL());
        item.setMaxVersion(V_MAX.getVersionL());
        return item;
    }

    protected VersionMetricControlItem buildAllVersionsItem(String metric, String unit){
        VersionMetricControlItem item = new VersionMetricControlItem();
        item.setType(versionItemType());
        item.setName(metric);
        item.setUnit(unit);
        item.setMinVersion(V_0_10_0_0.getVersionL());
        item.setMaxVersion(V_MAX.getVersionL());
        return item;
    }

    protected VersionMetricControlItem buildItem(String metric, String unit){
        VersionMetricControlItem item = new VersionMetricControlItem();
        item.setType(versionItemType());
        item.setName(metric);
        item.setUnit(unit);
        return item;
    }

    protected VersionMetricControlItem buildItem(){
        VersionMetricControlItem item = new VersionMetricControlItem();
        item.setType(versionItemType());
        return item;
    }

    protected VersionMethodInfo buildMethodExtend(String methodName){
        VersionMethodInfo versionMethodInfo =  new VersionMethodInfo();
        versionMethodInfo.setMethodName(methodName);
        return versionMethodInfo;
    }

    protected VersionJmxInfo buildJMXMethodExtend(String methodName){
        VersionJmxInfo jmxExtendInfo =  new VersionJmxInfo();
        jmxExtendInfo.setMethodName(methodName);
        return jmxExtendInfo;
    }

    protected VersionConnectJmxInfo buildConnectJMXMethodExtend(String methodName) {
        VersionConnectJmxInfo connectorJmxInfo = new VersionConnectJmxInfo();
        connectorJmxInfo.setMethodName(methodName);
        return connectorJmxInfo;
    }

    protected VersionConnectJmxInfo buildConnectJMXMethodExtend(String methodName, ConnectorTypeEnum type) {
        VersionConnectJmxInfo connectorJmxInfo = new VersionConnectJmxInfo();
        connectorJmxInfo.setMethodName(methodName);
        connectorJmxInfo.setType(type);
        return connectorJmxInfo;
    }
}
