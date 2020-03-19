package com.xiaojukeji.kafka.manager.service.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 对象成员相加的工具类
 *
 * @author tukun on 2015/11/11.
 */
public class ObjectUtil {

    /**
     * 对象o1的包含filterName的成员值与o2的成员值相加
     * 目前只支持Double类型的值相加
     * @param o1
     * @param o2
     * @param filterName
     */
    public static void add(Object o1, Object o2, String filterName) {
        if (!o1.getClass().equals(o2.getClass())) {
            return;
        }
        Field[] fields = o1.getClass().getDeclaredFields();
        List<Field> fieldList = new LinkedList<Field>(Arrays.asList(fields));
        Class superClass = o1.getClass().getSuperclass();
        while (!"Object".equals(superClass.getSimpleName())) {
            Field[] superFields = superClass.getDeclaredFields();
            fieldList.addAll(Arrays.asList(superFields));
            superClass = superClass.getSuperclass();
        }
        try {
            for (Field field : fieldList) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldName.contains(filterName)) {
                    field.set(o1, (Double) field.get(o1) + (Double) field.get(o2));
                }
            }
        } catch (IllegalAccessException e) {

        } catch (Exception e) {

        }
    }

}
