package com.xiaojukeji.kafka.manager.common.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arthur
 * @date 2017/7/30.
 */
public class ListUtils {
    private static final String REGEX = ",";

    public static List<Integer> string2IntList(String str) {
        if (!StringUtils.hasText(str)) {
            return new ArrayList<>();
        }
        List<Integer> intList = new ArrayList<>();
        for (String elem :str.split(REGEX)) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            intList.add(Integer.valueOf(elem));
        }
        return intList;
    }

    public static List<Long> string2LongList(String str) {
        if (!StringUtils.hasText(str)) {
            return new ArrayList<>();
        }
        List<Long> longList = new ArrayList<>();
        for (String elem :str.split(REGEX)) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            longList.add(Long.valueOf(elem));
        }
        return longList;
    }

    public static List<String> string2StrList(String str) {
        if (!StringUtils.hasText(str)) {
            return new ArrayList<>();
        }
        List<String> strList = new ArrayList<>();
        for (String elem: str.split(REGEX)) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            strList.add(elem);
        }
        return strList;
    }

    public static String longList2String(List<Long> longList) {
        if (longList == null || longList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Long elem: longList) {
            if (elem == null) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    public static String intList2String(List<Integer> intList) {
        if (intList == null || intList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Integer elem: intList) {
            if (elem == null) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }

    public static String strList2String(List<String> strList) {
        if (strList == null || strList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String elem: strList) {
            if (!StringUtils.hasText(elem)) {
                continue;
            }
            sb.append(elem).append(REGEX);
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }
}
