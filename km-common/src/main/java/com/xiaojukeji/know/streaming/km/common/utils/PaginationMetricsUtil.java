package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationPreciseFilterFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationRangeFilterFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import com.xiaojukeji.know.streaming.km.common.enums.SortTypeEnum;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分页工具
 * @author zengqiao
 * @date 21/08/25
 */
public class PaginationMetricsUtil {
    private static final ILog log = LogFactory.getLog(PaginationMetricsUtil.class);

    private PaginationMetricsUtil() {
    }

    public static List<? extends BaseMetrics> preciseFilterMetrics(List<? extends BaseMetrics> allDataList, List<PaginationPreciseFilterFieldDTO> preciseFilterFieldDTOList) {
        return preciseFilterMetricList(allDataList, preciseFilterFieldDTOList);
    }

    public static List<? extends BaseMetrics> rangeFilterMetrics(List<? extends BaseMetrics> allDataList, List<PaginationRangeFilterFieldDTO> rangeFilterDTOList) {
        return rangeFilterMetricList(allDataList, rangeFilterDTOList);
    }

    public static <T> List<T> sortMetrics(List<T> allDataList, String metricField, String metricName, String defaultSortField, String sortType) {
        sortMetricList(allDataList, metricField, metricName, defaultSortField, sortType);

        return allDataList;
    }

    //比较metricNameList中第一个不为空的metric值。
    public static <T> void sortMetrics(List<T> allDataList, String metricField, List<String> metricNameList, String defaultSortField, String sortType) {
        sortMetricList(allDataList, metricField, metricNameList, defaultSortField, sortType);
    }

    public static List<? extends BaseMetrics> sortMetrics(List<? extends BaseMetrics> allDataList, String metricName, String defaultSortField, String sortType) {
        sortMetricList(allDataList, metricName, defaultSortField, sortType);

        return allDataList;
    }


    /**************************************************** private method ****************************************************/


    private static List<? extends BaseMetrics> preciseFilterMetricList(List<? extends BaseMetrics> allDataList, List<PaginationPreciseFilterFieldDTO> preciseFilterDTOList) {
        if (ValidateUtils.isEmptyList(allDataList) || ValidateUtils.isEmptyList(preciseFilterDTOList)) {
            return allDataList;
        }

        for (PaginationPreciseFilterFieldDTO preciseFilterFieldDTO: preciseFilterDTOList) {
            if (ValidateUtils.isEmptyList(preciseFilterFieldDTO.getFieldValueList())) {
                continue;
            }

            if (!checkMetricNameExist(allDataList, preciseFilterFieldDTO.getFieldName())) {
                continue;
            }

            List<Float> floatList = preciseFilterFieldDTO.getFieldValueList().stream().map(elem -> ConvertUtil.string2Float(elem)).filter(data -> data != null).collect(Collectors.toList());
            if (floatList == null || floatList.isEmpty()) {
                continue;
            }

            allDataList = allDataList.stream().filter(elem -> {
                Float metricValue = elem.getMetric(preciseFilterFieldDTO.getFieldName());
                if (metricValue == null) {
                    return false;
                }

                return floatList.contains(metricValue);
            }).collect(Collectors.toList());
        }

        return allDataList;
    }


    private static List<? extends BaseMetrics> rangeFilterMetricList(List<? extends BaseMetrics> allDataList, List<PaginationRangeFilterFieldDTO> rangeFilterDTOList) {
        if (ValidateUtils.isEmptyList(allDataList) || ValidateUtils.isEmptyList(rangeFilterDTOList)) {
            return allDataList;
        }

        for (PaginationRangeFilterFieldDTO rangeFilterFieldDTO: rangeFilterDTOList) {
            if (!checkMetricNameExist(allDataList, rangeFilterFieldDTO.getFieldName())) {
                continue;
            }

            Float fieldMinValue = ConvertUtil.string2Float(rangeFilterFieldDTO.getFieldMinValue());
            Float fieldMaxValue = ConvertUtil.string2Float(rangeFilterFieldDTO.getFieldMaxValue());
            if (fieldMinValue == null || fieldMaxValue == null) {
                // 值不存在或者非法，则直接忽略
                continue;
            }

            allDataList = allDataList.stream().filter(elem -> {
                Float metricValue = elem.getMetric(rangeFilterFieldDTO.getFieldName());
                if (metricValue != null && fieldMinValue <= metricValue && metricValue <= fieldMaxValue) {
                    // 满足范围要求
                    return true;
                }

                return false;
            }).collect(Collectors.toList());
        }

        return allDataList;
    }

