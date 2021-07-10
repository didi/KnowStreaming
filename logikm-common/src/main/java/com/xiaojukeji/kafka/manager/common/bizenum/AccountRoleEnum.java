package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 用户角色
 * @author zengqiao_cn@163.com
 * @date 19/4/15
 */
public enum AccountRoleEnum {
    UNKNOWN(-1, "unknown"),

    NORMAL(0, "normal"),

    RD(1, "rd"),

    OP(2, "op");

    private Integer role;

    private String message;

    AccountRoleEnum(Integer role, String message) {
        this.role = role;
        this.message = message;
    }

    public Integer getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "AccountRoleEnum{" +
                "role=" + role +
                ", message='" + message + '\'' +
                '}';
    }

    public static AccountRoleEnum getUserRoleEnum(Integer role) {
        for (AccountRoleEnum elem: AccountRoleEnum.values()) {
            if (elem.role.equals(role)) {
                return elem;
            }
        }
        return AccountRoleEnum.UNKNOWN;
    }

    public static AccountRoleEnum getUserRoleEnum(String roleName) {
        for (AccountRoleEnum elem: AccountRoleEnum.values()) {
            if (elem.message.equalsIgnoreCase(roleName)) {
                return elem;
            }
        }
        return AccountRoleEnum.UNKNOWN;
    }
}
