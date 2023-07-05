package com.xiaojukeji.know.streaming.km.common.enums.group;

import lombok.Getter;


/**
 * @author wyb
 * @date 2022/10/11
 */
@Getter
public enum GroupTypeEnum {

    UNKNOWN(-1, "Unknown"),

    CONSUMER(0, "Consumer客户端的消费组"),

    CONNECTOR(1, "Connector的消费组"),

    CONNECT_CLUSTER(2, "Connect集群");

    private final Integer code;

    private final String msg;

    public static final String CONNECTOR_PROTOCOL_TYPE = "consumer";

    public static final String CONNECT_CLUSTER_PROTOCOL_TYPE = "connect";

    GroupTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static GroupTypeEnum getTypeByCode(Integer code) {
        if (code == null) return UNKNOWN;
        for (GroupTypeEnum groupTypeEnum : GroupTypeEnum.values()) {
            if (groupTypeEnum.code.equals(code)) {
                return groupTypeEnum;
            }
        }
        return UNKNOWN;
    }

    public static GroupTypeEnum getTypeByProtocolType(String protocolType) {
        if (protocolType == null) {
            return UNKNOWN;
        }
        if (protocolType.isEmpty()) {
            return CONSUMER;
        } else if (CONNECTOR_PROTOCOL_TYPE.equals(protocolType)) {
            return CONNECTOR;
        } else if (CONNECT_CLUSTER_PROTOCOL_TYPE.equals(protocolType)) {
            return CONNECT_CLUSTER;
        } else {
            return UNKNOWN;
        }
    }
}
