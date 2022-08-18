package com.xiaojukeji.know.streaming.km.common.enums.cluster;

import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.Getter;

/**
 * 集群运行状态
 * @author zengqiao
 * @date 22/03/08
 */
@Getter
public enum ClusterAuthTypeEnum {
    UNKNOWN(-1, "未知"),

    NO_AUTH(0, "无认证"),

    SASL(1000, "SASL"),

    SASL_GSSAPI(1100, "SASL-Gssapi"),

    SASL_PLAIN(1200, "SASL-PLAIN"),

    SASL_SCRAM(1300, "SASL-SCRAM"),
    SASL_SCRAM_256(1301, "SASL-SCRAM-256"),
    SASL_SCRAM_512(1302, "SASL-SCRAM-512"),

    SASL_OAUTH_BEARER(1400, "OAuth 2.0模式"),

    ;

    private final int authType;

    private final String authName;

    ClusterAuthTypeEnum(int authType, String authName) {
        this.authType = authType;
        this.authName = authName;
    }

    public static ClusterAuthTypeEnum getAuthBySaslMechanism(String saslMechanismName) {
        if (ValidateUtils.isBlank(saslMechanismName)) {
            return NO_AUTH;
        }

        if ("PLAIN".equals(saslMechanismName)) {
            return SASL_PLAIN;
        }

        if ("SCRAM-SHA-256".equals(saslMechanismName)) {
            return SASL_SCRAM_256;
        }

        if ("SCRAM-SHA-512".equals(saslMechanismName)) {
            return SASL_SCRAM_512;
        }

        return UNKNOWN;
    }

    public static boolean enableAuth(Integer authType) {
        return !(UNKNOWN.getAuthType() == authType || NO_AUTH.getAuthType() == authType);
    }

    public static boolean isScram(Integer authType) {
        if (authType == null) {
            return false;
        }

        return authType.equals(SASL_SCRAM.getAuthType())
                || authType.equals(SASL_SCRAM_256.getAuthType())
                || authType.equals(SASL_SCRAM_512.getAuthType());
    }
}