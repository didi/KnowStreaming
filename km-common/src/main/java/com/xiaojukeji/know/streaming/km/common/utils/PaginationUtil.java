package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationPreciseFilterFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.enums.SortTypeEnum;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 分页工具
 * @author zengqiao
 * @date 21/08/25
 */
public class PaginationUtil {
    private static final ILog log = LogFactory.getLog(PaginationUtil.class);

    private PaginationUtil() {
    }

    /**
     * 分页
     */
    public static <T> PaginationResult<T> pageBySubData(List<T> allDataList, PaginationBaseDTO dto) {
        return directSubList(allDataList, dto);
    }

    public static <T> List<T> directSubList(List<T> allDataList, Integer pageNo, Integer pageSize) {
        if (ValidateUtils.isNull(allDataList)) {
            allDataList = Collections.emptyList();
        }

        int startIdx = Math.min((pageNo - 1) * pageSize, allDataList.size());
        int endIdx = Math.min(startIdx + pageSize, allDataList.size());

        return allDataList.subList(startIdx, endIdx);
    }


    /**
     * 模糊搜索
     */
    public static <T> List<T> pageByFuzzyFilter(List<T> allDataList, String searchKeywords, List<String> fuzzySearchFields) {
        List<T> newAllDataList = fuzzyFilter(allDataList, fuzzySearchFields, searchKeywords);
        if (newAllDataList == null) {
            return Collections.emptyList();
        }

        return newAllDataList;
    }


    /**
     * 排序
     */
    public static <T> List<T> pageBySort(List<T> allDataList, String firstSortField, String firstSortType) {
        if (ValidateUtils.anyBlank(firstSortField, firstSortType)) {
            return allDataList;
        }

        sort(allDataList, firstSortField, firstSortType, firstSortField, firstSortType);

        return allDataList;
    }

    public static <T> List<T> pageBySort(List<T> allDataList, String firstSortField, String firstSortType, String defaultSortField, String defaultSortType) {
        if (ValidateUtils.anyBlank(firstSortField, firstSortType, defaultSortField, defaultSortType)) {
            return allDataList;
        }

        sort(allDataList, firstSortField, firstSortType, defaultSortField, defaultSortType);

        return allDataList;
    }


    /**
     * 精确过滤
     */
    public static <T> List<T> pageByPreciseFilter(List<T> allDataList, List<PaginationPreciseFilterFieldDTO> dtoList) {
        if (ValidateUtils.isEmptyList(allDataList)) {
            return new ArrayList<>();
        }

        // 精确搜索
        List<T> newAllDataList = preciseFilter(allDataList, dtoList);
        if (newAllDataList == null) {
            return new ArrayList<>();
        }

        return newAllDataList;
    }

    public static <T> List<T> pageByPreciseFilter(List<T> allDataList, String fieldName, List<String> fieldValueList) {
        if (ValidateUtils.isEmptyList(allDataList)) {
            return new ArrayList<>();
        }

        PaginationPreciseFilterFieldDTO dto = new PaginationPreciseFilterFieldDTO();
        dto.setFieldName(fieldName);
        dto.setFieldValueList(fieldValueList);

        // 精确搜索
        List<T> newAllDataList = preciseFilter(allDataList, Arrays.asList(dto));
        if (newAllDataList == null) {
            return new ArrayList<>();
        }

        return newAllDataList;
    }




    //////////
    //
    // --------数据操作子类------------------
    //
    /////////

    private static <T> List<T> fuzzyFilter(List<T> allDataList, List<String> fuzzySearchFieldList, String searchKeywords) {
        if (ValidateUtils.isEmptyList(allDataList) || ValidateUtils.isEmptyList(fuzzySearchFieldList) || ValidateUtils.isBlank(searchKeywords)) {
            return allDataList;
        }

        List<Field> fieldList = new ArrayList<>();

        for (String fuzzySearchField: fuzzySearchFieldList) {
            // step1 获取field
            Field field = FieldUtils.getField(allDataList.get(0).getClass(), fuzzySearchField, true);
            if(ValidateUtils.anyNull(field)) {
                log.debug("fuzzy filter failed, field not exist, className:{} fieldName:{}.", allDataList.get(0).getClass().getSimpleName(), fuzzySearchField);
                continue;
            }
            fieldList.add(field);
        }

        if (fieldList.isEmpty()) {
            return allDataList;
        }

        List<T> filteredDataList = new ArrayList<>();
        for (T elem: allDataList) {
            if (fuzzyFilter(elem, fieldList, searchKeywords)) {
                filteredDataList.add(elem);
            }
        }

        return filteredDataList;
    }

    private static <T> boolean fuzzyFilter(T data, List<Field> fieldList, String searchKeywords) {
        for (Field field: fieldList) {
            Object fieldValue = null;
            try {
                fieldValue = FieldUtils.readField(data, field.getName(), true);
            } catch (Exception e) {
                log.debug("fuzzy filter failed, className:{} fieldName:{}.", data.getClass().getSimpleName(), field.getName(), e);
            }

            if (null == fieldValue) {
                continue;
            }

            if (String.valueOf(fieldValue).contains(searchKeywords)) {
                return true;
            }
        }
        return false;
    }

