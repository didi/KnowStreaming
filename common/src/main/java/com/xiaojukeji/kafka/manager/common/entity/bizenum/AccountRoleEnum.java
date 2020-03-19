package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * 用户角色
 * @author zengqiao_cn@163.com
 * @date 19/4/15
 */
public enum AccountRoleEnum {
    UNKNOWN(-1),

    NORMAL(0),

    SRE(1),

    ADMIN(2);

    private Integer role;

    AccountRoleEnum(Integer role) {
        this.role = role;
    }

    public Integer getRole() {
        return role;
    }

    public static AccountRoleEnum getUserRoleEnum(Integer role) {
        for (AccountRoleEnum elem: AccountRoleEnum.values()) {
            if (elem.getRole().equals(role)) {
                return elem;
            }
        }
        return null;
    }
}