    private static <T> List<T> sortMetricList(List<T> allDataList, String metricFieldName, String metricName, String defaultFieldName, String sortType) {
        if (ValidateUtils.anyBlank(metricName, defaultFieldName, sortType) || ValidateUtils.isEmptyList(allDataList)) {
            return allDataList;
        }

        try {
            Field metricField = FieldUtils.getField(allDataList.get(0).getClass(), metricFieldName, true);
            Field defaultField = FieldUtils.getField(allDataList.get(0).getClass(), defaultFieldName, true);
            if(ValidateUtils.anyNull(defaultField, metricField)) {
                log.debug("method=sortMetrics||className={}||metricFieldName={}||metricName={}||defaultFieldName={}||metricSortType={}||msg=field not exist.",
                        allDataList.get(0).getClass().getSimpleName(), metricFieldName, metricName, defaultFieldName, sortType);

                // 字段不存在，则排序失效，直接返回
                return allDataList;
            }

            Collections.sort(allDataList, (a1, a2) -> {
                try {
                    Object m1 = FieldUtils.readField(a1, metricField.getName(), true);
                    Object m2 = FieldUtils.readField(a2, metricField.getName(), true);

                    return sortMetricsObject((BaseMetrics)m1, (BaseMetrics)m2, metricName, defaultField);
                } catch (Exception e) {
                    log.error("method=sortMetrics||className={}||metricFieldName={}||metricName={}||defaultFieldName={}||metricSortType={}||errMsg=exception.",
                            allDataList.get(0).getClass().getSimpleName(), metricFieldName, metricName, defaultFieldName, sortType, e);
                }

                return 0;
            });
        } catch (Exception e) {
            log.error("method=sortMetrics||className={}||metricFieldName={}||metricName={}||defaultFieldName={}||metricSortType={}||errMsg=exception.",
                    allDataList.get(0).getClass().getSimpleName(), metricFieldName, metricName, defaultFieldName, sortType, e);
        }

        if (!SortTypeEnum.DESC.getSortType().equals(sortType)) {
            Collections.reverse(allDataList);
        }
        return allDataList;
    }

    private static <T> List<T> sortMetricList(List<T> allDataList, String metricFieldName, List<String> metricNameList, String defaultFieldName, String sortType) {
        if (ValidateUtils.anyBlank(defaultFieldName, sortType) || ValidateUtils.isEmptyList(allDataList)||ValidateUtils.isEmptyList(metricNameList)) {
            return allDataList;
        }

        try {
            Field metricField = FieldUtils.getField(allDataList.get(0).getClass(), metricFieldName, true);
            Field defaultField = FieldUtils.getField(allDataList.get(0).getClass(), defaultFieldName, true);
            if(ValidateUtils.anyNull(defaultField, metricField)) {
                log.debug("method=sortMetrics||className={}||metricFieldName={}||metricNameList={}||defaultFieldName={}||metricSortType={}||msg=field not exist.",
                        allDataList.get(0).getClass().getSimpleName(), metricFieldName, metricNameList, defaultFieldName, sortType);

                // 字段不存在，则排序失效，直接返回
                return allDataList;
            }

            Collections.sort(allDataList, (a1, a2) -> {
                try {
                    Object m1 = FieldUtils.readField(a1, metricField.getName(), true);
                    Object m2 = FieldUtils.readField(a2, metricField.getName(), true);

                    return compareFirstNotNullMetricValue((BaseMetrics)m1, (BaseMetrics)m2, metricNameList, defaultField);
                } catch (Exception e) {
                    log.error("method=sortMetrics||className={}||metricFieldName={}||metricNameList={}||defaultFieldName={}||metricSortType={}||errMsg=exception.",
                            allDataList.get(0).getClass().getSimpleName(), metricFieldName, metricNameList, defaultFieldName, sortType, e);
                }

                return 0;
            });
        } catch (Exception e) {
            log.error("method=sortMetrics||className={}||metricFieldName={}||metricNameList={}||defaultFieldName={}||metricSortType={}||errMsg=exception.",
                    allDataList.get(0).getClass().getSimpleName(), metricFieldName, metricNameList, defaultFieldName, sortType, e);
        }

        if (!SortTypeEnum.DESC.getSortType().equals(sortType)) {
            Collections.reverse(allDataList);
        }
        return allDataList;
    }