    private static <T> List<T> preciseFilter(List<T> allDataList, List<PaginationPreciseFilterFieldDTO> filterFieldDTOList) {
        if (null == filterFieldDTOList) {
            return allDataList;
        }

        List<T> filteredDataList = allDataList;
        for (PaginationPreciseFilterFieldDTO dto: filterFieldDTOList) {
            // step1 获取field
            Field field = FieldUtils.getField(allDataList.get(0).getClass(), dto.getFieldName(), true);
            if(ValidateUtils.anyNull(field)) {
                log.debug("precise filter failed, field not exist, className:{} fieldName:{}.", allDataList.get(0).getClass().getSimpleName(), dto.getFieldName());
                continue;
            }

            filteredDataList = preciseFilter(filteredDataList, field, dto.getFieldValueList());
            if (filteredDataList.isEmpty()) {
                return filteredDataList;
            }
        }
        return filteredDataList;
    }

    private static <T> List<T> preciseFilter(List<T> allDataList, Field field, List<String> filterFieldValueList) {
        if (ValidateUtils.isEmptyList(filterFieldValueList)) {
            return allDataList;
        }

        List<T> filteredDataList = new ArrayList<>();
        for (T elem: allDataList) {
            Object fieldValue = null;
            try {
                fieldValue = FieldUtils.readField(elem, field.getName(), true);
            } catch (Exception e) {
                log.debug("precise filter failed, className:{} fieldName:{}.", allDataList.get(0).getClass().getSimpleName(), field.getName(), e);
            }

            if (null == fieldValue) {
                continue;
            }

            for (String filterFieldValue: filterFieldValueList) {
                if (String.valueOf(fieldValue).equals(filterFieldValue)) {
                    filteredDataList.add(elem);
                    break;
                }
            }

        }
        return filteredDataList;
    }

    private static <T> PaginationResult<T> directSubList(List<T> allDataList, PaginationBaseDTO dto) {
        if (ValidateUtils.isNull(allDataList)) {
            allDataList = Collections.emptyList();
        }

        int startIdx = Math.min((dto.getPageNo() - 1) * dto.getPageSize(), allDataList.size());
        int endIdx = Math.min(startIdx + dto.getPageSize(), allDataList.size());

        return PaginationResult.buildSuc(allDataList.subList(startIdx, endIdx), allDataList.size(), dto.getPageNo(), dto.getPageSize());
    }

    private static <T> List<T> sort(List<T> allDataList, String sortField, String sortType, String defaultSortField, String defaultSortType) {
        if (allDataList == null || allDataList.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            if (sortField == null) {
                // 如果排序字段不存在，则使用默认的排序字段，及默认的排序规则
                sortField = defaultSortField;
                sortType = defaultSortType;
            }

            Field firstField = FieldUtils.getField(allDataList.get(0).getClass(), sortField, true);
            Field secondField = FieldUtils.getField(allDataList.get(0).getClass(), defaultSortField, true);
            if(ValidateUtils.anyNull(firstField, secondField)) {
                log.debug("sort failed, field not exist, className:{} firstFieldName:{} secondFieldName:{}.", allDataList.get(0).getClass().getSimpleName(), sortField, defaultSortField);

                // 字段不存在，则排序失效，直接返回
                return allDataList;
            }

            Collections.sort(allDataList, (a1, a2) -> sortFuncNotCheckArgsDefaultDesc(a1, a2, firstField, secondField, false));
        } catch (Exception e) {
            log.debug("sort failed, className:{} firstFieldName:{} secondFieldName:{}.", allDataList.get(0).getClass().getSimpleName(), sortField, defaultSortField, e);
        }

        if (!SortTypeEnum.DESC.getSortType().equals(sortType)) {
            Collections.reverse(allDataList);
        }
        return allDataList;
    }

    private static int sortFuncNotCheckArgsDefaultDesc(Object a1, Object a2, Field firstField, Field secondField, boolean end) {
        try {
            Object v1 = FieldUtils.readField(a1, firstField.getName(), true);

            Object v2 = FieldUtils.readField(a2, firstField.getName(), true);

            if (v1 != null && v2 != null) {
                // 两个都不为空，则进行大小比较
                return compareObject(v2, v1);
            }

            if (v1 != null) {
                return -1;
            } else if (v2 != null) {
                return 1;
            }

            return end? 0: sortFuncNotCheckArgsDefaultDesc(a1, a2, secondField, firstField, true);
        } catch (Exception e) {
            log.debug("sort failed, className:{} firstFieldName:{} secondFieldName:{}.", a1.getClass().getSimpleName(), firstField.getName(), secondField.getName(), e);
        }
        return 0;
    }

    private static int compareObject(Object v1, Object v2) {
        if (v1 instanceof Integer) {
            return (((Integer) v1).compareTo((Integer) v2));
        }
        if (v1 instanceof Long) {
            return (((Long) v1).compareTo((Long) v2));
        }
        if (v1 instanceof Date) {
            return ((Date) v1).compareTo((Date) v2);
        }
        if (v1 instanceof Float) {
            return (((Float) v1).compareTo((Float) v2));
        }
        if (v1 instanceof Double) {
            return (((Double) v1).compareTo((Double) v2));
        }
        if (v1 instanceof String) {
            return ((String) v1).compareTo((String) v2);
        }
        return 0;
    }
}
