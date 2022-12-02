package com.xiaojukeji.know.streaming.km.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConvertUtil {
    private ConvertUtil(){}

    private static final ILog LOGGER = LogFactory.getLog(ConvertUtil.class);

    public static <T> T toObj(String json, Type resultType) {
        if (resultType instanceof Class) {
            Class<T> clazz = (Class<T>) resultType;
            return str2ObjByJson(json, clazz);
        }

        return JSON.parseObject(json, resultType);
    }

    public static <T> T str2ObjByJson(String srcStr, Class<T> tgtClass) {
        return JSON.parseObject(srcStr, tgtClass);
    }

    public static <T> T str2ObjByJson(String srcStr, TypeReference<T> tt) {
        return JSON.parseObject(srcStr, tt);
    }

    public static String obj2Json(Object srcObj) {
        if (srcObj == null) {
            return null;
        }
        if (srcObj instanceof String) {
            return (String) srcObj;
        } else {
            return JSON.toJSONString(srcObj);
        }
    }

    public static String obj2JsonWithIgnoreCircularReferenceDetect(Object srcObj) {
        return JSON.toJSONString(srcObj, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static <T> List<T> str2ObjArrayByJson(String srcStr, Class<T> tgtClass) {
        return JSON.parseArray(srcStr, tgtClass);
    }

    public static <T> T obj2ObjByJSON(Object srcObj, Class<T> tgtClass) {
        return JSON.parseObject( JSON.toJSONString(srcObj), tgtClass);
    }

    public static String list2String(List<?> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            sb.append(item).append(separator);
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public static <K, V> Map<K, V> list2Map(List<V> list, Function<? super V, ? extends K> mapper) {
        Map<K, V> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(list)) {
            for (V v : list) {
                map.put(mapper.apply(v), v);
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> list2MapParallel(List<V> list, Function<? super V, ? extends K> mapper) {
        Map<K, V> map = new ConcurrentHashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.parallelStream().forEach(v -> map.put(mapper.apply(v), v));
        }
        return map;
    }

    public static <K, V, O> Map<K, V> list2Map(List<O> list, Function<? super O, ? extends K> keyMapper,
                                               Function<? super O, ? extends V> valueMapper) {
        Map<K, V> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(list)) {
            for (O o : list) {
                map.put(keyMapper.apply(o), valueMapper.apply(o));
            }
        }
        return map;
    }

    public static <K, V> Multimap<K, V> list2MulMap(List<V> list, Function<? super V, ? extends K> mapper) {
        Multimap<K, V> multimap = ArrayListMultimap.create();
        if (CollectionUtils.isNotEmpty(list)) {
            for (V v : list) {
                multimap.put(mapper.apply(v), v);
            }
        }
        return multimap;
    }

    public static <K, V, O> Multimap<K, V> list2MulMap(List<O> list, Function<? super O, ? extends K> keyMapper,
                                                       Function<? super O, ? extends V> valueMapper) {
        Multimap<K, V> multimap = ArrayListMultimap.create();
        if (CollectionUtils.isNotEmpty(list)) {
            for (O o : list) {
                multimap.put(keyMapper.apply(o), valueMapper.apply(o));
            }
        }
        return multimap;
    }

    public static <K, V, O> Map<K, List<V>> list2MapOfList(List<O> list, Function<? super O, ? extends K> keyMapper,
                                                           Function<? super O, ? extends V> valueMapper) {
        ArrayListMultimap<K, V> multimap = ArrayListMultimap.create();
        if (CollectionUtils.isNotEmpty(list)) {
            for (O o : list) {
                multimap.put(keyMapper.apply(o), valueMapper.apply(o));
            }
        }

        return Multimaps.asMap(multimap);
    }

    public static <K, V> Set<K> list2Set(List<V> list, Function<? super V, ? extends K> mapper) {
        Set<K> set = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(list)) {
            for (V v : list) {
                set.add(mapper.apply(v));
            }
        }
        return set;
    }

    public static <T> Set<T> set2Set(Set<? extends Object> set, Class<T> tClass) {
        if (CollectionUtils.isEmpty(set)) {
            return new HashSet<>();
        }

        Set<T> result = new HashSet<>();

        for (Object o : set) {
            T t = obj2Obj(o, tClass);
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    public static <T> List<T> list2List(List<? extends Object> list, Class<T> tClass) {
        return list2List(list, tClass, (t) -> {
        });
    }

    public static <T> List<T> list2List(List<? extends Object> list, Class<T> tClass, Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }

        List<T> result = Lists.newArrayList();

        for (Object object : list) {
            T t = obj2Obj(object, tClass, consumer);
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * 对象转换工具
     * @param srcObj 元对象
     * @param tgtClass 目标对象类
     * @param <T> 泛型
     * @return 目标对象
     */
    public static <T> T obj2Obj(final Object srcObj, Class<T> tgtClass) {
        return obj2Obj(srcObj, tgtClass, (t) -> {
        });
    }

    public static <T> T obj2Obj(final Object srcObj, Class<T> tgtClass, Consumer<T> consumer) {
        if (srcObj == null) {
            return null;
        }

        T tgt = null;
        try {
            tgt = tgtClass.newInstance();
            BeanUtils.copyProperties(srcObj, tgt);
            consumer.accept(tgt);
        } catch (Exception e) {
            LOGGER.warn("method=obj2Obj||msg={}", e.getMessage());
        }

        return tgt;
    }

    public static <K, V> Map<K, V> mergeMapList(List<Map<K, V>> mapList) {
        Map<K, V> result = Maps.newHashMap();
        for (Map<K, V> map : mapList) {
            result.putAll(map);
        }
        return result;
    }

    public static Map<String, Object> Obj2Map(Object obj) {
        if (null == obj) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                LOGGER.warn("method=Obj2Map||msg={}", e.getMessage(), e);
            }
        }
        return map;
    }

    public static Object map2Obj(Map<String, Object> map, Class<?> clz) {
        Object obj = null;
        try {
            obj = clz.newInstance();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            LOGGER.warn("method=map2Obj||msg={}", e.getMessage(), e);
        }

        return obj;
    }

    public static Map<String, Double> sortMapByValue(Map<String, Double> map) {
        List<Entry<String, Double>> data = new ArrayList<>(map.entrySet());
        data.sort((o1, o2) -> {
            if ((o2.getValue() - o1.getValue()) > 0) {
                return 1;
            } else if ((o2.getValue() - o1.getValue()) == 0) {
                return 0;
            } else {
                return -1;
            }
        });

        Map<String, Double> result = Maps.newLinkedHashMap();

        for (Entry<String, Double> next : data) {
            result.put(next.getKey(), next.getValue());
        }
        return result;
    }

    public static Map<String, Object> directFlatObject(JSONObject obj) {
        Map<String, Object> ret = new HashMap<>();

        if(obj==null) {
            return ret;
        }

        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object o = entry.getValue();

            if (o instanceof JSONObject) {
                Map<String, Object> m = directFlatObject((JSONObject) o);
                for (Map.Entry<String, Object> e : m.entrySet()) {
                    ret.put(key + "." + e.getKey(), e.getValue());
                }
            } else {
                ret.put(key, o);
            }
        }

        return ret;
    }

    public static Long string2Long(String s) {
        if (ValidateUtils.isNull(s)) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static Float string2Float(String s) {
        if (ValidateUtils.isNull(s)) {
            return null;
        }
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static String float2String(Float f) {
        if (ValidateUtils.isNull(f)) {
            return null;
        }
        try {
            return String.valueOf(f);
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static Integer string2Integer(String s) {
        if (null == s) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static Double string2Double(String s) {
        if (null == s) {
            return null;
        }
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static Long double2Long(Double d) {
        if (null == d) {
            return null;
        }
        try {
            return d.longValue();
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static Long Float2Long(Float f) {
        if (null == f) {
            return null;
        }
        try {
            return f.longValue();
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    public static Integer float2Integer(Float f) {
        if (null == f) {
            return null;
        }
        try {
            return f.intValue();
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }
}
