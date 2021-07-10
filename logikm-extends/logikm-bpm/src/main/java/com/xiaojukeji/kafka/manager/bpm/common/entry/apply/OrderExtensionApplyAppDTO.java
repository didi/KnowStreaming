package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zhongyuankai
 * @date 2020/4/24
 */
public class OrderExtensionApplyAppDTO {
    private String name;

    private String idc;

    private String principals;

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isExistBlank(name) ||
                ValidateUtils.isNull(principals) ||
                ValidateUtils.isNull(idc)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionApplyAppDTO{" +
                "name='" + name + '\'' +
                ", idc='" + idc + '\'' +
                ", principals='" + principals + '\'' +
                '}';
    }
}
