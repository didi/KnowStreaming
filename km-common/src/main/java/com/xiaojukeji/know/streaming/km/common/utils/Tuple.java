package com.xiaojukeji.know.streaming.km.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @Author: D10865
 * @Description:
 * @Date: Create on 2018/5/29 下午4:08
 * @Modified By
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Data
public class Tuple<T, V> {
    private T v1;
    private V v2;

    public Tuple(){}

    public Tuple(T v1, V v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public T v1() {
        return v1;
    }

    public Tuple<T, V> setV1(T v1) {
        this.v1 = v1;
        return this;
    }

    public V v2() {
        return v2;
    }

    public Tuple<T, V> setV2(V v2) {
        this.v2 = v2;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (v1 != null ? !v1.equals(tuple.v1) : tuple.v1 != null) {return false;}
        return v2 != null ? v2.equals(tuple.v2) : tuple.v2 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}
