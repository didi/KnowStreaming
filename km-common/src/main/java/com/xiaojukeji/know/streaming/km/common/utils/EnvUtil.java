package com.xiaojukeji.know.streaming.km.common.utils;

public class EnvUtil {

    private static EnvType type = EnvType.TEST;

    public static boolean isOnline() {
        return type == EnvType.ONLINECN;
    }

    public static boolean isPre() {
        return type == EnvType.PRECN;
    }

    public static boolean isDev() {
        return type == EnvType.DEV;
    }

    public static boolean isPressure() {
        return type == EnvType.PRESSURE;
    }

    public static boolean isTest() {
        return type == EnvType.TEST;
    }

    public static boolean isStable() {
        return type == EnvType.STABLE;
    }

    public static String getStr() {
        return type.getStr();
    }

    /**
     * 设置加载的活跃的profile文件
     */
    public static void setLoadActiveProfiles(String[] activeProfiles) {
        if (activeProfiles == null || activeProfiles.length == 0) {
            return;
        }

        for (String profile : activeProfiles) {
            if (profile.contains("dev") || profile.contains("integration")) {
                type = EnvType.DEV;
            } else if (profile.contains("test")) {
                type = EnvType.TEST;
            } else if (profile.contains("stable")) {
                type = EnvType.STABLE;
            } else if (profile.contains("pressure")) {
                type = EnvType.PRESSURE;
            } else if (profile.contains("pre")) {
                type = EnvType.PRECN;
            }else if (profile.contains("online")) {
                type = EnvType.ONLINECN;
            }
        }
    }

    /**
     * 根据环境信息获取写入索引名称
     *
     * @param indexName
     * @return
     */
    public static String getWriteIndexNameByEnv(String indexName) {
        if (isPre()) {
            return "pre_".concat(indexName);
        }

        return indexName;
    }

    public enum EnvType {
        DEV("dev"),
        TEST("test"),
        STABLE("stable"),
        PRECN("pre"),
        PRESSURE("pressure"),
        ONLINECN("online");

        private String str;

        private EnvType(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public static EnvType getByStr(String str) {
            for (EnvType type : EnvType.values()) {
                if (type.str.equalsIgnoreCase(str)) {
                    return type;
                }
            }

            return null;
        }
    }
}
