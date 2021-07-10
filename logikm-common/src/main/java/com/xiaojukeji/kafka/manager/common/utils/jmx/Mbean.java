package com.xiaojukeji.kafka.manager.common.utils.jmx;

/**
 * Mbean的对象封装
 * @author tukun
 * @date 2015/11/9.
 */
public class Mbean {
    /**
     * mbean的对象名称
     */
    private String objectName;

    /**
     * mbean对象被监控的属性名称
     */
    private String property;

    /**
     * mbean对象被监控的属性值对象类型
     */
    private Class propertyClass;

    public Mbean(String objectName, String property, Class propertyClass) {
        this.objectName = objectName;
        this.property = property;
        this.propertyClass = propertyClass;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Class getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(Class propertyClass) {
        this.propertyClass = propertyClass;
    }

    @Override
    public String toString() {
        return "Mbean{" +
                "objectName='" + objectName + '\'' +
                ", property='" + property + '\'' +
                ", propertyClass=" + propertyClass +
                '}';
    }
}
