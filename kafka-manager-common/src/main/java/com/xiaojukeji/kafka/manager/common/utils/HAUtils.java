package com.xiaojukeji.kafka.manager.common.utils;

public class HAUtils {
    public static String mergeKafkaUserAndClient(String kafkaUser, String clientId) {
        if (ValidateUtils.isBlank(clientId)) {
            return kafkaUser;
        }

        return String.format("%s#%s", kafkaUser, clientId);
    }

    public static Tuple<String, String> splitKafkaUserAndClient(String kafkaUserAndClientId) {
        if (ValidateUtils.isBlank(kafkaUserAndClientId)) {
            return null;
        }

        int idx = kafkaUserAndClientId.indexOf('#');
        if (idx == -1) {
            return null;
        } else if (idx == kafkaUserAndClientId.length() - 1) {
            return new Tuple<>(kafkaUserAndClientId.substring(0, idx), "");
        }

        return new Tuple<>(kafkaUserAndClientId.substring(0, idx), kafkaUserAndClientId.substring(idx + 1));
    }

    private HAUtils() {
    }
}
