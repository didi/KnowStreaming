package com.xiaojukeji.know.streaming.km.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author didi
 */
public class BeanUtil {

    public static List<String> listBeanFields(Class c){
        List<String> fieldNames = new ArrayList<>();

        Field[] fields = c.getDeclaredFields();
        if(null == fields || fields.length == 0){
            return fieldNames;
        }

        for(Field field : fields){
            fieldNames.add(field.getName());
        }

        return fieldNames;
    }

    private BeanUtil() {
    }
}
