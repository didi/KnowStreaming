package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class OrderExtensionDeleteAppDTO {
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(appId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionDeleteAppDTO{" +
                "appId='" + appId + '\'' +
                '}';
    }
}