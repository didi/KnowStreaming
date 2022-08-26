package com.xiaojukeji.know.streaming.km.common.bean.entity.version;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionMethodInfo {

    private String methodName;

    private boolean jmx = false;

    public VersionMethodInfo methodName(String methodName){
        this.methodName  = methodName;
        return this;
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
