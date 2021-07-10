package com.xiaojukeji.kafka.manager.common.utils;

import com.xiaojukeji.kafka.manager.common.exception.CopyException;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象复制新类型和同类型深度克隆工具类
 * @author huangyiminghappy@163.com
 * @date 2019/3/15
 */
public class CopyUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> T deepCopy(T obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return (T)(String) obj;
        } else if (obj instanceof Integer) {
            return (T)(Integer) obj;
        } else if (obj instanceof Double) {
            return (T)(Double) obj;
        } else if (obj instanceof Byte) {
            return (T)(Byte) obj;
        } else if (obj instanceof Short) {
            return (T)(Short) obj;
        } else if (obj instanceof Long) {
            return (T)(Long) obj;
        } else if (obj instanceof Float) {
            return (T)(Float) obj;
        } else if (obj instanceof Character) {
            return (T)(Character) obj;
        } else if (obj instanceof Boolean) {
            return (T)(Boolean) obj;
        } else if (obj instanceof ArrayList<?>) {
            return (T) arrayListHandler((ArrayList<?>) obj);
        } else if (obj instanceof HashMap<?, ?>) {
            return (T) mapHandler((Map<?, ?>) obj);
        } else if (obj instanceof ConcurrentHashMap<?, ?>) {
            return (T) concurrentMapHandler((Map<?, ?>) obj);
        } else if (obj instanceof TreeMap<?, ?>) {
            return (T) treeMapHandler((Map<?, ?>) obj);
        } else if (obj instanceof LinkedList<?>) {
            return (T) linkedListHandler((LinkedList<?>) obj);
        } else if (obj instanceof HashSet<?>) {
            return (T) hashSetHandler((HashSet<?>) obj);
        } else if (isPrimitiveArray(obj)) {
            return getPrimitiveArray(obj);
        }

        T finObj = null;
        Class rezClass = obj.getClass();
        rezClass.cast(finObj);
        try {
            Constructor<T> constructor = getCompleteConstructor(rezClass);
            finObj = (T) constructor.newInstance(getParamsObjForConstructor(rezClass));
            copyFields(rezClass, obj, finObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finObj;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> T deepCopy(T obj, Object parrent) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return (T)String.valueOf((String) obj);
        } else if (obj instanceof Integer) {
            return (T)Integer.valueOf((Integer) obj);
        } else if (obj instanceof Double) {
            return (T)Double.valueOf((Double) obj);
        } else if (obj instanceof Byte) {
            return (T)Byte.valueOf((Byte) obj);
        } else if (obj instanceof Short) {
            return (T)Short.valueOf((Short) obj);
        } else if (obj instanceof Long) {
            return (T)Long.valueOf((Long) obj);
        } else if (obj instanceof Float) {
            return (T)Float.valueOf((Float) obj);
        } else if (obj instanceof Character) {
            return (T)Character.valueOf((Character) obj);
        } else if (obj instanceof Boolean) {
            return (T)Boolean.valueOf((Boolean) obj);
        } else if (obj instanceof ArrayList<?>) {
            return (T) arrayListHandler((ArrayList<?>) obj);
        } else if (obj instanceof HashMap<?, ?>) {
            return (T) mapHandler((Map<?, ?>) obj);
        } else if (obj instanceof ConcurrentHashMap<?, ?>) {
            return (T) concurrentMapHandler((Map<?, ?>) obj);
        } else if (obj instanceof TreeMap<?, ?>) {
            return (T) treeMapHandler((Map<?, ?>) obj);
        } else if (obj instanceof LinkedList<?>) {
            return (T) linkedListHandler((LinkedList<?>) obj);
        } else if (obj instanceof HashSet<?>) {
            return (T) hashSetHandler((HashSet<?>) obj);
        } else if (isPrimitiveArray(obj)) {
            return getPrimitiveArray(obj);
        }

        T finObj = null;
        Class rezClass = obj.getClass();
        rezClass.cast(finObj);
        try {
            Constructor<T> constructor = getCompleteConstructor(rezClass);
            finObj = (T) constructor.newInstance(getParamsObjForConstructor(rezClass));
            copyFields(rezClass, obj, finObj, parrent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finObj;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ArrayList<?> arrayListHandler(ArrayList<?> obj) {
        ArrayList srcList = obj;
        ArrayList finList = new ArrayList();
        for (int i = 0; i < srcList.size(); i++) {
            finList.add(CopyUtils.deepCopy(srcList.get(i)));
        }
        return finList;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <K, V> Map<K, V> mapHandler(Map<K, V> obj) {
        Map<K, V> src = obj;
        Map<K, V> fin = new HashMap<K, V>();
        for (Map.Entry entry : src.entrySet()) {
            K key = (K) CopyUtils.deepCopy(entry.getKey());
            V value = (V) CopyUtils.deepCopy(entry.getValue());
            fin.put(key, value);
        }
        return fin;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <K, V> Map<K, V> concurrentMapHandler(Map<K, V> obj) {
        Map<K, V> src = obj;
        Map<K, V> fin = new ConcurrentHashMap<K, V>();
        for (Map.Entry entry : src.entrySet()) {
            K key = (K) CopyUtils.deepCopy(entry.getKey());
            V value = (V) CopyUtils.deepCopy(entry.getValue());
            fin.put(key, value);
        }
        return fin;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <K, V> Map<K, V> treeMapHandler(Map<K, V> obj) {
        Map<K, V> src = obj;
        Map<K, V> fin = new TreeMap<K, V>();
        for (Map.Entry entry : src.entrySet()) {
            K key = (K) CopyUtils.deepCopy(entry.getKey());
            V value = (V) CopyUtils.deepCopy(entry.getValue());
            fin.put(key, value);
        }
        return fin;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static LinkedList<?> linkedListHandler(LinkedList<?> obj) {
        LinkedList srcList = obj;
        LinkedList finList = new LinkedList<>();
        for (int i = 0; i < srcList.size(); i++) {
            finList.add(CopyUtils.deepCopy(srcList.get(i)));
        }
        return finList;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static HashSet<?> hashSetHandler(HashSet<?> obj) {
        HashSet srcList = obj;
        HashSet finList = new HashSet<>();
        for (Object o : srcList) {
            finList.add(CopyUtils.deepCopy(o));
        }
        return finList;
    }


    private static boolean isPrimitiveArray(Object obj) {
        if (obj instanceof byte[] ||
                obj instanceof short[] ||
                obj instanceof int[] ||
                obj instanceof long[] ||
                obj instanceof float[] ||
                obj instanceof double[] ||
                obj instanceof char[] ||
                obj instanceof boolean[]) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isPrimitiveArray(String type) {
        if ("byte[]".equals(type) ||
                "short[]".equals(type) ||
                "int[]".equals(type) ||
                "long[]".equals(type) ||
                "float[]".equals(type) ||
                "double[]".equals(type) ||
                "char[]".equals(type) ||
                "boolean[]".equals(type)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPrimitiveArray(T obj) {
        if (obj instanceof int[]) {
            int[] arr = new int[((int[]) obj).length];
            for (int i = 0; i < ((int[]) obj).length; i++) {
                arr[i] = ((int[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof byte[]) {
            byte[] arr = new byte[((byte[]) obj).length];
            for (int i = 0; i < ((byte[]) obj).length; i++) {
                arr[i] = ((byte[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof short[]) {
            short[] arr = new short[((short[]) obj).length];
            for (int i = 0; i < ((short[]) obj).length; i++) {
                arr[i] = ((short[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof long[]) {
            long[] arr = new long[((long[]) obj).length];
            for (int i = 0; i < ((long[]) obj).length; i++) {
                arr[i] = ((long[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof float[]) {
            float[] arr = new float[((float[]) obj).length];
            for (int i = 0; i < ((float[]) obj).length; i++) {
                arr[i] = ((float[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof double[]) {
            double[] arr = new double[((double[]) obj).length];
            for (int i = 0; i < ((double[]) obj).length; i++) {
                arr[i] = ((double[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof char[]) {
            char[] arr = new char[((char[]) obj).length];
            for (int i = 0; i < ((char[]) obj).length; i++) {
                arr[i] = ((char[]) obj)[i];
            }
            return (T) arr;
        } else if (obj instanceof boolean[]) {
            boolean[] arr = new boolean[((boolean[]) obj).length];
            for (int i = 0; i < ((boolean[]) obj).length; i++) {
                arr[i] = ((boolean[]) obj)[i];
            }
            return (T) arr;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPrimitiveArray(T obj, String type) {
        if ("int[]".equals(type)) {
            int[] arr = new int[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("byte[]".equals(type)) {
            byte[] arr = new byte[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("short[]".equals(type)) {
            short[] arr = new short[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("long[]".equals(type)) {
            long[] arr = new long[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("float[]".equals(type)) {
            float[] arr = new float[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("double[]".equals(type)) {
            double[] arr = new double[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("char[]".equals(type)) {
            char[] arr = new char[1];
            arr[0] = 0;
            return (T) arr;
        } else if ("boolean[]".equals(type)) {
            boolean[] arr = new boolean[1];
            arr[0] = false;
            return (T) arr;
        }
        return null;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Constructor getCompleteConstructor(Class ourClass)
            throws NoSuchMethodException, SecurityException {
        Constructor constructor = null;
        Class[] params = new Class[ourClass.getDeclaredConstructors()[0].getParameterTypes().length];
        for (int i = 0; i < ourClass.getDeclaredConstructors()[0].getParameterTypes().length; i++) {
            params[i] = ourClass.getDeclaredConstructors()[0].getParameterTypes()[i];
        }
        constructor = ourClass.getConstructor(params);
        constructor.setAccessible(true);
        return constructor;
    }

    @SuppressWarnings("rawtypes")
    private static Object[] getParamsObjForConstructor(Class ourClass)
            throws NoSuchMethodException, SecurityException {
        Constructor constuctor = null;
        constuctor = ourClass.getDeclaredConstructors()[0];
        constuctor.setAccessible(true);
        Object[] objParams = new Object[constuctor.getParameterTypes().length];
        for (int i = 0; i < constuctor.getParameterTypes().length; i++) {
            String fieldType = constuctor.getParameterTypes()[i].toString();
            if ("int".equalsIgnoreCase(fieldType) ||
                    "double".toString().equalsIgnoreCase(fieldType) ||
                    "float".equalsIgnoreCase(fieldType) ||
                    "byte".toString().equalsIgnoreCase(fieldType) ||
                    "char".equalsIgnoreCase(fieldType) ||
                    "long".equalsIgnoreCase(fieldType)) {
                objParams[i] = 0;
            } else if ("boolean".equalsIgnoreCase(fieldType)) {
                objParams[i] = false;
            } else if (isPrimitiveArray(constuctor.getParameterTypes()[i].getCanonicalName())) {
                objParams[i] = getPrimitiveArray(constuctor.getParameterTypes()[i],
                        constuctor.getParameterTypes()[i].getCanonicalName()
                );
            } else {
                objParams[i] = null;
            }
        }
        return objParams;
    }

    @SuppressWarnings("rawtypes")
    private static <T> void copyFields(Class ourClass, T srcObj, T finObj)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field[] fields = ourClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Field modField = Field.class.getDeclaredField("modifiers");
            modField.setAccessible(true);
            modField.setInt(fields[i], fields[i].getModifiers() & ~Modifier.FINAL);
            String fieldType = fields[i].getType().toString();
            if ("int".equalsIgnoreCase(fieldType) ||
                    "double".equalsIgnoreCase(fieldType) ||
                    "float".equalsIgnoreCase(fieldType) ||
                    "byte".equalsIgnoreCase(fieldType) ||
                    "char".equalsIgnoreCase(fieldType) ||
                    "boolean".equalsIgnoreCase(fieldType) ||
                    "short".equalsIgnoreCase(fieldType) ||
                    "long".equalsIgnoreCase(fieldType)) {
                fields[i].set(finObj, fields[i].get(srcObj));
            } else {
                fields[i].set(finObj, CopyUtils.deepCopy(fields[i].get(srcObj), finObj));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static <T> void copyFields(Class ourClass, T srcObj, T finObj, Object parent)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field[] fields = ourClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Field modField = Field.class.getDeclaredField("modifiers");
            modField.setAccessible(true);
            modField.setInt(fields[i], fields[i].getModifiers() & ~Modifier.FINAL);
            String fieldType = fields[i].getType().toString();
            if ("int".equalsIgnoreCase(fieldType) ||
                    "double".equalsIgnoreCase(fieldType) ||
                    "float".equalsIgnoreCase(fieldType) ||
                    "byte".equalsIgnoreCase(fieldType) ||
                    "char".equalsIgnoreCase(fieldType) ||
                    "boolean".equalsIgnoreCase(fieldType) ||
                    "short".equalsIgnoreCase(fieldType) ||
                    "long".equalsIgnoreCase(fieldType)) {
                fields[i].set(finObj, fields[i].get(srcObj));
            } else {
                if (fields[i].get(srcObj).toString().equals(parent.toString())) {
                    fields[i].set(finObj, fields[i].get(srcObj));
                } else {
                    fields[i].set(finObj, CopyUtils.deepCopy(fields[i].get(srcObj), finObj));
                }
            }
        }
    }

    static void setFinalStaticField(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    public static Object copyProperties(Object target, Object orig) {
        if (target == null || orig == null) {
            return target;
        }

        PropertyDescriptor[] destDesc = PropertyUtils.getPropertyDescriptors(target);
        try {
            for (int i = 0; i < destDesc.length; i++) {
                Class destType = destDesc[i].getPropertyType();
                Class origType = PropertyUtils.getPropertyType(orig, destDesc[i].getName());
                if (destType != null && destType.equals(origType) && !destType.equals(Class.class)) {
                    if (!Collection.class.isAssignableFrom(origType)) {
                        try {
                            Object value = PropertyUtils.getProperty(orig, destDesc[i].getName());
                            PropertyUtils.setProperty(target, destDesc[i].getName(), value);
                        } catch (Exception ex) {
                        }
                    }
                }
            }

            return target;
        } catch (Exception ex) {
            throw new CopyException(ex);
        }
    }

    public static Object copyProperties(Object dest, Object orig, String[] ignores) {
        if (dest == null || orig == null) {
            return dest;
        }

        PropertyDescriptor[] destDesc = PropertyUtils.getPropertyDescriptors(dest);
        try {
            for (int i = 0; i < destDesc.length; i++) {
                if (contains(ignores, destDesc[i].getName())) {
                    continue;
                }

                Class destType = destDesc[i].getPropertyType();
                Class origType = PropertyUtils.getPropertyType(orig, destDesc[i].getName());
                if (destType != null && destType.equals(origType) && !destType.equals(Class.class)) {
                    if (!Collection.class.isAssignableFrom(origType)) {
                        Object value = PropertyUtils.getProperty(orig, destDesc[i].getName());
                        PropertyUtils.setProperty(dest, destDesc[i].getName(), value);
                    }
                }
            }

            return dest;
        } catch (Exception ex) {
            throw new CopyException(ex);
        }
    }

    static boolean contains(String[] ignores, String name) {
        boolean ignored = false;
        for (int j = 0; ignores != null && j < ignores.length; j++) {
            if (ignores[j].equals(name)) {
                ignored = true;
                break;
            }
        }
        return ignored;
    }
}
