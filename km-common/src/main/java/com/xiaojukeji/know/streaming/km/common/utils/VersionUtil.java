package com.xiaojukeji.know.streaming.km.common.utils;

import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import org.apache.commons.lang.StringUtils;

public class VersionUtil {

    private static final int MIN_VERSION_SECTIONS_3 = 3;
    private static final int MIN_VERSION_SECTIONS_4 = 4;
    private static final String VERSION_FORMAT_3 = "%d.%d.%d";
    private static final String VERSION_FORMAT_4 = "%d.%d.%d.%d";

    public static boolean isValid(String version){
        if(StringUtils.isBlank(version)){return false;}

        String[] vers = version.split("\\.");
        if(null == vers){return false;}

        if(vers.length < MIN_VERSION_SECTIONS_3){return false;}

        for(String ver : vers){
            if(!ver.chars().allMatch(Character::isDigit)){
                return false;
            }
        }

        return true;
    }


    /**
     * 版本号归一化
     * 先考虑标准的kafka版本号，0.7.x\0.10.0.x\1.0.x\2.2.x.x
     * @param version
     * @return
     */
    public static long normailze(String version){
        if (!isValid(version)) {
            return -1;
        }

        String[] vers = version.split("\\.");

        if(MIN_VERSION_SECTIONS_3 == vers.length){
            return Long.parseLong(vers[0]) * 1000000 + Long.parseLong(vers[1]) * 10000 + Long.parseLong(vers[2]) * 100;
        }else if(MIN_VERSION_SECTIONS_4 == vers.length){
            return Long.parseLong(vers[0]) * 1000000 + Long.parseLong(vers[1]) * 10000 + Long.parseLong(vers[2]) * 100 + Long.parseLong(vers[3]);
        }

        return -1;
    }

    /**
     * 版本号反归一化
     * @param version
     * @return
     */
    public static String dNormailze(long version){
        long version4 = version % 100;
        long version3 = (version / 100) % 100;
        long version2 = (version / 10000) % 100;
        long version1 = (version / 1000000) % 100;

        if(0 == version4){
            return String.format(VERSION_FORMAT_3, version1, version2, version3);
        }else {
            return String.format(VERSION_FORMAT_4, version1, version2, version3, version4);
        }
    }

    public static void main(String[] args){
        long n1 = VersionUtil.normailze(VersionEnum.V_0_10_0_0.getVersion());
        String v1 = VersionUtil.dNormailze(n1);
        System.out.println(VersionEnum.V_0_10_0_0.getVersion() + ":" + n1 + ":" + v1);

        long n2 = VersionUtil.normailze(VersionEnum.V_0_10_0_1.getVersion());
        String v2 = VersionUtil.dNormailze(n2);
        System.out.println(VersionEnum.V_0_10_0_1.getVersion() + ":" + n2 + ":" + v2);

        long n3 = VersionUtil.normailze(VersionEnum.V_0_11_0_3.getVersion());
        String v3 = VersionUtil.dNormailze(n3);
        System.out.println(VersionEnum.V_0_11_0_3.getVersion() + ":" + n3 + ":" + v3);

        long n4 = VersionUtil.normailze(VersionEnum.V_2_5_0.getVersion());
        String v4 = VersionUtil.dNormailze(n4);
        System.out.println(VersionEnum.V_2_5_0.getVersion() + ":" + n4 + ":" + v4);
    }
}
