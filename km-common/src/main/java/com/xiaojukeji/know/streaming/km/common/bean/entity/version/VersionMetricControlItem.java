package com.xiaojukeji.know.streaming.km.common.bean.entity.version;

import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionMetricControlItem extends VersionControlItem{

    public static final String CATEGORY_CLUSTER     = "Cluster";
    public static final String CATEGORY_HEALTH      = "Health";
    public static final String CATEGORY_BROKER      = "Broker";
    public static final String CATEGORY_CONSUMER    = "Consumer";
    public static final String CATEGORY_SECURITY    = "Security";
    public static final String CATEGORY_JOB         = "Job";
    public static final String CATEGORY_PARTITION   = "Partition";
    public static final String CATEGORY_PERFORMANCE = "Performance";
    public static final String CATEGORY_FLOW        = "Flow";

    public static final String CATEGORY_CLIENT      = "Client";

    /**
     * 指标单位名称，非指标的没有
     */
    private String unit;

    /**
     * 指标的类别
     */
    private String category;

    @Override
    public VersionMetricControlItem type(int type){
        this.setType(type);
        return this;
    }

    @Override
    public VersionMetricControlItem name(String name){
        setName(name);
        return this;
    }

    @Override
    public VersionMetricControlItem minVersion(long minVersion){
        setMinVersion(minVersion);
        return this;
    }

    @Override
    public VersionMetricControlItem minVersion(VersionEnum versionEnum){
        setMinVersion(versionEnum.getVersionL());
        return this;
    }

    @Override
    public VersionMetricControlItem maxVersion(long maxVersion){
        setMaxVersion(maxVersion);
        return this;
    }

    @Override
    public VersionMetricControlItem maxVersion(VersionEnum versionEnum){
        setMaxVersion(versionEnum.getVersionL());
        return this;
    }

    @Override
    public VersionMetricControlItem extend(VersionJmxInfo jmxInfo){
        setExtend(jmxInfo);
        return this;
    }

    @Override
    public VersionMetricControlItem extend(VersionMethodInfo extendInfo){
        setExtend(extendInfo);
        return this;
    }

    @Override
    public VersionMetricControlItem extendMethod(String methodName){
        VersionMethodInfo versionMethodInfo =  new VersionMethodInfo();
        versionMethodInfo.setMethodName(methodName);
        setExtend(versionMethodInfo);
        return this;
    }

    @Override
    public VersionMetricControlItem desc(String desc){
        setDesc(desc);
        return this;
    }

    public VersionMetricControlItem unit(String unit){
        this.unit         = unit;
        return this;
    }

    public VersionMetricControlItem category(String category){
        this.category     = category;
        return this;
    }
}
