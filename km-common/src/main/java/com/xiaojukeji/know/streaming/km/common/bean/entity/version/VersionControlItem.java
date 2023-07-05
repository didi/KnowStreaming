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
public class VersionControlItem {

    /**
     * VersionItemTypeEnum
     */
    private int type;

    /**
     * name
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

    /**
     * 归一化之后的版本号，包含
     */
    private long minVersion;

    /**
     * 归一化之后的版本号，不包含，是一个左闭右开的空间
     */
    private long maxVersion;

    /**
     * 扩展信息：
     * 由用户自定义需要放的类型数据，获取时，自行转换
     */
    private Object extend;

    public static VersionControlItem build(){
        return new VersionControlItem();
    }

    public VersionControlItem type(int type){
        this.type   = type;
        return this;
    }

    public VersionControlItem name(String name){
        this.name   = name;
        return this;
    }

    public VersionControlItem minVersion(long minVersion){
        this.minVersion   = minVersion;
        return this;
    }

    public VersionControlItem minVersion(VersionEnum versionEnum){
        this.minVersion   = versionEnum.getVersionL();
        return this;
    }

    public VersionControlItem maxVersion(long maxVersion){
        this.maxVersion   = maxVersion;
        return this;
    }

    public VersionControlItem maxVersion(VersionEnum versionEnum){
        this.maxVersion   = versionEnum.getVersionL();
        return this;
    }

    public VersionControlItem extend(VersionJmxInfo jmxInfo){
        this.extend       = jmxInfo;
        return this;
    }

    public VersionControlItem extend(VersionMethodInfo extendInfo){
        this.extend       = extendInfo;
        return this;
    }

    public VersionControlItem extendMethod(String methodName){
        VersionMethodInfo versionMethodInfo =  new VersionMethodInfo();
        versionMethodInfo.setMethodName(methodName);
        this.extend       = versionMethodInfo;
        return this;
    }

    public VersionControlItem desc(String desc){
        this.desc         = desc;
        return this;
    }
}
