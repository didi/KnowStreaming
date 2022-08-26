package com.xiaojukeji.know.streaming.km.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IndexNameUtils {

    private IndexNameUtils(){}

    private static final String VERSION_TAG = "_v";

    private static final Long ONE_DAY = 24 * 60 * 60 * 1000L;

    public static String removeVersion(String indexName) {
        if (indexName == null || indexName.length() < VERSION_TAG.length()) {
            return indexName;
        }

        int i = indexName.lastIndexOf(VERSION_TAG);
        if (i < 0) {
            return indexName;
        }


        String numStr = indexName.substring(i + VERSION_TAG.length());
        try {
            if (numStr.startsWith("+") || numStr.startsWith("-")) {
                return indexName;
            }

            Long.valueOf(numStr);
        } catch (Exception t) {
            return indexName;
        }

        return indexName.substring(0, i);
    }

    public static String genCurrentDailyIndexName(String templateName){
        return templateName + "_" + DateUtils.getFormatDayByOffset(0);
    }

    public static String genCurrentMonthlyIndexName(String templateName){
        return templateName + "_" + DateUtils.getFormatMonthByOffset(0);
    }

    public static String genIndexNameWithVersion(String indexName, Integer version) {
        if(version == null || version <= 0) {
            return indexName;
        }
        return indexName + "_v" + version;
    }

    public static String genDailyIndexNameWithVersion(String templateName, int offsetDay, Integer version){
        return genIndexNameWithVersion(genDailyIndexName(templateName, offsetDay), version);
    }

    public static String genCurrentMonthlyIndexNameWithVersion(String templateName, Integer version){
        return genIndexNameWithVersion(genCurrentMonthlyIndexName(templateName), version);
    }

    public static String genDailyIndexName(String templateName, int offsetDay){
        return templateName + "_" + DateUtils.getFormatDayByOffset(offsetDay);
    }

    //startDate、endDate 毫秒
    public static String genDailyIndexName(String templateName, Long startDate, Long endDate){
        if(startDate > endDate){
            return templateName + "_" + DateUtils.getFormatDayByOffset(0);
        }

        long currentDate     = System.currentTimeMillis();
        long offsetFromStart = (currentDate - startDate)/ONE_DAY + 1;
        long offsetFromEnd   = (currentDate - endDate)/ONE_DAY;

        List<String> indexList = new ArrayList<>();
        for(; offsetFromStart >= offsetFromEnd; offsetFromStart--){
            indexList.add(templateName + "_" + DateUtils.getFormatDayByOffset((int)offsetFromStart));
        }

        return StringUtils.join(indexList, ",");
    }

    public static boolean indexExpMatch(String index, String exp) {

        if (StringUtils.isBlank(index)) {
            return false;
        }

        if (StringUtils.isBlank(exp)) {
            return false;
        }

        int indexPointer = 0;
        int expPointer = 0;

        while (expPointer < exp.length()) {
            char expC = exp.charAt(expPointer);

            if (expC == '*') {
                expPointer++;
                boolean expPointerEnd = true;
                while (expPointer < exp.length()) {
                    expC = exp.charAt(expPointer);
                    if (expC != '*') {
                        expPointerEnd = false;
                        break;
                    }

                    expPointer++;
                }

                // * is the last char in exp
                if (expPointerEnd) {
                    return true;
                }

                int nextStar = exp.indexOf('*', expPointer);
                String expInter = null;
                if (nextStar < 0) {
                    expInter = exp.substring(expPointer);
                } else {
                    expInter = exp.substring(expPointer, nextStar);
                }

                int indexPos = index.indexOf(expInter, indexPointer);
                if (indexPos <= 0) {
                    return false;
                }

                expPointer = expPointer + expInter.length();
                indexPointer = indexPos + expInter.length();

            } else if (indexPointer < index.length()) {
                char indexC = index.charAt(indexPointer);
                if (indexC != expC) {
                    // not the same, failed
                    return false;
                }

                indexPointer++;
                expPointer++;
            } else {
                return false;
            }
        }

        if (indexPointer < index.length()) {
            // index has chars left
            return false;
        } else {
            // index also to end
            return true;
        }
    }

    /**
     * 查询的索引名称是否匹配到索引模板
     *
     * @param searchIndexName
     * @param templateExp
     * @return
     */
    public static boolean isIndexNameMatchTemplateExp(final String searchIndexName, final String templateExp) {

        if (StringUtils.isBlank(searchIndexName) || StringUtils.isBlank(templateExp)) {
            return false;
        }

        String tmpTemplateExp = templateExp;

        // 索引表达式不是以*结尾，单个索引没有分区
        if (!tmpTemplateExp.endsWith("*")) {
            return isSearchIndexNameMatchNoExpTemplate(searchIndexName, tmpTemplateExp);
        }

        // 1. 去除索引模板表达式中的结尾*
        tmpTemplateExp = StringUtils.removeEnd(tmpTemplateExp , "*");
        // 2. 用*来切分查询使用的索引名称
        String[] indexSplits = StringUtils.split(searchIndexName , "*");

        if (indexSplits == null || indexSplits.length == 0) {
            return false;
        }

        // 查询使用的索引名称不包含*
        if (!searchIndexName.endsWith("*") && indexSplits.length <= 1) {

            String trimSearchIndexName = StringUtils.removeEnd(searchIndexName , "*");
            // 去掉_vx版本号信息
            String lastStr = removeIndexNameVersionIfHas(trimSearchIndexName);

            // 去掉查询索引中的索引表达式部分
            String leftStr = lastStr.replace(tmpTemplateExp, "");
            if (StringUtils.isBlank(leftStr)) {
                return true;
            }

            // 剩余的为时间
            if (isNumbericOrSpecialChar(leftStr)) {
                return true;
            }

            // 去掉_vx版本号信息
            tmpTemplateExp = removeIndexNameVersionIfHas(tmpTemplateExp);
            return lastStr.equals(tmpTemplateExp);

            // 查询使用的索引名称含有*，例如btb_b2b.crius*hna*_2019-03-07
        } else {
            String lastStr = indexSplits[indexSplits.length - 1];
            // 去掉_vx版本号信息
            lastStr = removeIndexNameVersionIfHas(lastStr);
            indexSplits[indexSplits.length - 1] = lastStr;

            // lastStr是日期,除掉最后一个进行匹配
            if (isNumbericOrSpecialChar(lastStr)
                    && isMatchIndexPartPositiveSequence(tmpTemplateExp, indexSplits, indexSplits.length - 1)) {
                return true;
            }

            // 去掉lastStr日期部分
            lastStr = removeIndexNameDateIfHas(lastStr);
            if (StringUtils.isBlank(lastStr)) {

                return isMatchIndexPartPositiveSequence(tmpTemplateExp, indexSplits, indexSplits.length - 1);
            } else {
                indexSplits[indexSplits.length - 1] = lastStr;

                return isMatchIndexPartPositiveSequence(tmpTemplateExp, indexSplits, indexSplits.length);
            }
        }
    }

    /**
     * 部分索引切片匹配索引模板是否为正序
     *
     * 例如templateExp为ABCDEFG,partIndexName为AB,DE,FG。则匹配
     *
     * @param templateExp
     * @param partIndexName
     * @return
     */
    public static boolean isMatchIndexPartPositiveSequence(String templateExp, String[] partIndexName, int compareCount) {
        // 存在子字符串序号
        List<Integer> indexList = Lists.newArrayList();

        for (int i = 0; i < partIndexName.length && i < compareCount; ++i) {
            indexList.add(templateExp.indexOf(partIndexName[i]));
        }

        Integer lastIndex = null;
        for (int i = 0; i < indexList.size(); ++i) {
            // 不包含其中一个子串，则整体不匹配
            if (indexList.get(i) < 0) {
                return false;
            }
            if (lastIndex == null) {
                lastIndex = indexList.get(i);
            } else {
                if (indexList.get(i) >= lastIndex) {
                    lastIndex = indexList.get(i);
                } else {
                    // 后一个的序号小于前一个，则整体不匹配
                    return false;
                }
            }
        }

        // 如果只有一个序号, 匹配部分为索引表达式中间
        if (indexList.size() == 1 && indexList.get(0) > 0) {
            return false;
        }

        return true;
    }

    /**
     * 如果有版本号信息，则移除版本号字符串
     *
     * @param lastIndexNamePart
     * @return
     */
    public static String removeIndexNameVersionIfHas(String lastIndexNamePart) {
        // 是否含有_vx版本号数据
        int index = lastIndexNamePart.lastIndexOf("_v");
        if (index > 0) {
            // 去掉_v
            String endStr = lastIndexNamePart.substring(index + 2);
            // _v之后是不是数字
            if (StringUtils.isNumeric(endStr)) {
                // 是数字,去掉版本号
                return lastIndexNamePart.substring(0, index);
            }
        }

        return lastIndexNamePart;
    }

    /**
     * 是否是数字或者特殊字符
     *
     * @param lastIndexNamePart
     * @return
     */
    public static boolean isNumbericOrSpecialChar(String lastIndexNamePart) {
        for (int i = 0; i < lastIndexNamePart.length(); ++i) {
            char c = lastIndexNamePart.charAt(i);
            // 是数字或者-,_
            if (Character.isDigit(c) || c == '-' || c == '_') {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * 如果有日期信息，则移除日期
     *
     * @param lastIndexNamePart
     * @return
     */
    public static String removeIndexNameDateIfHas(String lastIndexNamePart) {

        int index = lastIndexNamePart.lastIndexOf("_");
        if (index >= 0) {
            String endStr = lastIndexNamePart.substring(index + 1);
            // 剩余部分是日期，则移除
            if (isNumbericOrSpecialChar(endStr)) {
                return lastIndexNamePart.substring(0, index);
            }
        } else {
            if (isNumbericOrSpecialChar(lastIndexNamePart)) {
                return "";
            }
        }

        return lastIndexNamePart;
    }

    /**
     * 查询索引是否匹配没有时间分区的索引模板表达式
     *
     * @param searchIndexName
     * @param templateExp  索引表达式不带*
     * @return
     */
    public static boolean isSearchIndexNameMatchNoExpTemplate(final String searchIndexName, final String templateExp) {

        if (StringUtils.isBlank(searchIndexName) || StringUtils.isBlank(templateExp)) {
            return false;
        }

        // 1. 用*来切分查询使用的索引名称
        String[] indexSplits = StringUtils.split(searchIndexName , "*");

        if (indexSplits == null || indexSplits.length == 0) {
            return false;
        }

        // 查询使用的索引名称不包含*
        if (!searchIndexName.endsWith("*") && indexSplits.length <= 1) {
            String trimSearchIndexName = StringUtils.removeEnd(searchIndexName , "*");
            // 如果查询索引名称和索引表达式相同，例如arius.dsl.template -> arius.dsl.template
            return trimSearchIndexName.equals(templateExp);
        } else {
            // 查看*分隔之后最后一个分段

            // 去掉_vx版本号信息
            String lastStr = removeIndexNameVersionIfHas(indexSplits[indexSplits.length - 1]);
            indexSplits[indexSplits.length - 1] = lastStr;

            // *之后为空串
            if (StringUtils.isBlank(lastStr)) {
                // 各个分段进行匹配
                return isMatchIndexPartPositiveSequence(templateExp, indexSplits, indexSplits.length - 1);
            }

            // 如果lastStr包含_，而索引表达式不包含_
            if (lastStr.contains("_") && !templateExp.contains("_")) {
                return false;
            }

            // lastStr是日期
            if (isNumbericOrSpecialChar(lastStr)) {
                return false;
            }

            // 各个分段进行匹配
            return isMatchIndexPartPositiveSequence(templateExp, indexSplits, indexSplits.length);
        }
    }
}
