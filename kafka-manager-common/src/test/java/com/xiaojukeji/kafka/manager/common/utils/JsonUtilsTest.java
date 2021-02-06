package com.xiaojukeji.kafka.manager.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonUtilsTest {
    @Test
    public void testMapToJsonString() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        map.put("int", 1);
        String expectRes = "{\"key\":\"value\",\"int\":1}";
        Assert.assertEquals(expectRes, JsonUtils.toJSONString(map));
    }
}
