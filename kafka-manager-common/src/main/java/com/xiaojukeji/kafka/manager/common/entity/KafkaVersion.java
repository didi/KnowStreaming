package com.xiaojukeji.kafka.manager.common.entity;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/6/15
 */
public class KafkaVersion {
    private static final String DIDI_VERSION_EXTEND = "d";

    public static final Long VERSION_0_10_3 = 10030000L; // 0.10.2+
    public static final Long VERSION_MAX = Long.MAX_VALUE;

    private volatile String version = null;

    private volatile long versionNum = Long.MAX_VALUE;

    public boolean initialized() {
        if (ValidateUtils.isNull(version)) {
            return false;
        }
        return true;
    }

    public String getVersion() {
        return version;
    }

    public long getVersionNum() {
        return versionNum;
    }

    @Override
    public String toString() {
        return "KafkaVersion{" +
                "version='" + version + '\'' +
                ", versionNum=" + versionNum +
                '}';
    }

    public long init(String version) {
        version = version.toLowerCase();
        String[] splitElems = version.split("-");
        int splitElemLength = splitElems.length;
        if (splitElemLength <= 0) {
            versionNum = Long.MAX_VALUE;
            return versionNum;
        }

        try {
            // kafka的version
            String[] kafkaVersion = splitElems[0].split("\\.");
            int kafkaVersionLength = kafkaVersion.length;

            versionNum = kafkaVersionLength > 0? Integer.valueOf(kafkaVersion[0]): 0;
            versionNum = versionNum * 100 + (kafkaVersionLength > 1? Integer.valueOf(kafkaVersion[1]): 0);
            versionNum = versionNum * 100 + (kafkaVersionLength > 2? Integer.valueOf(kafkaVersion[2]): 0);
        } catch (Exception e) {
            // Kafka版本信息获取不到时, 直接返回空
            this.versionNum = Long.MAX_VALUE;
            return versionNum;
        }

        // 成功获取版本信息
        versionNum = versionNum * 10000;
        this.version = version;

        // 补充扩展信息
        try {
            for (int idx = 0; idx < splitElemLength; ++idx) {
                if (splitElems[idx].equals(DIDI_VERSION_EXTEND) && idx < splitElemLength - 1) {
                    versionNum = versionNum + (Integer.valueOf(splitElems[idx + 1]));
                    return versionNum;
                }
            }
        } catch (Exception e) {
            // 扩展版本信息获取不到时, 忽略
        }
        return versionNum;
    }
}