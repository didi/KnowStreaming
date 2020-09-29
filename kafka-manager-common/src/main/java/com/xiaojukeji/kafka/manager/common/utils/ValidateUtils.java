package com.xiaojukeji.kafka.manager.common.utils;

import com.xiaojukeji.kafka.manager.common.bizenum.IDCEnum;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengqiao
 * @date 20/4/16
 */
public class ValidateUtils {
    /**
     * 为空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 是空字符串或者空
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 存在空
     */
    public static boolean isExistBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是空字符串
     */
    public static boolean equalList(List<Object> seq1, List<Object> seq2) {
        if (isNull(seq1) && isNull(seq2)) {
            return true;
        } else if (isNull(seq1) || isNull(seq2) || seq1.size() != seq2.size()) {
            return false;
        }
        for (Object elem: seq1) {
            if (!seq2.contains(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyList(List<?> seq) {
        return isNull(seq) || seq.isEmpty();
    }

    public static boolean isEmptySet(Set<?> seq) {
        return isNull(seq) || seq.isEmpty();
    }

    public static boolean isEmptyMap(Map<?, ?> seq) {
        return isNull(seq) || seq.isEmpty();
    }

    public static boolean isNullOrLessThanZero(Long value) {
        return value == null || value < 0;
    }

    public static boolean isNullOrLessThanZero(Integer value) {
        return value == null || value < 0;
    }

    public static boolean isNullOrLessThanZero(Double value) {
        return value == null || value < 0;
    }

    public static boolean topicNameLegal(String idc, String topicName) {
        if (ValidateUtils.isNull(idc) || ValidateUtils.isNull(topicName)) {
            return false;
        }

        // 校验Topic的长度
        if (topicName.length() >= TopicCreationConstant.TOPIC_NAME_MAX_LENGTH) {
            return false;
        }

        // 校验前缀
        if (IDCEnum.CN.getIdc().equals(idc) ||
                (IDCEnum.US.getIdc().equals(idc) && topicName.startsWith(TopicCreationConstant.TOPIC_NAME_PREFIX_US)) ||
                (IDCEnum.RU.getIdc().equals(idc) && topicName.startsWith(TopicCreationConstant.TOPIC_NAME_PREFIX_RU))) {
            return true;
        }
        return false;
    }
}