    private static int compareFirstNotNullMetricValue(BaseMetrics a1, BaseMetrics a2, List<String> metricNameList, Field defaultField) {
        try {
            // 指标数据排序
            Float m1 = null;
            Float m2 = null;

            //获取第一个非空指标
            for (String metric : metricNameList) {
                m1 = a1.getMetric(metric);
                if (m1 != null) {
                    break;
                }
            }
            for (String metric : metricNameList) {
                m2 = a2.getMetric(metric);
                if (m2 != null) {
                    break;
                }
            }

            if (m1 != null && m2 == null) {
                return -1;
            } else if (m1 == null && m2 != null) {
                return 1;
            } else if (m1 != null && m2 != null) {
                // 两个都不为空，则进行大小比较
                int val = compareObject(m2, m1);
                if (val != 0) {
                    return val;
                }
            }

            // 默认字段排序
            Object f1 = FieldUtils.readField(a1, defaultField.getName(), true);
            Object f2 = FieldUtils.readField(a2, defaultField.getName(), true);
            if (f1 != null && f2 != null) {
                // 两个都不为空，则进行大小比较
                return compareObject(f2, f1);
            }
            if (f1 != null) {
                return -1;
            } else if (f2 != null) {
                return 1;
            }

            return 0;
        } catch (Exception e) {
            log.debug("method=sortMetricsObject||metricsA={}||metricsB={}||metricNameList={}||defaultFieldName={}||errMsg=exception.",
                    a1, a2, metricNameList, defaultField.getName(), e);
        }

        return 0;
    }



    private static List<? extends BaseMetrics> sortMetricList(List<? extends BaseMetrics> allDataList, String metricName, String defaultSortField, String sortType) {
        if (ValidateUtils.anyBlank(metricName, defaultSortField, sortType) || ValidateUtils.isEmptyList(allDataList)) {
            return allDataList;
        }

        if (!checkMetricNameExist(allDataList, metricName)) {
            return allDataList;
        }

        try {
            Field defaultField = FieldUtils.getField(allDataList.get(0).getClass(), defaultSortField, true);
            if(ValidateUtils.anyNull(defaultField)) {
                log.debug("method=sortMetrics||className={}||metricName={}||defaultFieldName={}||metricSortType={}||msg=default field not exist.",
                        allDataList.get(0).getClass().getSimpleName(), metricName, defaultSortField, sortType);

                // 字段不存在，则排序失效，直接返回
                return allDataList;
            }

            Collections.sort(allDataList, (a1, a2) -> sortMetricsObject(a1, a2, metricName, defaultField));
        } catch (Exception e) {
            log.debug("method=sortMetrics||className={}||metricName={}||defaultFieldName={}||metricSortType={}||errMsg=exception.",
                    allDataList.get(0).getClass().getSimpleName(), metricName, defaultSortField, sortType, e);
        }

        if (!SortTypeEnum.DESC.getSortType().equals(sortType)) {
            Collections.reverse(allDataList);
        }
        return allDataList;
    }

    private static int sortMetricsObject(BaseMetrics a1, BaseMetrics a2, String metricName, Field defaultField) {
        try {
            // 指标数据排序
            Float m1 = a1.getMetric(metricName);
            Float m2 = a2.getMetric(metricName);
            if (m1 != null && m2 == null) {
                return -1;
            } else if (m1 == null && m2 != null) {
                return 1;
            } else if (m1 != null && m2 != null) {
                // 两个都不为空，则进行大小比较
                int val = compareObject(m2, m1);
                if (val != 0) {
                    return val;
                }
            }

            // 默认字段排序
            Object f1 = FieldUtils.readField(a1, defaultField.getName(), true);
            Object f2 = FieldUtils.readField(a2, defaultField.getName(), true);
            if (f1 != null && f2 != null) {
                // 两个都不为空，则进行大小比较
                return compareObject(f2, f1);
            }
            if (f1 != null) {
                return -1;
            } else if (f2 != null) {
                return 1;
            }

            return 0;
        } catch (Exception e) {
            log.debug("method=sortMetricsObject||metricsA={}||metricsB={}||metricName={}||defaultFieldName={}||errMsg=exception.",
                    a1, a2, metricName, defaultField.getName(), e);
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

    private static boolean checkMetricNameExist(List<? extends BaseMetrics> baseMetrics, String metricName) {
        return baseMetrics.stream().anyMatch(elem -> elem.getMetric(metricName) != null);
    }
}
