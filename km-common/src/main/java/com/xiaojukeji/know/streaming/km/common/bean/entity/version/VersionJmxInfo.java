package com.xiaojukeji.know.streaming.km.common.bean.entity.version;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VersionJmxInfo extends VersionMethodInfo {
    private String jmxObjectName;
    private String jmxAttribute;

    public VersionJmxInfo(){
        super();
        this.setJmx(true);
    }

    @Override
    public VersionJmxInfo methodName(String methodName){
        this.setMethodName(methodName);
        return this;
    }

    public VersionJmxInfo jmxObjectName(String jmxObjectName){
        this.jmxObjectName  = jmxObjectName;
        return this;
    }

    public VersionJmxInfo jmxAttribute(String jmxAttribute){
        this.jmxAttribute  = jmxAttribute;
        return this;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
