package com.xiaojukeji.kafka.manager.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
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

    public static <T> T stringToObj(String src, Class<T> clazz) {
        if (ValidateUtils.isBlank(src)) {
            return null;
        }
        return JSON.parseObject(src, clazz);
    }

    public static <T> List<T> stringToArrObj(String src, Class<T> clazz) {
        if (ValidateUtils.isBlank(src)) {
            return null;
        }
        return JSON.parseArray(src, clazz);
    }

    public static List<TopicConnectionDO> parseTopicConnections(Long clusterId, JSONObject jsonObject, long postTime) {
        List<TopicConnectionDO> connectionDOList = new ArrayList<>();
        for (String clientType: jsonObject.keySet()) {
            JSONObject topicObject = jsonObject.getJSONObject(clientType);

            // 解析单个Topic的连接信息
            for (String topicName: topicObject.keySet()) {
                JSONArray appIdArray = topicObject.getJSONArray(topicName);
                for (Object appIdDetail : appIdArray.toArray()) {
                    TopicConnectionDO connectionDO = new TopicConnectionDO();

                    String[] appIdDetailArray = appIdDetail.toString().split("#");
                    if (appIdDetailArray.length >= 3) {
                        connectionDO.setAppId(appIdDetailArray[0]);
                        connectionDO.setIp(appIdDetailArray[1]);
                        connectionDO.setClientVersion(appIdDetailArray[2]);
                    }

                    connectionDO.setClusterId(clusterId);
                    connectionDO.setTopicName(topicName);
                    connectionDO.setType(clientType);
                    connectionDO.setCreateTime(new Date(postTime));
                    connectionDOList.add(connectionDO);
                }
            }
        }
        return connectionDOList;
    }
}