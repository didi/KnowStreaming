package com.xiaojukeji.kafka.manager.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/23
 */
public class JsonUtils {
    private static final String ENUM_METHOD_VALUES = "values";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Object toJson(Class<? extends Enum> clazz) {
        try {
            Method method = clazz.getMethod(ENUM_METHOD_VALUES);
            Object invoke = method.invoke(null);

            int length = java.lang.reflect.Array.getLength(invoke);
            List<Object> values = new ArrayList<Object>();
            for (int i = 0; i < length; i++) {
                values.add(java.lang.reflect.Array.get(invoke, i));
            }
            SerializeConfig config = new SerializeConfig();
            config.configEnumAsJavaBean(clazz);
            return JSON.parseArray(JSON.toJSONString(values, config));
        } catch (Exception e) {
        }
        return "";
    }

    public static Object toJson(Enum obj) {
        try {
            SerializeConfig config = new SerializeConfig();
            config.configEnumAsJavaBean(obj.getClass());
            return JSON.parseObject(JSON.toJSONString(obj, config));
        } catch (Exception e) {
        }
        return "";
    }

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }
}