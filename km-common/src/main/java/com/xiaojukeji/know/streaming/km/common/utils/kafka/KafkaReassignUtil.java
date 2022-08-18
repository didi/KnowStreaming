package com.xiaojukeji.know.streaming.km.common.utils.kafka;

import kafka.admin.ReassignPartitionsCommand;
import org.apache.kafka.common.TopicPartition;
import scala.collection.Seq;
import scala.jdk.javaapi.CollectionConverters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KafkaReassignUtil {
    public static String formatAsReassignmentJson(Map<TopicPartition, List<Integer>> partitionsToBeReassigned) {
        Map<TopicPartition, Seq<Object>> scalaPartitionsToBeReassigned = new HashMap<>();
        partitionsToBeReassigned.entrySet().forEach(
                entry -> scalaPartitionsToBeReassigned.put(
                        entry.getKey(),
                        CollectionConverters.asScala(entry.getValue().stream().map(elem -> (Object)elem).collect(Collectors.toList())))
        );

        return ReassignPartitionsCommand.formatAsReassignmentJson(CollectionConverters.asScala(scalaPartitionsToBeReassigned), new scala.collection.mutable.HashMap<>());
    }

    private KafkaReassignUtil() {
    }
}
