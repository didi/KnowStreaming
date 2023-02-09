package com.xiaojukeji.know.streaming.km.common.utils;

import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import org.apache.commons.lang.StringUtils;


public class VersionUtil {
    /**
     * apache的kafka相关的版本信息
     */
    private static final long   BASE_VAL            = 10000L;
    private static final long   APACHE_STEP_VAL     = 100L;
    public static final long    APACHE_MAX_VAL      = 100000000L;

    private static final int MIN_VERSION_SECTIONS_3 = 3;
    private static final int MIN_VERSION_SECTIONS_4 = 4;
    private static final String VERSION_FORMAT_3 = "%d.%d.%d";
    private static final String VERSION_FORMAT_4 = "%d.%d.%d.%d";

    /**
     * XiaoJu的kafka相关的版本信息
     */
    private static final String XIAO_JU_VERSION_FEATURE     = "-d-";
    private static final String XIAO_JU_VERSION_FORMAT_4    = "%d.%d.%d-d-%d";


    /**
     * 版本号归一化
     * 先考虑标准的kafka版本号，0.7.x\0.10.0.x\1.0.x\2.2.x.x
     * @param version
     * @return
     */
    public static long normailze(String version) {
        if(StringUtils.isBlank(version)) {
            return -1;
        }

        if (version.contains(XIAO_JU_VERSION_FEATURE)) {
            // XiaoJu的kafka
            return normalizeXiaoJuVersion(version);
        }

        // 检查是否合法
        String[] vers = version.split("\\.");
        if(vers.length < MIN_VERSION_SECTIONS_3) {
            return -1;
        }
        for(String ver : vers){
            if(!ver.chars().allMatch(Character::isDigit)){
                return -1;
            }
        }

        // 转为数字
        long val = -1;
        if(MIN_VERSION_SECTIONS_3 == vers.length) {
            val = Long.parseLong(vers[0]) * APACHE_STEP_VAL * APACHE_STEP_VAL * APACHE_STEP_VAL + Long.parseLong(vers[1]) * APACHE_STEP_VAL * APACHE_STEP_VAL + Long.parseLong(vers[2]) * APACHE_STEP_VAL;
        } else if(MIN_VERSION_SECTIONS_4 == vers.length) {
            val = Long.parseLong(vers[0]) * APACHE_STEP_VAL * APACHE_STEP_VAL * APACHE_STEP_VAL + Long.parseLong(vers[1]) * APACHE_STEP_VAL * APACHE_STEP_VAL + Long.parseLong(vers[2]) * APACHE_STEP_VAL + Long.parseLong(vers[3]);
        }

        return val == -1? val: val * BASE_VAL;
    }

    public static long normalizeXiaoJuVersion(String version) {
        if(StringUtils.isBlank(version)) {
            return -1;
        }

        if (!version.contains(XIAO_JU_VERSION_FEATURE)) {
            // 非XiaoJu的kafka
            return normailze(version);
        }

        String[] vers = version.split(XIAO_JU_VERSION_FEATURE);
        if (vers.length < 2) {
            return -1;
        }

        long apacheVal = normailze(vers[0]);
        if (apacheVal == -1) {
            return apacheVal;
        }

        Long xiaoJuVal = ConvertUtil.string2Long(vers[1]);
        if (xiaoJuVal == null) {
            return apacheVal;
        }

        return apacheVal + xiaoJuVal;
    }

    /**
     * 版本号反归一化
     * @param version
     * @return
     */
    public static String dNormailze(long version) {
        long version4 = (version / BASE_VAL) % APACHE_STEP_VAL;
        long version3 = (version / BASE_VAL / APACHE_STEP_VAL) % APACHE_STEP_VAL;
        long version2 = (version / BASE_VAL / APACHE_STEP_VAL / APACHE_STEP_VAL) % APACHE_STEP_VAL;
        long version1 = (version / BASE_VAL / APACHE_STEP_VAL / APACHE_STEP_VAL / APACHE_STEP_VAL) % APACHE_STEP_VAL;

        if (version % BASE_VAL != 0) {
            return String.format(XIAO_JU_VERSION_FORMAT_4, version1, version2, version3, version % BASE_VAL);
        } else if (0 == version4) {
            return String.format(VERSION_FORMAT_3, version1, version2, version3);
        } else {
            return String.format(VERSION_FORMAT_4, version1, version2, version3, version4);
        }
    }

    public static void main(String[] args){
        long n1 = VersionUtil.normailze(VersionEnum.V_0_10_0_0.getVersion());
        String v1 = VersionUtil.dNormailze(n1);
        System.out.println(VersionEnum.V_0_10_0_0.getVersion() + "\t:\t" + n1 + "\t:\t" + v1);

        long n2 = VersionUtil.normailze(VersionEnum.V_0_10_0_1.getVersion());
        String v2 = VersionUtil.dNormailze(n2);
        System.out.println(VersionEnum.V_0_10_0_1.getVersion() + "\t:\t" + n2 + "\t:\t" + v2);

        long n3 = VersionUtil.normailze(VersionEnum.V_0_11_0_3.getVersion());
        String v3 = VersionUtil.dNormailze(n3);
        System.out.println(VersionEnum.V_0_11_0_3.getVersion() + "\t:\t" + n3 + "\t:\t" + v3);

        long n4 = VersionUtil.normailze(VersionEnum.V_2_5_0.getVersion());
        String v4 = VersionUtil.dNormailze(n4);
        System.out.println(VersionEnum.V_2_5_0.getVersion() + "\t:\t" + n4 + "\t:\t" + v4);

        long n5 = VersionUtil.normailze(VersionEnum.V_2_5_0_D_300.getVersion());
        String v5 = VersionUtil.dNormailze(n5);
        System.out.println(VersionEnum.V_2_5_0_D_300.getVersion() + "\t:\t" + n4 + "\t:\t" + v5);

        System.out.println(Long.MAX_VALUE);
    }
}
