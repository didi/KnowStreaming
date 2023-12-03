package com.xiaojukeji.know.streaming.km.common.enums.version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaojukeji.know.streaming.km.common.utils.VersionUtil.normailze;

public enum VersionEnum {

    /**
     * 0.10.x.x
     */
    V_0_10_0_0("0.10.0.0", normailze("0.10.0.0")),
    V_0_10_0_1("0.10.0.1", normailze("0.10.0.1")),

    V_0_10_1_0("0.10.1.0", normailze("0.10.1.0")),
    V_0_10_1_1("0.10.1.1", normailze("0.10.1.1")),

    V_0_10_2_0("0.10.2.0", normailze("0.10.2.0")),
    V_0_10_2_1("0.10.2.1", normailze("0.10.2.1")),
    V_0_10_2_2("0.10.2.2", normailze("0.10.2.2")),

    /**
     * 0.11.x.x
     */
    V_0_11_0_0("0.11.0.0", normailze("0.11.0.0")),
    V_0_11_0_1("0.11.0.1", normailze("0.11.0.1")),
    V_0_11_0_2("0.11.0.2", normailze("0.11.0.2")),
    V_0_11_0_3("0.11.0.3", normailze("0.11.0.3")),

    /**
     * 1.x.x
     */
    V_1_0_0("1.0.0", normailze("1.0.0")),
    V_1_0_1("1.0.1", normailze("1.0.1")),
    V_1_0_2("1.0.2", normailze("1.0.2")),
    V_1_1_0("1.1.0", normailze("1.1.0")),
    V_1_1_1("1.1.1", normailze("1.1.1")),

    /**
     * 2.x.x
     */
    V_2_0_0("2.0.0", normailze("2.0.0")),
    V_2_0_1("2.0.1", normailze("2.0.1")),
    V_2_1_0("2.1.0", normailze("2.1.0")),
    V_2_1_1("2.1.1", normailze("2.1.1")),
    V_2_2_0("2.2.0", normailze("2.2.0")),
    V_2_2_1("2.2.1", normailze("2.2.1")),
    V_2_2_2("2.2.2", normailze("2.2.2")),
    V_2_3_0("2.3.0", normailze("2.3.0")),
    V_2_3_1("2.3.1", normailze("2.3.1")),
    V_2_4_0("2.4.0", normailze("2.4.0")),
    V_2_4_1("2.4.1", normailze("2.4.1")),

    V_2_5_0("2.5.0", normailze("2.5.0")),
    V_2_5_0_D_300("2.5.0-d-300", normailze("2.5.0-d-300")),
    V_2_5_0_D_MAX("2.5.0-d-999", normailze("2.5.0-d-999")),

    V_2_5_1("2.5.1", normailze("2.5.1")),
    V_2_6_0("2.6.0", normailze("2.6.0")),
    V_2_6_1("2.6.1", normailze("2.6.1")),
    V_2_6_2("2.6.2", normailze("2.6.2")),
    V_2_6_3("2.6.3", normailze("2.6.3")),
    V_2_7_0("2.7.0", normailze("2.7.0")),
    V_2_7_1("2.7.1", normailze("2.7.1")),
    V_2_7_2("2.7.2", normailze("2.7.2")),
    V_2_8_0("2.8.0", normailze("2.8.0")),
    V_2_8_1("2.8.1", normailze("2.8.1")),

    /**
     * 3.x.x
     */
    V_3_0_0("3.0.0", normailze("3.0.0")),

    V_3_1_0("3.1.0", normailze("3.1.0")),
    V_3_1_1("3.1.1", normailze("3.1.1")),
    V_3_1_2("3.1.2", normailze("3.1.2")),

    V_3_2_0("3.2.0", normailze("3.2.0")),
    V_3_2_1("3.2.1", normailze("3.2.1")),
    V_3_2_3("3.2.3", normailze("3.2.3")),

    V_3_3_0("3.3.0", normailze("3.3.0")),
    V_3_3_1("3.3.1", normailze("3.3.1")),
    V_3_3_2("3.3.2", normailze("3.3.2")),

    V_3_4_0("3.4.0", normailze("3.4.0")),
    V_3_4_1("3.4.1", normailze("3.4.1")),

    V_3_5_0("3.5.0", normailze("3.5.0")),
    V_3_5_1("3.5.1", normailze("3.5.1")),

    V_3_6_0("3.6.0", normailze("3.6.0")),


    V_MAX("x.x.x.x", Long.MAX_VALUE),

    ;

    private final String version;

    private final Long   versionL;

    VersionEnum(String version, Long versionL) {
        this.version  = version;
        this.versionL = versionL;
    }

    public String getVersion() {
        return version;
    }

    public Long getVersionL(){return versionL;}

    public static List<String> allVersions(){
        List<String> versions = new ArrayList<>();

        for(VersionEnum v :VersionEnum.values()){
            versions.add(v.getVersion());
        }

        return versions;
    }

    public static Map<String, Long> allVersionsWithOutMax(){
        Map<String, Long> versionMap = new HashMap<>();

        for(VersionEnum v :VersionEnum.values()){
            if(V_MAX.versionL.longValue() == v.versionL.longValue()){continue;}
            versionMap.put(v.getVersion(), v.getVersionL());
        }

        return versionMap;
    }
}
