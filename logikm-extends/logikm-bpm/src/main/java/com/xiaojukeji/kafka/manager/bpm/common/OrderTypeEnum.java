package com.xiaojukeji.kafka.manager.bpm.common;

/**
 * 工单类型
 * @author zengqiao
 * @date 19/6/23
 */
public enum OrderTypeEnum {
    APPLY_TOPIC                 (00,     "Topic申请", "applyTopicOrder"),
    DELETE_TOPIC                (10,     "Topic下线", "deleteTopicOrder"),
    THIRD_PART_DELETE_TOPIC     (20,     "第三方Topic下线申请", "thirdPartDeleteTopicOrder"),

    APPLY_APP                   (01,     "应用申请", "applyAppOrder"),
    DELETE_APP                  (11,     "应用下线", "deleteAppOrder"),

    APPLY_QUOTA                 (02,     "配额申请", "applyQuotaOrder"),
    APPLY_PARTITION             (12,     "分区申请", "applyPartitionOrder"),

    APPLY_AUTHORITY             (03,     "权限申请", "applyAuthorityOrder"),
    DELETE_AUTHORITY            (13,     "权限删除", "deleteAuthorityOrder"),

    APPLY_CLUSTER               (04,     "集群申请", "applyClusterOrder"),
    DELETE_CLUSTER              (14,     "集群下线", "deleteClusterOrder"),

    APPLY_EXPAND_CLUSTER        (05,     "集群扩容", "modifyClusterOrder"),
    APPLY_REDUCE_CLUSTER        (15,     "集群缩容", "modifyClusterOrder"),

    ADD_GATEWAY_CONFIG          (06,     "增加GateWay配置", "addGatewayConfigOrder"),
    DELETE_GATEWAY_CONFIG       (16,     "删除GateWay配置", "deleteGatewayConfigOrder"),
    MODIFY_GATEWAY_CONFIG       (26,     "修改GateWay配置", "modifyGatewayConfigOrder"),

    ;

    private Integer code;

    private String message;

    private String orderName;

    OrderTypeEnum(Integer code, String message, String orderName) {
        this.code = code;
        this.message = message;
        this.orderName = orderName;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderName() {
        return orderName;
    }

    public static OrderTypeEnum getByTypeCode(Integer code) {
        for (OrderTypeEnum elem : OrderTypeEnum.values()) {
            if (elem.getCode().equals(code)) {
                return elem;
            }
        }
        return null;
    }
}
