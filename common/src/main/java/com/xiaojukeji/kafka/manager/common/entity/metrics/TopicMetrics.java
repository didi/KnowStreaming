package com.xiaojukeji.kafka.manager.common.entity.metrics;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.annotations.FieldSelector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TopicMetrics extends BaseMetrics {
    /**
     * 集群ID
     */
    private Long clusterId;

    /**
     * Topic名称
     */
    private String topicName;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    private static void initialization(Field[] fields){
        for(Field field : fields){
            FieldSelector annotation = field.getAnnotation(FieldSelector.class);
            if(annotation ==null){
                continue;
            }
            String fieldName;
            if("".equals(annotation.name())){
                String name = field.getName();
                fieldName = name.substring(0,1).toUpperCase()+name.substring(1);
            }else{
                fieldName = annotation.name();
            }
            for(int type: annotation.types()){
                List<String> list = Constant.TOPIC_METRICS_TYPE_MBEAN_NAME_MAP.getOrDefault(type, new ArrayList<>());
                list.add(fieldName);
                Constant.TOPIC_METRICS_TYPE_MBEAN_NAME_MAP.put(type, list);
            }
        }
    }

    public static List<String> getFieldNameList(int type){
        synchronized (TopicMetrics.class) {
            if (Constant.TOPIC_METRICS_TYPE_MBEAN_NAME_MAP.isEmpty()) {
                initialization(TopicMetrics.class.getDeclaredFields());
                initialization(BaseMetrics.class.getDeclaredFields());
            }
        }
        return Constant.TOPIC_METRICS_TYPE_MBEAN_NAME_MAP.get(type);
    }

}
